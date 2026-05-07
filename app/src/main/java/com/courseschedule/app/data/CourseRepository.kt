package com.courseschedule.app.data

import kotlinx.coroutines.flow.Flow

class CourseRepository(private val courseDao: CourseDao) {
    val allCourses: Flow<List<Course>> = courseDao.getAllCourses()

    fun getCoursesByDay(day: Int): Flow<List<Course>> = courseDao.getCoursesByDay(day)

    suspend fun getCourseById(id: Int): Course? = courseDao.getCourseById(id)

    suspend fun insert(course: Course) = courseDao.insertCourse(course)

    suspend fun update(course: Course) = courseDao.updateCourse(course)

    suspend fun delete(course: Course) = courseDao.deleteCourse(course)

    suspend fun deleteAll() = courseDao.deleteAllCourses()

    suspend fun insertAll(courses: List<Course>) = courseDao.insertCourses(courses)
}
