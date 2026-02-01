package edu.pbs.ProjektPogoda.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import pbs.edu.ProjektPogoda.screens.AddScreen
import pbs.edu.ProjektPogoda.screens.DetailsScreen
import pbs.edu.ProjektPogoda.screens.HomeScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {

        composable("home") {
            HomeScreen(navController = navController)
        }

        composable("add") {
            AddScreen(navController = navController)
        }

        composable(
            route = "details/{id}",
            arguments = listOf(
                navArgument("id") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->

            val id = backStackEntry.arguments?.getLong("id") ?: 0L

            DetailsScreen(
                navController = navController,
                itemId = id
            )
        }
    }
}
