using System;
using System.Collections.Generic;
using System.Text.Json.Serialization;

namespace FitnessApi.Models;

public partial class Workout
{
    public int WorkoutId { get; set; }

    public int UserId { get; set; }

    public string Name { get; set; } = null!;

    public int? DurationMin { get; set; }
    [JsonIgnore]
    public virtual User User { get; set; } = null!;
    [JsonIgnore]
    public virtual ICollection<WorkoutCompletion> WorkoutCompletions { get; set; } = new List<WorkoutCompletion>();
    [JsonIgnore]
    public virtual ICollection<WorkoutExercise> WorkoutExercises { get; set; } = new List<WorkoutExercise>();
}
