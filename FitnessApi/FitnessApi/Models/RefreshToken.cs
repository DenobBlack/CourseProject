using System;
using System.Collections.Generic;
using System.Text.Json.Serialization;

namespace FitnessApi.Models;

public partial class RefreshToken
{
    public int Id { get; set; }

    public int UserId { get; set; }

    public string Token { get; set; } = null!;

    public DateTime ExpiresAt { get; set; }

    public DateTime? RevokedAt { get; set; }
    [JsonIgnore]
    public virtual User User { get; set; } = null!;
}
