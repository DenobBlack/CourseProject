package com.example.fitnessapp

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fitnessapp.data.notification.createNotificationChannel
import com.example.fitnessapp.data.repository.SettingsRepository
import com.example.fitnessapp.data.repository.WorkoutScheduleStore
import com.example.fitnessapp.ui.navigation.BottomNavigationBar
import com.example.fitnessapp.ui.screens.AdminPanelScreen
import com.example.fitnessapp.ui.screens.AutoSelectWorkoutScreen
import com.example.fitnessapp.ui.screens.ChangePasswordScreen
import com.example.fitnessapp.ui.screens.CreateWorkoutScreen
import com.example.fitnessapp.ui.screens.DashboardScreen
import com.example.fitnessapp.ui.screens.DeleteAccountScreen
import com.example.fitnessapp.ui.screens.EditProfileScreen
import com.example.fitnessapp.ui.screens.EditWorkoutScreen
import com.example.fitnessapp.ui.screens.ExerciseCreateScreen
import com.example.fitnessapp.ui.screens.ExerciseDetailScreen
import com.example.fitnessapp.ui.screens.ExerciseEditScreen
import com.example.fitnessapp.ui.screens.ExerciseScreen
import com.example.fitnessapp.ui.screens.ExportPdfScreen
import com.example.fitnessapp.ui.screens.LicensesScreen
import com.example.fitnessapp.ui.screens.LoginScreen
import com.example.fitnessapp.ui.screens.MealDetailScreen
import com.example.fitnessapp.ui.screens.MealEditScreen
import com.example.fitnessapp.ui.screens.MealsScreen
import com.example.fitnessapp.ui.screens.RegisterScreen
import com.example.fitnessapp.ui.screens.RestTimerSettingsScreen
import com.example.fitnessapp.ui.screens.SettingsScreen
import com.example.fitnessapp.ui.screens.StartWorkoutScreen
import com.example.fitnessapp.ui.screens.WorkoutSessionScreen
import com.example.fitnessapp.ui.theme.FitnessAppTheme
import com.example.fitnessapp.ui.viewmodel.AdminViewModel
import com.example.fitnessapp.ui.viewmodel.ApiSettingsViewModel
import com.example.fitnessapp.ui.viewmodel.AuthViewModel
import com.example.fitnessapp.ui.viewmodel.ExerciseViewModel
import com.example.fitnessapp.ui.viewmodel.MealViewModel
import com.example.fitnessapp.ui.viewmodel.WaterViewModel
import com.example.fitnessapp.ui.viewmodel.WorkoutViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authViewModel = AuthViewModel(this)
        val waterViewModel = WaterViewModel(this)
        val mealViewModel = MealViewModel(this)
        val exerciseViewModel = ExerciseViewModel(this)
        val workoutViewModel = WorkoutViewModel(this)
        val adminViewModel = AdminViewModel(this)
        val apiSettingsViewModel = ApiSettingsViewModel(this)



        setContent {
            val activity = this

            FitnessAppTheme {
                Log.d("MainActivity", "SDK=${Build.VERSION.SDK_INT}")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                        Log.d("MainActivity","notif perm granted=$granted")
                    }
                    LaunchedEffect(Unit) {
                        Log.d("MainActivity", "CHECK_PERM start")
                        val check = ContextCompat.checkSelfPermission(
                            activity,
                            android.Manifest.permission.POST_NOTIFICATIONS
                        )

                        Log.d("MainActivity", "CHECK_PERM = $check")

                        if (check != PackageManager.PERMISSION_GRANTED) {
                            Log.d("MainActivity", "LAUNCHING PERM DIALOG")
                            launcher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            Log.d("MainActivity", "PERMISSION ALREADY GRANTED")
                        }
                    }
                }
                createNotificationChannel(this)
                val scheduleStore = remember {
                    WorkoutScheduleStore(this.applicationContext)
                }
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val settingsRepository = SettingsRepository(context = this)
                val currentRoute = navBackStackEntry?.destination?.route
                val userId = authViewModel.userId
                val userRole = authViewModel.roleName
                val startDestination = if (userId != null) "home" else "login"

                Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
                    NavHost(
                        navController = navController,
                        startDestination = startDestination
                    ) {
                        composable("login") {
                            LoginScreen(
                                viewModel = authViewModel,
                                navController = navController
                            )
                        }
                        composable("register") {
                            RegisterScreen(
                                viewModel = authViewModel,
                                navController = navController
                            )
                        }
                        composable("home") {
                            DashboardScreen(navController,authViewModel, workoutViewModel, exerciseViewModel, waterViewModel, scheduleStore)
                        }
                        composable(
                            "startWorkout/{plannedId}",
                            arguments = listOf(navArgument("plannedId") { type = NavType.IntType })
                        ) { backStack ->
                            val id = backStack.arguments?.getInt("plannedId")
                            StartWorkoutScreen(
                                workoutViewModel,
                                exerciseViewModel,
                                navController,
                                plannedWorkoutId = id
                            )
                        }

                        composable("startWorkout") {
                            StartWorkoutScreen(
                                workoutViewModel,
                                exerciseViewModel,
                                navController,
                                plannedWorkoutId = null
                            )
                        }
                        composable("createWorkout") {
                            CreateWorkoutScreen(workoutViewModel, exerciseViewModel, navController, userId!!)
                        }
                        composable("deleteAccount"){
                            DeleteAccountScreen(navController, authViewModel)
                        }
                        composable("editWorkout/{id}") { backStack ->
                            val id = backStack.arguments?.getString("id")!!.toInt()
                            EditWorkoutScreen(id, workoutViewModel, exerciseViewModel, navController)
                        }
                        composable("workoutSession/{id}") { backStack ->
                            val id = backStack.arguments?.getString("id")!!.toInt()
                            WorkoutSessionScreen(id, userId!!, workoutViewModel, exerciseViewModel, settingsRepository,navController)
                        }
                        composable("meals") {
                            MealsScreen(navController = navController, viewModel = mealViewModel)
                        }
                        composable("mealDetail/{id}") { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull()
                            if (id != null) MealDetailScreen(id = id, viewModel = mealViewModel, userRole!!, navController)
                        }
                        composable("settings") {
                            SettingsScreen(navController, authViewModel.username!!, userRole!!)
                        }
                        composable("adminPanel") {
                            AdminPanelScreen(adminViewModel)
                        }
                        composable("editProfile") {
                            EditProfileScreen(userId!!,navController, authViewModel)
                        }
                        composable("exerciseCreate") {
                            ExerciseCreateScreen(navController, exerciseViewModel)
                        }
                        composable("autoSelectWorkout") {
                            AutoSelectWorkoutScreen(navController, workoutViewModel)
                        }

                        composable("restTimerSettings") {
                            RestTimerSettingsScreen(navController, workoutViewModel)
                        }
                        composable("exportPdf") {
                            ExportPdfScreen(navController,authViewModel, workoutViewModel, exerciseViewModel, userId!!)
                        }
                        composable("exitAccount") {
                            authViewModel.logout()
                        }
                        composable("changePassword"){
                            ChangePasswordScreen(navController, authViewModel)
                        }
                        composable("licenses"){
                            LicensesScreen(navController, apiSettingsViewModel)
                        }
                        composable("exercises") {
                            ExerciseScreen(
                                navController = navController,
                                viewModel = exerciseViewModel,
                                roleName = authViewModel.roleName!!
                            )
                        }
                        composable("mealEdit/{id}", arguments = listOf(navArgument("id") { type = NavType.IntType })) { backStackEntry ->
                            val id = backStackEntry.arguments?.getInt("id")
                            if (id != null) {
                                MealEditScreen(id, mealViewModel, navController)
                            }
                        }
                        composable("exerciseEdit/{id}", arguments = listOf(navArgument("id") { type = NavType.IntType })) { backStackEntry ->
                            val id = backStackEntry.arguments?.getInt("id")
                            if (id != null) {
                                ExerciseEditScreen(exerciseViewModel, navController)
                            }
                        }
                        composable(
                            route = "exerciseDetail/{id}",
                            arguments = listOf(navArgument("id") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getInt("id")
                            ExerciseDetailScreen(id = id!!, viewModel = exerciseViewModel, userRole!!, navController)
                        }
                    }

                    val showBottomBar = currentRoute in listOf(
                        "home", "exercises", "meals", "settings"
                    )

                    if (showBottomBar) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                        ) {
                            BottomNavigationBar(navController = navController)
                        }
                    }
                }
            }
        }

    }
}
