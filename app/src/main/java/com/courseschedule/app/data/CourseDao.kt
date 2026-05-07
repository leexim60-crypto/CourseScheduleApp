package com.courseschedule.app.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    @Query("SELECT * FROM courses ORDER BY dayOfWeek, startSection")
    fun getAllCourses(): Flow<List<Course>>

    @Query("SELECT * FROM courses WHERE dayOfWeek = :day ORDER BY startSection")
    fun getCoursesByDay(day: Int): Flow<List<Course>>

    @Query("SELECT * FROM courses WHERE id = :id")
    suspend fun getCourseById(id: Int): Course?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: Course)

    @Update
    suspend fun updateCourse(course: Course)

    @Delete
    suspend fun deleteCourse(course: Course)

    @Query("DELETE FROM courses")
    suspend fun deleteAllCourses()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourses(courses: List<Course>)
}
