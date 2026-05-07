package com.courseschedule.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.courseschedule.app.data.Course
import com.courseschedule.app.ui.screen.AddCourseScreen
import com.courseschedule.app.ui.screen.HomeScreen
import com.courseschedule.app.ui.screen.SettingsScreen
import com.courseschedule.app.ui.theme.CourseScheduleTheme
import com.courseschedule.app.viewmodel.CourseViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CourseScheduleTheme {
                CourseScheduleApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseScheduleApp() {
    val navController = rememberNavController()
    val viewModel: CourseViewModel = viewModel()
    val courses by viewModel.allCourses.collectAsStateWithLifecycle()
    val currentWeek by viewModel.currentWeek.collectAsStateWithLifecycle()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute == "home" || currentRoute == "settings") {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text("课表") },
                        selected = currentRoute == "home",
                        onClick = {
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = true }
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                        label = { Text("设置") },
                        selected = currentRoute == "settings",
                        onClick = {
                            navController.navigate("settings") {
                                popUpTo("home")
                            }
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            if (currentRoute == "home") {
                FloatingActionButton(
                    onClick = { navController.navigate("add_course") }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "添加课程")
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(
                    courses = courses,
                    currentWeek = currentWeek,
                    onWeekChange = { viewModel.setCurrentWeek(it) },
                    onCourseClick = { course ->
                        navController.navigate("edit_course/${course.id}")
                    }
                )
            }
            composable("add_course") {
                AddCourseScreen(
                    onSave = { course ->
                        viewModel.addCourse(course)
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable("edit_course/{courseId}") { backStackEntry ->
                val courseId = backStackEntry.arguments?.getString("courseId")?.toIntOrNull()
                val course = courses.find { it.id == courseId }
                if (course != null) {
                    var showDeleteDialog by remember { mutableStateOf(false) }
                    AddCourseScreen(
                        existingCourse = course,
                        onSave = { updatedCourse ->
                            viewModel.updateCourse(updatedCourse)
                            navController.popBackStack()
                        },
                        onDelete = { showDeleteDialog = true },
                        onBack = { navController.popBackStack() }
                    )
                    if (showDeleteDialog) {
                        AlertDialog(
                            onDismissRequest = { showDeleteDialog = false },
                            title = { Text("确认删除") },
                            text = { Text("确定要删除课程「${course.name}」吗？") },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        viewModel.deleteCourse(course)
                                        showDeleteDialog = false
                                        navController.popBackStack()
                                    },
                                    colors = ButtonDefaults.textButtonColors(
                                        contentColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Text("删除")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDeleteDialog = false }) {
                                    Text("取消")
                                }
                            }
                        )
                    }
                }
            }
            composable("settings") {
                SettingsScreen(
                    courses = courses,
                    onImport = { importedCourses ->
                        viewModel.importCourses(importedCourses)
                    },
                    onDeleteAll = { viewModel.deleteAllCourses() },
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
