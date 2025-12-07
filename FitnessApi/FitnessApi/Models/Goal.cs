using System;
using System.Collections.Generic;
using System.Text.Json.Serialization;

namespace FitnessApi.Models;

public partial class Goal
{
    public int GoalId { get; set; }

    public int UserId { get; set; }

    public string GoalType { get; set; } = null!;

    public float? TargetWeight { get; set; }

    public DateOnly? TargetDate { get; set; }

    public string? Description { get; set; }
    [JsonIgnore]
    public virtual User? User { get; set; }
}
