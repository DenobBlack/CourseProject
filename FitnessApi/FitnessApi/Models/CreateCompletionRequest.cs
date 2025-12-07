namespace FitnessApi.Models
{
    public class CreateCompletionRequest
    {
        public DateTime? CompletedAt { get; set; }
        public List<ExerciseCompletionDto> Exercises { get; set; }
    }

    public class ExerciseCompletionDto
    {
        public int ExerciseId { get; set; }
        public float Weight { get; set; }
    }
}
