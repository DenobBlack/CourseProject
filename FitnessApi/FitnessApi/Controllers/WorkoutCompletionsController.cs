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
    [Route("api")]
    public class WorkoutCompletionsController : ControllerBase
    {
        private readonly FitnessDbContext _context;

        public WorkoutCompletionsController(FitnessDbContext context)
        {
            _context = context;
        }

        [HttpPost("Workouts/{workoutId}/completions")] public async Task<IActionResult> CreateCompletion(int workoutId, [FromBody] CreateCompletionRequest req) 
        { 
            var workout = await _context.Workouts.FindAsync(workoutId); 
            if (workout == null) 
                return NotFound(); 
            int userId = workout.UserId; 
            var completion = new WorkoutCompletion 
            { 
                WorkoutId = workoutId, 
                UserId = userId, 
                CompletedAt = req.CompletedAt ?? DateTime.UtcNow 
            }; 
            _context.WorkoutCompletions.Add(completion); 
            await _context.SaveChangesAsync(); 
            foreach (var item in req.Exercises) 
            { 
                var ex = new WorkoutCompletionExercise 
                { 
                    CompletionId = completion.CompletionId, 
                    ExerciseId = item.ExerciseId, 
                    Weight = item.Weight 
                }; 
                _context.WorkoutCompletionExercises.Add(ex); 
            } 
            await _context.SaveChangesAsync(); 
            return Ok(new { completion.CompletionId, completion.WorkoutId, completion.CompletedAt, Exercises = req.Exercises }); }


        [HttpGet("Workouts/{workoutId}/completions")]
        public async Task<IActionResult> GetCompletionsForWorkout(int workoutId)
        {
            var list = await _context.WorkoutCompletions
                .Where(c => c.WorkoutId == workoutId)
                .Select(c => new
                {
                    c.CompletionId,
                    c.UserId,
                    c.CompletedAt,
                    Exercises = c.WorkoutCompletionExercises
                        .Select(e => new
                        {
                            e.ExerciseId,
                            e.Weight
                        })
                        .ToList()
                })
                .OrderByDescending(c => c.CompletedAt)
                .ToListAsync();

            return Ok(list);
        }

        [HttpGet("Users/{userId}/workout-completions")]
        public async Task<IActionResult> GetCompletionsForUser(int userId)
        {
            var list = await _context.WorkoutCompletions
                .Where(c => c.UserId == userId)
                .Select(c => new
                {
                    c.CompletionId,
                    c.WorkoutId,
                    c.CompletedAt,
                    Exercises = c.WorkoutCompletionExercises
                        .Select(e => new
                        {
                            e.ExerciseId,
                            e.Weight
                        })
                        .ToList()
                })
                .OrderByDescending(c => c.CompletedAt)
                .ToListAsync();

            return Ok(list);
        }
    }
}
