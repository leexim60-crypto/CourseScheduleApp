package com.courseschedule.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class Course(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val classroom: String,
    val teacher: String,
    val dayOfWeek: Int,
    val startSection: Int,
    val endSection: Int,
    val startWeek: Int,
    val endWeek: Int,
    val colorIndex: Int,
    val note: String = "",
    val startTime: String = "",
    val endTime: String = ""
)
