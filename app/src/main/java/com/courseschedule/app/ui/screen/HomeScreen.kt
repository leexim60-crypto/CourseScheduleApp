package com.courseschedule.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.courseschedule.app.data.Course
import com.courseschedule.app.ui.components.CourseCard
import java.text.SimpleDateFormat
import java.util.*

private val dayNames = listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")

val defaultSectionTimes = listOf(
    "08:00" to "08:45",
    "08:55" to "09:40",
    "10:00" to "10:45",
    "10:55" to "11:40",
    "14:00" to "14:45",
    "14:55" to "15:40",
    "16:00" to "16:45",
    "16:55" to "17:40",
    "19:00" to "19:45",
    "19:55" to "20:40"
)

fun getCourseStartTime(course: Course): String {
    if (course.startTime.isNotBlank()) return course.startTime
    return defaultSectionTimes.getOrElse(course.startSection - 1) { "08:00" to "08:45" }.first
}

fun getCourseEndTime(course: Course): String {
    if (course.endTime.isNotBlank()) return course.endTime
    return defaultSectionTimes.getOrElse(course.endSection - 1) { "08:00" to "08:45" }.second
}

@Composable
fun HomeScreen(
    courses: List<Course>,
    currentWeek: Int,
    onWeekChange: (Int) -> Unit,
    onCourseClick: (Course) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
    val todayIndex = if (today == Calendar.SUNDAY) 6 else today - 2

    val dateFormat = SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA)
    val weekDayFormat = SimpleDateFormat("EEEE", Locale.CHINA)
    val currentDate = dateFormat.format(Date())
    val currentDayName = weekDayFormat.format(Date())

    var showWeekDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        // 顶部信息栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧：当前日期
            Column {
                Text(
                    text = currentDate,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = currentDayName,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            // 右侧：当前周数（可点击修改）
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { showWeekDialog = true },
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "第 ${currentWeek} 周",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "▼",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                    )
                }
            }
        }

        Divider(modifier = Modifier.padding(horizontal = 16.dp))

        // 周数快速切换
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { if (currentWeek > 1) onWeekChange(currentWeek - 1) }, modifier = Modifier.size(32.dp)) {
                Text("<", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Text(
                text = "第 ${currentWeek} 周",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            IconButton(onClick = { if (currentWeek < 30) onWeekChange(currentWeek + 1) }, modifier = Modifier.size(32.dp)) {
                Text(">", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        // 课表网格
        Row(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // 左侧节次列
            Column(modifier = Modifier.width(44.dp)) {
                Box(modifier = Modifier.height(40.dp))
                for (i in 1..10) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$i",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            val startTime = defaultSectionTimes[i - 1].first
                            Text(
                                text = startTime,
                                fontSize = 7.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }

            // 每天的列
            for (day in 1..7) {
                val dayCourses = courses.filter {
                    it.dayOfWeek == day && currentWeek in it.startWeek..it.endWeek
                }
                val isToday = day - 1 == todayIndex

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            if (isToday)
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                            else Color.Transparent
                        )
                ) {
                    // 星期表头
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = dayNames[day - 1],
                            fontSize = 12.sp,
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                            color = if (isToday)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                    }

                    // 课程格子
                    val sectionMap = mutableMapOf<Int, Course>()
                    for (c in dayCourses) {
                        for (s in c.startSection..c.endSection) {
                            sectionMap[s] = c
                        }
                    }

                    var section = 1
                    while (section <= 10) {
                        val course = sectionMap[section]
                        if (course != null) {
                            CourseCard(
                                course = course,
                                onClick = { onCourseClick(course) }
                            )
                            section = course.endSection + 1
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .padding(1.dp)
                            )
                            section++
                        }
                    }
                }
            }
        }
    }

    // 周数选择对话框
    if (showWeekDialog) {
        WeekPickerDialog(
            currentWeek = currentWeek,
            onWeekSelected = {
                onWeekChange(it)
                showWeekDialog = false
            },
            onDismiss = { showWeekDialog = false }
        )
    }
}

@Composable
private fun WeekPickerDialog(
    currentWeek: Int,
    onWeekSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var inputWeek by remember { mutableStateOf(currentWeek.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("设置当前周数") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("请输入当前是第几周（1-30）", fontSize = 14.sp)
                OutlinedTextField(
                    value = inputWeek,
                    onValueChange = { inputWeek = it.filter { c -> c.isDigit() } },
                    label = { Text("周数") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                // 快速选择
                Text("快速选择：", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(1, 4, 8, 12, 16, 20).forEach { week ->
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { inputWeek = week.toString() },
                            color = if (inputWeek == week.toString())
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = "${week}周",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                fontSize = 12.sp,
                                color = if (inputWeek == week.toString())
                                    Color.White
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val week = inputWeek.toIntOrNull()
                if (week != null && week in 1..30) {
                    onWeekSelected(week)
                }
            }) {
                Text("确认")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
