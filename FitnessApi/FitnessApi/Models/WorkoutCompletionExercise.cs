using System;
using System.Collections.Generic;
using System.Text.Json.Serialization;

namespace FitnessApi.Models;

public partial class WorkoutCompletionExercise
{
    public int Id { get; set; }

    public int CompletionId { get; set; }

    public int ExerciseId { get; set; }

    public float? Weight { get; set; }
    [JsonIgnore]
    public virtual WorkoutCompletion Completion { get; set; } = null!;
    [JsonIgnore]
    public virtual Exercise Exercise { get; set; } = null!;
}
