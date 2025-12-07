using System;
using System.Collections.Generic;
using FitnessApi.Models;
using Microsoft.EntityFrameworkCore;
using Pomelo.EntityFrameworkCore.MySql.Scaffolding.Internal;

namespace FitnessApi.DataContext;

public partial class FitnessDbContext : DbContext
{
    public FitnessDbContext()
    {
    }

    public FitnessDbContext(DbContextOptions<FitnessDbContext> options)
        : base(options)
    {
    }

    public virtual DbSet<Exercise> Exercises { get; set; }

    public virtual DbSet<Goal> Goals { get; set; }

    public virtual DbSet<Meal> Meals { get; set; }

    public virtual DbSet<RefreshToken> RefreshTokens { get; set; }

    public virtual DbSet<Role> Roles { get; set; }

    public virtual DbSet<User> Users { get; set; }

    public virtual DbSet<UserMeal> UserMeals { get; set; }

    public virtual DbSet<UserWaterLog> UserWaterLogs { get; set; }

    public virtual DbSet<Workout> Workouts { get; set; }

    public virtual DbSet<WorkoutCompletion> WorkoutCompletions { get; set; }

    public virtual DbSet<WorkoutCompletionExercise> WorkoutCompletionExercises { get; set; }

    public virtual DbSet<WorkoutExercise> WorkoutExercises { get; set; }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder
            .UseCollation("utf8mb4_0900_ai_ci")
            .HasCharSet("utf8mb4");

        modelBuilder.Entity<Exercise>(entity =>
        {
            entity.HasKey(e => e.ExerciseId).HasName("PRIMARY");

            entity
                .ToTable("exercises")
                .UseCollation("utf8mb4_unicode_ci");

            entity.Property(e => e.ExerciseId).HasColumnName("exercise_id");
            entity.Property(e => e.Description)
                .HasColumnType("text")
                .HasColumnName("description");
            entity.Property(e => e.Difficulty)
                .HasDefaultValueSql("'новичок'")
                .HasColumnType("enum('новичок','средний','продвинутый')")
                .HasColumnName("difficulty");
            entity.Property(e => e.Equipment)
                .HasMaxLength(100)
                .HasColumnName("equipment");
            entity.Property(e => e.MuscleGroup)
                .HasMaxLength(50)
                .HasColumnName("muscle_group");
            entity.Property(e => e.Name)
                .HasMaxLength(100)
                .HasColumnName("name");
            entity.Property(e => e.PreviewImage)
                .HasMaxLength(100)
                .HasColumnName("previewImage");
            entity.Property(e => e.TutorialImage)
                .HasMaxLength(100)
                .HasColumnName("tutorialImage")
                .UseCollation("utf8mb3_general_ci")
                .HasCharSet("utf8mb3");
        });

        modelBuilder.Entity<Goal>(entity =>
        {
            entity.HasKey(e => e.GoalId).HasName("PRIMARY");

            entity
                .ToTable("goals")
                .UseCollation("utf8mb4_unicode_ci");

            entity.HasIndex(e => e.UserId, "user_id");

            entity.Property(e => e.GoalId).HasColumnName("goal_id");
            entity.Property(e => e.Description)
                .HasColumnType("text")
                .HasColumnName("description");
            entity.Property(e => e.GoalType)
                .HasColumnType("enum('weight_loss','muscle_gain','endurance','maintenance')")
                .HasColumnName("goal_type");
            entity.Property(e => e.TargetDate).HasColumnName("target_date");
            entity.Property(e => e.TargetWeight).HasColumnName("target_weight");
            entity.Property(e => e.UserId).HasColumnName("user_id");

            entity.HasOne(d => d.User).WithMany(p => p.Goals)
                .HasForeignKey(d => d.UserId)
                .HasConstraintName("goals_ibfk_1");
        });

        modelBuilder.Entity<Meal>(entity =>
        {
            entity.HasKey(e => e.MealId).HasName("PRIMARY");

            entity
                .ToTable("meals")
                .UseCollation("utf8mb4_unicode_ci");

            entity.Property(e => e.MealId).HasColumnName("meal_id");
            entity.Property(e => e.Calories).HasColumnName("calories");
            entity.Property(e => e.Carbs).HasColumnName("carbs");
            entity.Property(e => e.Description)
                .HasColumnType("text")
                .HasColumnName("description");
            entity.Property(e => e.Fat).HasColumnName("fat");
            entity.Property(e => e.Name)
                .HasMaxLength(100)
                .HasColumnName("name");
            entity.Property(e => e.PreviewImage)
                .HasMaxLength(100)
                .HasColumnName("previewImage");
            entity.Property(e => e.Protein).HasColumnName("protein");
        });

        modelBuilder.Entity<RefreshToken>(entity =>
        {
            entity.HasKey(e => e.Id).HasName("PRIMARY");

            entity
                .ToTable("refresh_tokens")
                .UseCollation("utf8mb4_unicode_ci");

            entity.HasIndex(e => e.UserId, "user_id");

            entity.Property(e => e.Id).HasColumnName("id");
            entity.Property(e => e.ExpiresAt)
                .HasColumnType("datetime")
                .HasColumnName("expires_at");
            entity.Property(e => e.RevokedAt)
                .HasColumnType("datetime")
                .HasColumnName("revoked_at");
            entity.Property(e => e.Token)
                .HasMaxLength(512)
                .HasColumnName("token");
            entity.Property(e => e.UserId).HasColumnName("user_id");

            entity.HasOne(d => d.User).WithMany(p => p.RefreshTokens)
                .HasForeignKey(d => d.UserId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("refresh_tokens_ibfk_1");
        });

        modelBuilder.Entity<Role>(entity =>
        {
            entity.HasKey(e => e.RoleId).HasName("PRIMARY");

            entity
                .ToTable("roles")
                .UseCollation("utf8mb4_unicode_ci");

            entity.HasIndex(e => e.Name, "name").IsUnique();

            entity.Property(e => e.RoleId)
                .ValueGeneratedNever()
                .HasColumnName("role_id");
            entity.Property(e => e.Name)
                .HasMaxLength(50)
                .HasColumnName("name");
        });

        modelBuilder.Entity<User>(entity =>
        {
            entity.HasKey(e => e.UserId).HasName("PRIMARY");

            entity
                .ToTable("users")
                .UseCollation("utf8mb4_unicode_ci");

            entity.HasIndex(e => e.Email, "email").IsUnique();

            entity.HasIndex(e => e.RoleId, "fk_role_user_idx");

            entity.HasIndex(e => e.Username, "username_UNIQUE").IsUnique();

            entity.Property(e => e.UserId).HasColumnName("user_id");
            entity.Property(e => e.BirthDate).HasColumnName("birth_date");
            entity.Property(e => e.CreatedAt)
                .HasDefaultValueSql("CURRENT_TIMESTAMP")
                .HasColumnType("timestamp")
                .HasColumnName("created_at");
            entity.Property(e => e.Email)
                .HasMaxLength(100)
                .HasColumnName("email");
            entity.Property(e => e.Gender)
                .HasDefaultValueSql("'male'")
                .HasColumnType("enum('male','female')")
                .HasColumnName("gender");
            entity.Property(e => e.HeightCm).HasColumnName("height_cm");
            entity.Property(e => e.LastName)
                .HasMaxLength(70)
                .HasColumnName("last_name");
            entity.Property(e => e.Name)
                .HasMaxLength(50)
                .HasColumnName("name");
            entity.Property(e => e.PasswordHash)
                .HasMaxLength(255)
                .HasColumnName("password_hash");
            entity.Property(e => e.Patronymic)
                .HasMaxLength(60)
                .HasColumnName("patronymic");
            entity.Property(e => e.RoleId).HasColumnName("role_id");
            entity.Property(e => e.Username)
                .HasMaxLength(50)
                .HasColumnName("username");
            entity.Property(e => e.WeightKg).HasColumnName("weight_kg");

            entity.HasOne(d => d.Role).WithMany(p => p.Users)
                .HasForeignKey(d => d.RoleId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("role_user");
        });

        modelBuilder.Entity<UserMeal>(entity =>
        {
            entity.HasKey(e => new { e.UserId, e.MealId, e.MealTime })
                .HasName("PRIMARY")
                .HasAnnotation("MySql:IndexPrefixLength", new[] { 0, 0, 0 });

            entity
                .ToTable("user_meals")
                .UseCollation("utf8mb4_unicode_ci");

            entity.HasIndex(e => e.MealId, "meal_id");

            entity.Property(e => e.UserId).HasColumnName("user_id");
            entity.Property(e => e.MealId).HasColumnName("meal_id");
            entity.Property(e => e.MealTime)
                .HasDefaultValueSql("CURRENT_TIMESTAMP")
                .HasColumnType("datetime")
                .HasColumnName("meal_time");
            entity.Property(e => e.PortionSize)
                .HasDefaultValueSql("'1'")
                .HasColumnName("portion_size");

            entity.HasOne(d => d.Meal).WithMany(p => p.UserMeals)
                .HasForeignKey(d => d.MealId)
                .HasConstraintName("user_meals_ibfk_2");

            entity.HasOne(d => d.User).WithMany(p => p.UserMeals)
                .HasForeignKey(d => d.UserId)
                .HasConstraintName("user_meals_ibfk_1");
        });

        modelBuilder.Entity<UserWaterLog>(entity =>
        {
            entity.HasKey(e => e.Id).HasName("PRIMARY");

            entity
                .ToTable("user_water_logs")
                .UseCollation("utf8mb4_unicode_ci");

            entity.HasIndex(e => e.UserId, "user_id");

            entity.Property(e => e.Id).HasColumnName("id");
            entity.Property(e => e.AmountL).HasColumnName("amount_l");
            entity.Property(e => e.RecordedAt)
                .HasDefaultValueSql("CURRENT_TIMESTAMP")
                .HasColumnType("datetime")
                .HasColumnName("recorded_at");
            entity.Property(e => e.UserId).HasColumnName("user_id");

            entity.HasOne(d => d.User).WithMany(p => p.UserWaterLogs)
                .HasForeignKey(d => d.UserId)
                .OnDelete(DeleteBehavior.ClientSetNull)
                .HasConstraintName("user_water_logs_ibfk_1");
        });

        modelBuilder.Entity<Workout>(entity =>
        {
            entity.HasKey(e => e.WorkoutId).HasName("PRIMARY");

            entity
                .ToTable("workouts")
                .UseCollation("utf8mb4_unicode_ci");

            entity.HasIndex(e => e.UserId, "user_id");

            entity.Property(e => e.WorkoutId).HasColumnName("workout_id");
            entity.Property(e => e.DurationMin).HasColumnName("duration_min");
            entity.Property(e => e.Name)
                .HasMaxLength(100)
                .HasColumnName("name");
            entity.Property(e => e.UserId).HasColumnName("user_id");

            entity.HasOne(d => d.User).WithMany(p => p.Workouts)
                .HasForeignKey(d => d.UserId)
                .HasConstraintName("workouts_ibfk_1");
        });

        modelBuilder.Entity<WorkoutCompletion>(entity =>
        {
            entity.HasKey(e => e.CompletionId).HasName("PRIMARY");

            entity.ToTable("workout_completions");

            entity.HasIndex(e => e.UserId, "user_id");

            entity.HasIndex(e => e.WorkoutId, "workout_id");

            entity.Property(e => e.CompletionId).HasColumnName("completion_id");
            entity.Property(e => e.CompletedAt)
                .HasDefaultValueSql("CURRENT_TIMESTAMP")
                .HasColumnType("datetime")
                .HasColumnName("completed_at");
            entity.Property(e => e.UserId).HasColumnName("user_id");
            entity.Property(e => e.WorkoutId).HasColumnName("workout_id");

            entity.HasOne(d => d.User).WithMany(p => p.WorkoutCompletions)
                .HasForeignKey(d => d.UserId)
                .HasConstraintName("workout_completions_ibfk_2");

            entity.HasOne(d => d.Workout).WithMany(p => p.WorkoutCompletions)
                .HasForeignKey(d => d.WorkoutId)
                .HasConstraintName("workout_completions_ibfk_1");
        });

        modelBuilder.Entity<WorkoutCompletionExercise>(entity =>
        {
            entity.HasKey(e => e.Id).HasName("PRIMARY");

            entity.ToTable("workout_completion_exercises");

            entity.HasIndex(e => e.CompletionId, "completion_id");

            entity.HasIndex(e => e.ExerciseId, "exercise_id");

            entity.Property(e => e.Id).HasColumnName("id");
            entity.Property(e => e.CompletionId).HasColumnName("completion_id");
            entity.Property(e => e.ExerciseId).HasColumnName("exercise_id");
            entity.Property(e => e.Weight).HasColumnName("weight");

            entity.HasOne(d => d.Completion).WithMany(p => p.WorkoutCompletionExercises)
                .HasForeignKey(d => d.CompletionId)
                .HasConstraintName("workout_completion_exercises_ibfk_1");

            entity.HasOne(d => d.Exercise).WithMany(p => p.WorkoutCompletionExercises)
                .HasForeignKey(d => d.ExerciseId)
                .HasConstraintName("workout_completion_exercises_ibfk_2");
        });

        modelBuilder.Entity<WorkoutExercise>(entity =>
        {
            entity.HasKey(e => new { e.WorkoutId, e.ExerciseId })
                .HasName("PRIMARY")
                .HasAnnotation("MySql:IndexPrefixLength", new[] { 0, 0 });

            entity
                .ToTable("workout_exercises")
                .UseCollation("utf8mb4_unicode_ci");

            entity.HasIndex(e => e.ExerciseId, "exercise_id");

            entity.Property(e => e.WorkoutId).HasColumnName("workout_id");
            entity.Property(e => e.ExerciseId).HasColumnName("exercise_id");
            entity.Property(e => e.Reps)
                .HasDefaultValueSql("'10'")
                .HasColumnName("reps");
            entity.Property(e => e.Sets)
                .HasDefaultValueSql("'3'")
                .HasColumnName("sets");
            entity.Property(e => e.WeightKg)
                .HasDefaultValueSql("'0'")
                .HasColumnName("weight_kg");

            entity.HasOne(d => d.Exercise).WithMany(p => p.WorkoutExercises)
                .HasForeignKey(d => d.ExerciseId)
                .HasConstraintName("workout_exercises_ibfk_2");

            entity.HasOne(d => d.Workout).WithMany(p => p.WorkoutExercises)
                .HasForeignKey(d => d.WorkoutId)
                .HasConstraintName("workout_exercises_ibfk_1");
        });

        OnModelCreatingPartial(modelBuilder);
    }

    partial void OnModelCreatingPartial(ModelBuilder modelBuilder);
}
