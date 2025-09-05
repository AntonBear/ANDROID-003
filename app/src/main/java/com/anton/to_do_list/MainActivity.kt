package com.anton.to_do_list

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.anton.to_do_list.ui.screens.taskEditScreen.TaskEditScreen
import com.anton.to_do_list.ui.screens.taskListScreen.TaskListScreen
import com.anton.to_do_list.ui.theme.TodolistTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TodolistTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = Routes.LIST) {
                    composable(Routes.LIST) {
                        TaskListScreen(navController)
                    }
                    composable(
                        route = "${Routes.EDIT}/{taskId}",
                        arguments = listOf(navArgument("taskId") {
                            type = NavType.IntType
                            defaultValue = -1
                        })
                    ) { backStackEntry ->
                        val taskId = backStackEntry.arguments?.getInt("taskId") ?: -1
                        TaskEditScreen(navController, taskId)
                    }
                }
            }
        }
    }
}

object Routes {
    const val LIST = "list"
    const val EDIT = "edit"
}
