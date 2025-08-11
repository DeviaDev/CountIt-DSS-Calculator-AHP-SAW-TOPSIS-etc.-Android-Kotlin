package com.devianest.countit.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.devianest.countit.ui.screens.*
import com.devianest.countit.viewmodel.MainViewModel

@Composable
fun CountItNavigation() {
    val navController = rememberNavController()
    val viewModel: MainViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        composable("project_setup") {
            ProjectSetupScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        composable("data_input") {
            DataInputScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        composable("matrix_input") {
            MatrixInputScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        composable("results") {
            ResultsScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}