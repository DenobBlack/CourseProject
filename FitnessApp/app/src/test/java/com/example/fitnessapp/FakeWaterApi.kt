package com.example.fitnessapp

import com.example.fitnessapp.data.model.AddWaterRequest
import com.example.fitnessapp.data.model.AuthResponse
import com.example.fitnessapp.data.model.CreateCompletionRequest
import com.example.fitnessapp.data.model.Exercise
import com.example.fitnessapp.data.model.ImageUploadResponse
import com.example.fitnessapp.data.model.LoginRequest
import com.example.fitnessapp.data.model.Meal
import com.example.fitnessapp.data.model.RefreshRequest
import com.example.fitnessapp.data.model.User
import com.example.fitnessapp.data.model.UserProfileDto
import com.example.fitnessapp.data.model.UserRegisterRequest
import com.example.fitnessapp.data.model.Workout
import com.example.fitnessapp.data.model.WorkoutCompletionDto
import com.example.fitnessapp.data.model.WorkoutExercise
import com.example.fitnessapp.data.network.ApiService
import okhttp3.MultipartBody
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

class FakeWaterApi( private val shouldSucceed: Boolean = true,
                   private val todayValue: Int = 0) : ApiService {

    override suspend fun getTodayWater(userId: Int): Response<Int> {
        return Response.success(todayValue)
    }

    override suspend fun addWater(body: AddWaterRequest): Response<Unit> {
        return if (shouldSucceed) {
            Response.success(Unit)
        } else {
            Response.error(400, """{"message":"err"}""".toResponseBody())
        }
    }

    override suspend fun login(request: LoginRequest): Response<AuthResponse> {
        TODO()
    }


    override suspend fun register(request: UserRegisterRequest): Response<AuthResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun refresh(request: RefreshRequest): Response<AuthResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getUsers(): Response<List<User>> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserProfile(id: Int): Response<UserProfileDto> {
        TODO("Not yet implemented")
    }

    override suspend fun updateUserProfile(userId: Int, dto: UserProfileDto): Response<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun createUser(user: User): Response<User> {
        TODO("Not yet implemented")
    }

    override suspend fun updateUser(id: Int, user: User): Response<User> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteUser(id: Int): Response<Void> {
        TODO("Not yet implemented")
    }

    override suspend fun getExercises(): Response<List<Exercise>> {
        TODO("Not yet implemented")
    }

    override suspend fun getExerciseById(id: Int): Response<Exercise> {
        TODO("Not yet implemented")
    }

    override suspend fun createExercise(exercise: Exercise): Response<Exercise> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteExercise(id: Int): Response<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun updateExercise(id: Int, exercise: Exercise): Response<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getMeals(): Response<List<Meal>> {
        TODO("Not yet implemented")
    }

    override suspend fun getMealById(id: Int): Response<Meal> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserWorkouts(userId: Int): Response<List<Workout>> {
        TODO("Not yet implemented")
    }

    override suspend fun getWorkoutExercises(workoutId: Int): Response<List<WorkoutExercise>> {
        TODO("Not yet implemented")
    }

    override suspend fun createWorkout(workout: Workout): Response<Workout> {
        TODO("Not yet implemented")
    }

    override suspend fun addExerciseToWorkout(ex: WorkoutExercise): Response<WorkoutExercise> {
        TODO("Not yet implemented")
    }

    override suspend fun renameWorkout(workoutId: Int, name: String): Response<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun removeExerciseFromWorkout(
        workoutId: Int,
        exerciseId: Int
    ): Response<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun updateWorkoutExercise(
        workoutId: Int,
        exerciseId: Int,
        ex: WorkoutExercise
    ): Response<WorkoutExercise> {
        TODO("Not yet implemented")
    }

    override suspend fun createMeal(meal: Meal): Response<Meal> {
        TODO("Not yet implemented")
    }

    override suspend fun updateMeal(id: Int, meal: Meal): Response<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteMeal(id: Int): Response<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun createWorkoutCompletion(
        workoutId: Int,
        body: CreateCompletionRequest
    ): Response<WorkoutCompletionDto> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserWorkoutCompletions(userId: Int): Response<List<WorkoutCompletionDto>> {
        TODO("Not yet implemented")
    }

    override suspend fun uploadExerciseImage(file: MultipartBody.Part): Response<ImageUploadResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun uploadMealImage(file: MultipartBody.Part): Response<ImageUploadResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun updateWorkoutWeight(
        workoutId: Int,
        exerciseId: Int,
        weight: Float
    ): Response<Unit> {
        TODO("Not yet implemented")
    }


}