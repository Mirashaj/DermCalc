package com.dermcalc.app.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Pasi : Screen("pasi")
    object Easi : Screen("easi")
    object Bmi : Screen("bmi")
    object Bsa : Screen("bsa")
    object History : Screen("history")
    object Result : Screen("result/{score}/{type}") {
        fun createRoute(score: Float, type: String) = "result/$score/$type"
    }
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(route = Screen.Splash.route) {
            SplashScreen(onSplashComplete = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }
        composable(route = Screen.Home.route) {
            HomeScreen(
                onNavigateToPasi = { navController.navigate(Screen.Pasi.route) },
                onNavigateToEasi = { navController.navigate(Screen.Easi.route) },
                onNavigateToBmi = { navController.navigate(Screen.Bmi.route) },
                onNavigateToBsa = { navController.navigate(Screen.Bsa.route) },
                onNavigateToHistory = { navController.navigate(Screen.History.route) }
            )
        }
        composable(route = Screen.Pasi.route) {
            PasiScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToResult = { score -> 
                    navController.navigate(Screen.Result.createRoute(score, "PASI")) {
                        popUpTo(Screen.Pasi.route) { inclusive = true }
                    }
                }     
            )
        }
        composable(route = Screen.Easi.route) {
            EasiScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToResult = { score -> 
                    navController.navigate(Screen.Result.createRoute(score, "EASI")) {
                        popUpTo(Screen.Easi.route) { inclusive = true }
                    }
                }
            )
        }
        composable(route = Screen.Bmi.route) {
            BmiScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToResult = { score -> 
                    navController.navigate(Screen.Result.createRoute(score, "BMI")) {
                        popUpTo(Screen.Bmi.route) { inclusive = true }
                    }
                }
            )
        }
        composable(route = Screen.Bsa.route) {
            BsaScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToResult = { score -> 
                    navController.navigate(Screen.Result.createRoute(score, "BSA")) {
                        popUpTo(Screen.Bsa.route) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = Screen.Result.route,
            arguments = listOf(
                navArgument("score") { type = NavType.FloatType },
                navArgument("type") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val score = backStackEntry.arguments?.getFloat("score") ?: 0f
            val type = backStackEntry.arguments?.getString("type") ?: "UNKNOWN"
            ResultScreen(
                score = score,
                calculatorType = type,
                onNavigateHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
        composable(route = Screen.History.route) {
            HistoryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
