package com.courseschedule.app.util

import com.courseschedule.app.data.Course
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object JsonHelper {
    private val gson = Gson()

    fun toJson(courses: List<Course>): String {
        val exportData = courses.map { course ->
            mapOf(
                "name" to course.name,
                "classroom" to course.classroom,
                "teacher" to course.teacher,
                "dayOfWeek" to course.dayOfWeek,
                "startSection" to course.startSection,
                "endSection" to course.endSection,
                "startWeek" to course.startWeek,
                "endWeek" to course.endWeek,
                "colorIndex" to course.colorIndex,
                "note" to course.note,
                "startTime" to course.startTime,
                "endTime" to course.endTime
            )
        }
        return gson.toJson(exportData)
    }

    fun fromJson(json: String): List<Course> {
        return try {
            val type = object : TypeToken<List<Map<String, Any>>>() {}.type
            val list: List<Map<String, Any>> = gson.fromJson(json, type)
            list.mapNotNull { map ->
                try {
                    Course(
                        name = map["name"] as? String ?: return@mapNotNull null,
                        classroom = map["classroom"] as? String ?: "",
                        teacher = map["teacher"] as? String ?: "",
                        dayOfWeek = (map["dayOfWeek"] as? Double)?.toInt() ?: return@mapNotNull null,
                        startSection = (map["startSection"] as? Double)?.toInt() ?: 1,
                        endSection = (map["endSection"] as? Double)?.toInt() ?: 2,
                        startWeek = (map["startWeek"] as? Double)?.toInt() ?: 1,
                        endWeek = (map["endWeek"] as? Double)?.toInt() ?: 16,
                        colorIndex = (map["colorIndex"] as? Double)?.toInt() ?: 0,
                        note = map["note"] as? String ?: "",
                        startTime = map["startTime"] as? String ?: "",
                        endTime = map["endTime"] as? String ?: ""
                    )
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
