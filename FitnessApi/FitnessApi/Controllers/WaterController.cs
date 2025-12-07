using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Rendering;
using Microsoft.EntityFrameworkCore;
using FitnessApi.DataContext;
using FitnessApi.Models;
using Microsoft.AspNetCore.Authorization;

namespace FitnessApi.Controllers
{
    [Authorize]
    [ApiController]
    [Route("api/[controller]")]
    public class WaterController : ControllerBase
    {
        private readonly FitnessDbContext _context;
        public WaterController(FitnessDbContext context) => _context = context;

        public record AddWaterDto(int UserId, float Amount);

        [HttpGet("today/{userId}")]
        public async Task<IActionResult> GetToday(int userId)
        {
            var today = DateTime.UtcNow.Date;

            var totalMl = await _context.UserWaterLogs
                .Where(w => w.UserId == userId && w.RecordedAt.Date == today)
                .SumAsync(w => (int?)w.AmountL) ?? 0;

            return Ok(totalMl);
        }

        [HttpPost("add")]
        public async Task<IActionResult> AddWater([FromBody] AddWaterDto dto)
        {
            var log = new UserWaterLog
            {
                UserId = dto.UserId,
                AmountL = dto.Amount,
                RecordedAt = DateTime.UtcNow
            };
            _context.UserWaterLogs.Add(log);
            await _context.SaveChangesAsync();
            return Ok();
        }
    }
}
