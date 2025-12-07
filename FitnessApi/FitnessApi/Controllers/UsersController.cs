using FitnessApi.DataContext;
using FitnessApi.Models;
using FitnessApi.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace FitnessApi.Controllers
{
    
    [Route("api/[controller]")]
    [ApiController]
    public class UsersController : ControllerBase
    {
        private readonly FitnessDbContext _context;

        public UsersController(FitnessDbContext context)
        {
            _context = context;
        }

        [Authorize(Roles = "Administrator")]
        [HttpGet]
        public async Task<IActionResult> GetAllUsers()
        {
            var users = await _context.Users.ToListAsync();
            return Ok(users);
        }
        [Authorize(Roles = "Administrator")]
        [HttpGet("{id}")]
        public async Task<IActionResult> GetUserById(int id)
        {
            var user = await _context.Users.FindAsync(id);
            if (user == null)
                return NotFound(new { message = $"Пользователь {id} не найден" });
            return Ok(user);
        }
        [Authorize]
        [HttpGet("{id}/profile")]
        public async Task<IActionResult> GetUserProfile(int id)
        {
            var user = await _context.Users.FindAsync(id);
            if (user == null)
                return NotFound(new { message = $"Пользователь {id} не найден" });
            UserProfileDto userDto = new() { 
                Email= user.Email, 
                BirthDate = user.BirthDate, 
                Gender = user.Gender, 
                HeightCm = user.HeightCm, 
                UserId = user.UserId, 
                Username = user.Username, 
                WeightKg = user.WeightKg
            };
            return Ok(userDto);
        }
        [Authorize(Roles = "Administrator")]
        [HttpPost]
        public async Task<IActionResult> CreateUser([FromBody] User user)
        {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            _context.Users.Add(user);
            await _context.SaveChangesAsync();
            return CreatedAtAction(nameof(GetUserById), new { id = user.UserId }, user);
        }
        [Authorize(Roles = "Administrator")]
        [HttpPut("{id}")]
        public async Task<IActionResult> UpdateUser(string id, [FromBody] User updated)
        {
            int.TryParse(id, out int userId);
            if (userId != updated.UserId)
                return BadRequest(new { message = "ID не совпадают" });

            var user = await _context.Users.FindAsync(userId);
            if (user == null)
                return NotFound();

            user.Username = updated.Username;
            user.Email = updated.Email;
            user.PasswordHash = updated.PasswordHash;
            user.Gender = updated.Gender;
            user.BirthDate = updated.BirthDate;
            user.HeightCm = updated.HeightCm;
            user.WeightKg = updated.WeightKg;

            await _context.SaveChangesAsync();
            return Ok(user);
        }
        [Authorize(Roles = "Administrator")]
        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteUser(string id)
        {
            var user = await _context.Users.FindAsync(id);
            if (user == null)
                return NotFound();

            _context.Users.Remove(user);
            await _context.SaveChangesAsync();
            return Ok(new { message = "Пользователь удалён" });
        }
    }
}
