using FitnessApi.DataContext;
using FitnessApi.Models;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;

namespace FitnessApi.Services
{
    public class JwtService
    {
        private readonly IConfiguration _config;
        private readonly FitnessDbContext _context;
        public static readonly Dictionary<string, string> roles = new();
        public JwtService(IConfiguration config, FitnessDbContext context)
        {
            roles["Администратор"] = "Administrator";
            roles["Тренер"] = "Coach";
            roles["Пользователь"] = "User";
            _config = config;
            _context = context;
        }

        public string GenerateToken(User user)
        {
            var jwtSection = _config.GetSection("Jwt");
            var key = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(jwtSection["Key"]));
            var creds = new SigningCredentials(key, SecurityAlgorithms.HmacSha256);

            List<Claim> claims = new List<Claim>(){
                    new Claim(ClaimTypes.NameIdentifier,user.UserId.ToString()),
                    new Claim(ClaimTypes.Name, user.Username),
                    new Claim(ClaimTypes.Role, roles[_context.Roles.Find(user.RoleId).Name])
            };

            var token = new JwtSecurityToken(
                issuer: jwtSection["Issuer"],
                audience: jwtSection["Audience"],
                claims: claims,
                expires: DateTime.Now.AddMinutes(Convert.ToDouble(jwtSection["ExpiresInMinutes"])),
                signingCredentials: creds
            );

            return new JwtSecurityTokenHandler().WriteToken(token);
        }
    }
}
