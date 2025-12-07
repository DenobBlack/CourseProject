using System;
using System.Collections.Generic;
using System.Text.Json.Serialization;

namespace FitnessApi.Models;

public partial class UserWaterLog
{
    public int Id { get; set; }

    public int UserId { get; set; }

    public float AmountL { get; set; }

    public DateTime RecordedAt { get; set; }
    [JsonIgnore]
    public virtual User? User { get; set; }
}
