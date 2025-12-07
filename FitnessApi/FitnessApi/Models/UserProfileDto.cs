namespace FitnessApi.Models
{
    public class UserProfileDto
    {
        public int UserId { get; set; }

        public string Username { get; set; } = null!;

        public string Email { get; set; } = null!;

        public string? Gender { get; set; }

        public DateOnly BirthDate { get; set; }

        public int HeightCm { get; set; }

        public float WeightKg { get; set; }
    }
}
