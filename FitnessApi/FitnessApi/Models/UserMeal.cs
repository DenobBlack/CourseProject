using System;
using System.Collections.Generic;
using System.Text.Json.Serialization;

namespace FitnessApi.Models;

public partial class UserMeal
{
    public int UserId { get; set; }

    public int MealId { get; set; }

    public DateTime MealTime { get; set; }

    public float? PortionSize { get; set; }
    [JsonIgnore]
    public virtual Meal Meal { get; set; } = null!;
    [JsonIgnore]
    public virtual User User { get; set; } = null!;
}
