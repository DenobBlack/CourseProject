using FitnessApi.DataContext;
using FitnessApi.Models;
using FitnessApi.Services;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using NuGet.Common;
using System.Text;

namespace FitnessApi.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class AuthController : ControllerBase
    {
        private readonly FitnessDbContext _context;
        private readonly JwtService _jwt;
        private readonly TokenService _tokenService;

        public AuthController(FitnessDbContext context, JwtService jwt, TokenService tokenService)
        {
            _context = context;
            _jwt = jwt;
            _tokenService = tokenService;
        }

        [HttpPost("register")]
        public async Task<IActionResult> Register(User user)
        {
            if (await _context.Users.AnyAsync(u => u.Email == user.Email || u.Username == user.Username))
                return BadRequest(new { message = "Такой пользователь уже зарегистрирован" });

            user.PasswordHash = HashPassword(user.PasswordHash);
            user.CreatedAt = DateTime.Now;
            user.RoleId = 2;
            user.Role = await _context.Roles.FirstOrDefaultAsync(r => r.RoleId == user.RoleId);

            _context.Users.Add(user);
            await _context.SaveChangesAsync();

            var accessToken = _jwt.GenerateToken(user);
            var refreshToken = await _tokenService.CreateRefreshTokenAsync(user.UserId);
            HttpContext.Session.SetString("UserToken", accessToken);
            return Ok(new
            {
                message = "Регистрация успешна",
                accessToken,
                refreshToken = refreshToken.Token
            });
        }

        [HttpPost("login")]
        public async Task<IActionResult> Login([FromBody] UserDto creds)
        {
            var user = await _context.Users.Include(u => u.Role)
                .FirstOrDefaultAsync(u => u.Email == creds.Email);

            if (user == null || !VerifyPassword(creds.Password, user.PasswordHash))
                return Unauthorized(new { message = "Неверный email или пароль" });

            var accessToken = _jwt.GenerateToken(user);
            var refreshToken = await _tokenService.CreateRefreshTokenAsync(user.UserId);
            HttpContext.Session.SetString("UserToken", accessToken);
            return Ok(new
            {
                message = "Успешный вход",
                accessToken,
                refreshToken = refreshToken.Token
            });
        }

        [HttpPost("refresh")]
        public async Task<IActionResult> Refresh([FromBody] TokenRequest request)
        {
            var existing = await _tokenService.GetValidTokenAsync(request.RefreshToken);
            if (existing == null)
                return Unauthorized(new { message = "Неверный или просроченный refresh-токен" });

            var user = existing.User;
            var newAccessToken = _jwt.GenerateToken(user);
            var newRefreshToken = await _tokenService.CreateRefreshTokenAsync(user.UserId);
            HttpContext.Session.SetString("UserToken", newAccessToken);
            await _tokenService.RevokeTokenAsync(request.RefreshToken);

            return Ok(new
            {
                accessToken = newAccessToken,
                refreshToken = newRefreshToken.Token
            });
        }
        private string HashPassword(string password)
        {
            using var sha = System.Security.Cryptography.SHA256.Create();
            var bytes = sha.ComputeHash(Encoding.UTF8.GetBytes(password));
            return Convert.ToBase64String(bytes);
        }

        private bool VerifyPassword(string input, string hashed)
        {
            return HashPassword(input) == hashed;
        }
    }
}
