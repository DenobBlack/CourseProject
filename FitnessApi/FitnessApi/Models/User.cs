using System;
using System.Collections.Generic;
using System.Text.Json.Serialization;

namespace FitnessApi.Models;

public partial class User
{
    public int UserId { get; set; }

    public string Username { get; set; } = null!;

    public string Email { get; set; } = null!;

    public string PasswordHash { get; set; } = null!;

    public string? Gender { get; set; }

    public DateOnly BirthDate { get; set; }

    public int HeightCm { get; set; }

    public float WeightKg { get; set; }

    public DateTime? CreatedAt { get; set; }

    public short RoleId { get; set; }

    public string Name { get; set; } = null!;

    public string LastName { get; set; } = null!;

    public string? Patronymic { get; set; }
    [JsonIgnore]
    public virtual ICollection<Goal> Goals { get; set; } = new List<Goal>();
    [JsonIgnore]
    public virtual ICollection<RefreshToken> RefreshTokens { get; set; } = new List<RefreshToken>();
    [JsonIgnore]
    public virtual Role? Role { get; set; }
    [JsonIgnore]
    public virtual ICollection<UserMeal> UserMeals { get; set; } = new List<UserMeal>();
    [JsonIgnore]
    public virtual ICollection<UserWaterLog> UserWaterLogs { get; set; } = new List<UserWaterLog>();
    [JsonIgnore]
    public virtual ICollection<WorkoutCompletion> WorkoutCompletions { get; set; } = new List<WorkoutCompletion>();
    [JsonIgnore]
    public virtual ICollection<Workout> Workouts { get; set; } = new List<Workout>();
}
