package com.example.fitnessapp.data.repository

import android.content.Context
import com.example.fitnessapp.data.model.CreateCompletionRequest
import com.example.fitnessapp.data.model.Workout
import com.example.fitnessapp.data.model.WorkoutCompletionDto
import com.example.fitnessapp.data.model.WorkoutExercise
import com.example.fitnessapp.data.network.RetrofitClient

class WorkoutRepository(private val context: Context) {

    private val workoutApi = RetrofitClient.create(context)

    suspend fun getUserWorkouts(id: Int) = workoutApi.getUserWorkouts(id)
    suspend fun getWorkoutExercises(id: Int) = workoutApi.getWorkoutExercises(id)
    suspend fun createWorkout(workout: Workout) = workoutApi.createWorkout(workout)
    suspend fun addExerciseToWorkout(workoutExercise: WorkoutExercise)
        = workoutApi.addExerciseToWorkout(workoutExercise)
    suspend fun renameWorkout(id: Int, name: String) = workoutApi.renameWorkout(id, name)
    suspend fun removeExerciseFromWorkout(workoutId: Int, exerciseId: Int)
        = workoutApi.removeExerciseFromWorkout(workoutId, exerciseId)
    suspend fun updateWorkoutExercise(workoutId: Int, exerciseId: Int, workoutExercise: WorkoutExercise)
        = workoutApi.updateWorkoutExercise(workoutId, exerciseId, workoutExercise)
    suspend fun createWorkoutCompletion(id: Int, completionRequest: CreateCompletionRequest)
        = workoutApi.createWorkoutCompletion(id, completionRequest)
    suspend fun getUserWorkoutCompletions(id: Int)
        = workoutApi.getUserWorkoutCompletions(id)
}