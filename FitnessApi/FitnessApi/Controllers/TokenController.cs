using FitnessApi.DataContext;
using FitnessApi.Models;
using Microsoft.EntityFrameworkCore;

namespace FitnessApi.Services
{
    public class TokenService
    {
        private readonly FitnessDbContext _context;

        public TokenService(FitnessDbContext context)
        {
            _context = context;
        }

        public async Task<RefreshToken> CreateRefreshTokenAsync(int userId)
        {
            var token = Convert.ToBase64String(Guid.NewGuid().ToByteArray());

            var refresh = new RefreshToken
            {
                UserId = userId,
                Token = token,
                ExpiresAt = DateTime.UtcNow.AddDays(7)
            };

            _context.RefreshTokens.Add(refresh);
            await _context.SaveChangesAsync();
            return refresh;
        }

        public async Task<RefreshToken?> GetValidTokenAsync(string token)
        {
            return await _context.RefreshTokens
                .Include(t => t.User)
                .FirstOrDefaultAsync(t => t.Token == token && t.RevokedAt == null && t.ExpiresAt > DateTime.UtcNow);
        }

        public async Task RevokeTokenAsync(string token)
        {
            var refresh = await _context.RefreshTokens.FirstOrDefaultAsync(t => t.Token == token);
            if (refresh != null)
            {
                refresh.RevokedAt = DateTime.UtcNow;
                await _context.SaveChangesAsync();
            }
        }
    }
}
