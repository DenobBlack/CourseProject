package com.example.fitnessapp.data.network

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
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {


    @POST("api/Auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/Auth/register")
    suspend fun register(@Body request: UserRegisterRequest): Response<AuthResponse>

    @POST("api/Auth/refresh")
    suspend fun refresh(@Body request: RefreshRequest): Response<AuthResponse>

    @GET("api/Users")
    suspend fun getUsers(): Response<List<User>>

    @GET("api/Users/{id}/profile")
    suspend fun getUserProfile(@Path("id") id: Int): Response<UserProfileDto>

    @PUT("api/Users/{id}/profile")
    suspend fun updateUserProfile(@Path("id") userId: Int, @Body dto: UserProfileDto): Response<Unit>

    @POST("api/Users")
    suspend fun createUser(@Body user: User): Response<User>

    @PUT("api/Users/{id}")
    suspend fun updateUser(@Path("id") id: Int, @Body user: User): Response<User>

    @DELETE("api/Users/{id}")
    suspend fun deleteUser(@Path("id") id: Int): Response<Void>

    @GET("api/Exercises")
    suspend fun getExercises(): Response<List<Exercise>>

    @GET("api/Exercises/{id}")
    suspend fun getExerciseById(@Path("id") id: Int): Response<Exercise>

    @POST("api/Exercises")
    suspend fun createExercise(@Body exercise: Exercise): Response<Exercise>

    @DELETE("api/Exercises/{id}")
    suspend fun deleteExercise(@Path("id") id: Int) : Response<Unit>

    @PUT("api/Exercises/{id}")
    suspend fun updateExercise(@Path("id") id: Int, @Body exercise: Exercise) : Response<Unit>

    @GET("api/Meals")
    suspend fun getMeals(): Response<List<Meal>>

    @GET("api/Meals/{id}")
    suspend fun getMealById(@Path("id") id: Int): Response<Meal>

    @GET("api/Workouts/{userId}")
    suspend fun getUserWorkouts(@Path("userId") userId: Int): Response<List<Workout>>

    @GET("api/WorkoutExercises/{workoutId}")
    suspend fun getWorkoutExercises(@Path("workoutId") workoutId: Int): Response<List<WorkoutExercise>>

    @POST("api/Workouts")
    suspend fun createWorkout(@Body workout: Workout): Response<Workout>

    @POST("api/WorkoutExercises")
    suspend fun addExerciseToWorkout(@Body ex: WorkoutExercise): Response<WorkoutExercise>

    @PUT("api/Workouts/{id}/rename")
    suspend fun renameWorkout(@Path("id") workoutId: Int, @Body name: String): Response<Unit>

    @DELETE("api/WorkoutExercises/{workoutId}/{exerciseId}")
    suspend fun removeExerciseFromWorkout(@Path("workoutId") workoutId: Int, @Path("exerciseId") exerciseId: Int): Response<Unit>

    @PUT("api/WorkoutExercises/{workoutId}/{exerciseId}")
    suspend fun updateWorkoutExercise(@Path("workoutId") workoutId: Int, @Path("exerciseId") exerciseId: Int, @Body ex: WorkoutExercise): Response<WorkoutExercise>

    @POST("api/Meals")
    suspend fun createMeal(@Body meal: Meal): Response<Meal>

    @PUT("api/Meals/{id}")
    suspend fun updateMeal(@Path("id") id: Int, @Body meal: Meal): Response<Unit>

    @DELETE("api/Meals/{id}")
    suspend fun deleteMeal(@Path("id") id: Int): Response<Unit>

    @Multipart
    @POST("api/Exercises/upload")
    suspend fun uploadExerciseImage(
        @Part file: MultipartBody.Part
    ): Response<ImageUploadResponse>

    @Multipart
    @POST("api/Meals/upload")
    suspend fun uploadMealImage(
        @Part file: MultipartBody.Part
    ): Response<ImageUploadResponse>

    @PATCH("api/WorkoutExercises/{workoutId}/{exerciseId}/weight")
    suspend fun updateWorkoutWeight(
        @Path("workoutId") workoutId: Int,
        @Path("exerciseId") exerciseId: Int,
        @Body weight: Float
    ): Response<Unit>

    @POST("api/Workouts/{workoutId}/completions")
    suspend fun createWorkoutCompletion(
        @Path("workoutId") workoutId: Int,
        @Body body: CreateCompletionRequest
    ): Response<WorkoutCompletionDto>

    @GET("api/Users/{userId}/workout-completions")
    suspend fun getUserWorkoutCompletions(
        @Path("userId") userId: Int
    ): Response<List<WorkoutCompletionDto>>

    @GET("api/Water/today/{userId}")
    suspend fun getTodayWater(@Path("userId") userId: Int): Response<Int>

    @POST("api/Water/add")
    suspend fun addWater(@Body body: AddWaterRequest): Response<Unit>

}
