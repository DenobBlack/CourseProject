using System;
using System.Collections.Generic;
using System.Text.Json.Serialization;

namespace FitnessApi.Models;

public partial class Exercise
{
    public int ExerciseId { get; set; }

    public string Name { get; set; } = null!;

    public string? MuscleGroup { get; set; }

    public string? Description { get; set; }

    public string? Difficulty { get; set; }

    public string? Equipment { get; set; }

    public string? PreviewImage { get; set; }

    public string? TutorialImage { get; set; }
    [JsonIgnore]
    public virtual ICollection<WorkoutCompletionExercise> WorkoutCompletionExercises { get; set; } = new List<WorkoutCompletionExercise>();
    [JsonIgnore]
    public virtual ICollection<WorkoutExercise> WorkoutExercises { get; set; } = new List<WorkoutExercise>();
}
