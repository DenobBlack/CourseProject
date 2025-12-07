using System.ComponentModel.DataAnnotations;

namespace FitnessApi.Models
{
    public class UserDto
    {
        [Required(ErrorMessage = "Email обязателен")]
        [EmailAddress(ErrorMessage = "Некорректный формат email")]
        [MaxLength(100)]
        public string Email { get; set; } = null!;
        [Required(ErrorMessage = "Пароль обязателен")]
        public string Password { get; set; } = null!;
    }
}
