using System;
using System.Collections.Generic;
using System.Text.Json.Serialization;

namespace FitnessApi.Models;

public partial class WorkoutExercise
{
    public int WorkoutId { get; set; }

    public int ExerciseId { get; set; }

    public int? Sets { get; set; }

    public int? Reps { get; set; }

    public float? WeightKg { get; set; }
    [JsonIgnore]
    public virtual Exercise Exercise { get; set; } = null!;
    [JsonIgnore]
    public virtual Workout Workout { get; set; } = null!;
}
