using FitnessApi.DataContext;
using FitnessApi.Models;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace FitnessApi.Controllers
{
    [Authorize]
    [Route("api/[controller]")]
    [ApiController]
    public class WorkoutExercisesController : ControllerBase
    {
        private readonly FitnessDbContext _context;

        public WorkoutExercisesController(FitnessDbContext context)
        {
            _context = context;
        }

        // GET: api/WorkoutExercises/5/3
        [HttpGet("{workoutId:int}")]
        public async Task<ActionResult<List<WorkoutExercise>>> GetWorkoutExercise(int workoutId)
        {
            var workoutExercise = await _context.WorkoutExercises
                .Where(we => we.WorkoutId == workoutId).ToListAsync();

            if (workoutExercise == null)
                return NotFound();

            return workoutExercise;
        }
        // DELETE: api/WorkoutExercises/5/3
        [HttpDelete("{workoutId:int}/{exerciseId:int}")]
        public async Task<IActionResult> DeleteWorkoutExercise(int workoutId, int exerciseId)
        {
            var item = await _context.WorkoutExercises
                .FirstOrDefaultAsync(we => we.WorkoutId == workoutId && we.ExerciseId == exerciseId);

            if (item == null)
                return NotFound("Упражнение в тренировке не найдено");

            _context.WorkoutExercises.Remove(item);
            await _context.SaveChangesAsync();

            return Ok("Удалено");
        }
        [HttpPost]
        public async Task<ActionResult<WorkoutExercise>> PostWorkoutExercise(WorkoutExercise ex)
        {
            var workout = await _context.Workouts
                .FirstOrDefaultAsync(w => w.WorkoutId == ex.WorkoutId);
            if (workout == null)
                return BadRequest("Тренировка не найдена");
            var exercise = await _context.Exercises
                .FirstOrDefaultAsync(e => e.ExerciseId == ex.ExerciseId);

            if (exercise == null)
                return BadRequest("Упражнение не найдено");

            // Добавляем
            _context.WorkoutExercises.Add(ex);
            await _context.SaveChangesAsync();

            return Ok(ex);

        }
        [HttpPatch("{workoutId:int}/{exerciseId:int}/weight")]
        public async Task<IActionResult> UpdateWorkoutWeight(int workoutId, int exerciseId, [FromBody] float actualWeight)
        {
            var item = await _context.WorkoutExercises
                .FirstOrDefaultAsync(we =>
                    we.WorkoutId == workoutId && we.ExerciseId == exerciseId);

            if (item == null)
                return NotFound("Упражнение в тренировке не найдено");

            item.WeightKg = actualWeight;

            await _context.SaveChangesAsync();

            return Ok(item);
        }

        [HttpPut("{workoutId:int}/{exerciseId:int}")]
        public async Task<IActionResult> UpdateWorkoutExercise(int workoutId,int exerciseId,[FromBody] WorkoutExercise updated)
        {
            var item = await _context.WorkoutExercises
                .FirstOrDefaultAsync(we =>
                    we.WorkoutId == workoutId && we.ExerciseId == exerciseId);

            if (item == null)
                return NotFound("Упражнение в тренировке не найдено");

            item.Sets = updated.Sets;
            item.Reps = updated.Reps;
            item.WeightKg = updated.WeightKg;

            await _context.SaveChangesAsync();

            return Ok(item);
        }
    }
}


