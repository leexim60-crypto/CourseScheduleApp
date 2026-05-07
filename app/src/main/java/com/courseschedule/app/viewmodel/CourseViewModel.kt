package com.courseschedule.app.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.courseschedule.app.data.AppDatabase
import com.courseschedule.app.data.Course
import com.courseschedule.app.data.CourseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CourseViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CourseRepository
    private val prefs = application.getSharedPreferences("course_prefs", Context.MODE_PRIVATE)
    val allCourses: StateFlow<List<Course>>
    private val _currentWeek = MutableStateFlow(prefs.getInt("current_week", 1))
    val currentWeek: StateFlow<Int> = _currentWeek.asStateFlow()

    init {
        val courseDao = AppDatabase.getDatabase(application).courseDao()
        repository = CourseRepository(courseDao)
        allCourses = repository.allCourses.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun setCurrentWeek(week: Int) {
        _currentWeek.value = week
        prefs.edit().putInt("current_week", week).apply()
    }

    fun addCourse(course: Course) {
        viewModelScope.launch {
            repository.insert(course)
        }
    }

    fun updateCourse(course: Course) {
        viewModelScope.launch {
            repository.update(course)
        }
    }

    fun deleteCourse(course: Course) {
        viewModelScope.launch {
            repository.delete(course)
        }
    }

    fun deleteAllCourses() {
        viewModelScope.launch {
            repository.deleteAll()
        }
    }

    fun importCourses(courses: List<Course>) {
        viewModelScope.launch {
            repository.insertAll(courses)
        }
    }
}
