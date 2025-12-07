using System;
using System.Collections.Generic;
using System.Text.Json.Serialization;

namespace FitnessApi.Models;

public partial class WorkoutCompletion
{
    public int CompletionId { get; set; }

    public int WorkoutId { get; set; }

    public int UserId { get; set; }

    public DateTime CompletedAt { get; set; }
    [JsonIgnore]
    public virtual User User { get; set; } = null!;
    [JsonIgnore]
    public virtual Workout Workout { get; set; } = null!;

    public virtual ICollection<WorkoutCompletionExercise> WorkoutCompletionExercises { get; set; } = new List<WorkoutCompletionExercise>();
}
