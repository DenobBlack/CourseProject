using System;
using System.Collections.Generic;
using System.Text.Json.Serialization;

namespace FitnessApi.Models;

public partial class Meal
{
    public int MealId { get; set; }

    public string Name { get; set; } = null!;

    public int Calories { get; set; }

    public float Protein { get; set; }

    public float Fat { get; set; }

    public float Carbs { get; set; }

    public string? Description { get; set; }

    public string PreviewImage { get; set; } = null!;
    [JsonIgnore]
    public virtual ICollection<UserMeal> UserMeals { get; set; } = new List<UserMeal>();
}
