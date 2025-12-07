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
    public class WorkoutsController : ControllerBase
    {
        private readonly FitnessDbContext _context;
        public WorkoutsController(FitnessDbContext context) => _context = context;

        [HttpGet("{userId}")]
        public async Task<IActionResult> GetUserWorkouts(int userId)
        {
            var data = await _context.Workouts
                .Where(w => w.UserId == userId)
                .ToListAsync();

            return Ok(data);
        }
        [HttpPost]
        public async Task<IActionResult> CreateWorkoutAsync([FromBody] Workout workout)
        {
            _context.Workouts.Add(workout);
            await _context.SaveChangesAsync();
            return Ok(workout);
        }   
        [HttpPut("{id}/rename")]
        public async Task<IActionResult> RenameWorkout(int id, string name)
        {
            var workout = await _context.Workouts.FindAsync(id);
            if (workout == null) return NotFound();
            if (name.Length > 20) return BadRequest();
            workout.Name = name;
            await _context.SaveChangesAsync();
            return Ok();
        }
        [HttpDelete("{id}/exercises")]
        public async Task<IActionResult> ClearWorkoutExercises(int id)
        {
            var exercises = await _context.WorkoutExercises
                .Where(we => we.WorkoutId == id)
                .ToListAsync();

            if (!exercises.Any()) return NotFound();

            _context.WorkoutExercises.RemoveRange(exercises);
            await _context.SaveChangesAsync();
            return Ok();
        }
    }
}
