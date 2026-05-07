package com.courseschedule.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Schedule
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
import com.courseschedule.app.ui.theme.courseColors
import com.courseschedule.app.ui.theme.courseColorNames

private val dayNames = listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCourseScreen(
    existingCourse: Course? = null,
    onSave: (Course) -> Unit,
    onDelete: (() -> Unit)? = null,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf(existingCourse?.name ?: "") }
    var classroom by remember { mutableStateOf(existingCourse?.classroom ?: "") }
    var teacher by remember { mutableStateOf(existingCourse?.teacher ?: "") }
    var dayOfWeek by remember { mutableIntStateOf(existingCourse?.dayOfWeek ?: 1) }
    var startSection by remember { mutableIntStateOf(existingCourse?.startSection ?: 1) }
    var endSection by remember { mutableIntStateOf(existingCourse?.endSection ?: 2) }
    var startWeek by remember { mutableIntStateOf(existingCourse?.startWeek ?: 1) }
    var endWeek by remember { mutableIntStateOf(existingCourse?.endWeek ?: 16) }
    var colorIndex by remember { mutableIntStateOf(existingCourse?.colorIndex ?: 0) }
    var note by remember { mutableStateOf(existingCourse?.note ?: "") }

    // 时间字段
    var startTime by remember {
        mutableStateOf(
            existingCourse?.startTime?.takeIf { it.isNotBlank() }
                ?: defaultSectionTimes.getOrElse((existingCourse?.startSection ?: 1) - 1) { "08:00" to "08:45" }.first
        )
    }
    var endTime by remember {
        mutableStateOf(
            existingCourse?.endTime?.takeIf { it.isNotBlank() }
                ?: defaultSectionTimes.getOrElse((existingCourse?.endSection ?: 2) - 1) { "08:00" to "08:45" }.second
        )
    }

    val isEditing = existingCourse != null
    val scrollState = rememberScrollState()
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "编辑课程" else "添加课程") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    if (isEditing && onDelete != null) {
                        IconButton(onClick = onDelete) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "删除",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 课程名称
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("课程名称 *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 教室
            OutlinedTextField(
                value = classroom,
                onValueChange = { classroom = it },
                label = { Text("教室") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 老师
            OutlinedTextField(
                value = teacher,
                onValueChange = { teacher = it },
                label = { Text("老师") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // 星期选择
            Text("上课星期", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                dayNames.forEachIndexed { index, dayName ->
                    val selected = dayOfWeek == index + 1
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (selected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable { dayOfWeek = index + 1 }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = dayName,
                            fontSize = 12.sp,
                            color = if (selected) Color.White
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 节次选择
            Text("上课节次", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("开始节次", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedTextField(
                            value = "第 $startSection 节",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            (1..10).forEach { s ->
                                DropdownMenuItem(
                                    text = { Text("第 $s 节") },
                                    onClick = {
                                        startSection = s
                                        if (endSection < s) endSection = s
                                        // 自动更新时间
                                        val times = defaultSectionTimes.getOrElse(s - 1) { "08:00" to "08:45" }
                                        startTime = times.first
                                        if (endSection == s) endTime = times.second
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("结束节次", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedTextField(
                            value = "第 $endSection 节",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            (startSection..10).forEach { s ->
                                DropdownMenuItem(
                                    text = { Text("第 $s 节") },
                                    onClick = {
                                        endSection = s
                                        val times = defaultSectionTimes.getOrElse(s - 1) { "08:00" to "08:45" }
                                        endTime = times.second
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // 上课时间 / 下课时间
            Text("上课时间", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 开始时间
                OutlinedTextField(
                    value = startTime,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("上课时间") },
                    trailingIcon = {
                        IconButton(onClick = { showStartTimePicker = true }) {
                            Icon(Icons.Default.Schedule, contentDescription = "选择时间")
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showStartTimePicker = true }
                )
                // 结束时间
                OutlinedTextField(
                    value = endTime,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("下课时间") },
                    trailingIcon = {
                        IconButton(onClick = { showEndTimePicker = true }) {
                            Icon(Icons.Default.Schedule, contentDescription = "选择时间")
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showEndTimePicker = true }
                )
            }

            // 周数选择
            Text("上课周数", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("开始周", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedTextField(
                            value = "第 $startWeek 周",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            (1..30).forEach { w ->
                                DropdownMenuItem(
                                    text = { Text("第 $w 周") },
                                    onClick = {
                                        startWeek = w
                                        if (endWeek < w) endWeek = w
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("结束周", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedTextField(
                            value = "第 $endWeek 周",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            (startWeek..30).forEach { w ->
                                DropdownMenuItem(
                                    text = { Text("第 $w 周") },
                                    onClick = {
                                        endWeek = w
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // 颜色选择
            Text("颜色标记", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                courseColors.forEachIndexed { index, color ->
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(color)
                            .then(
                                if (colorIndex == index)
                                    Modifier.border(3.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(8.dp))
                                else Modifier
                            )
                            .clickable { colorIndex = index },
                        contentAlignment = Alignment.Center
                    ) {
                        if (colorIndex == index) {
                            Text("✓", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }

            // 备注
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("备注") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )

            // 保存按钮
            Button(
                onClick = {
                    val course = Course(
                        id = existingCourse?.id ?: 0,
                        name = name.trim(),
                        classroom = classroom.trim(),
                        teacher = teacher.trim(),
                        dayOfWeek = dayOfWeek,
                        startSection = startSection,
                        endSection = endSection,
                        startWeek = startWeek,
                        endWeek = endWeek,
                        colorIndex = colorIndex,
                        note = note.trim(),
                        startTime = startTime,
                        endTime = endTime
                    )
                    onSave(course)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = name.isNotBlank()
            ) {
                Text(if (isEditing) "保存修改" else "添加课程", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // 时间选择对话框
    if (showStartTimePicker) {
        TimePickerDialog(
            title = "选择上课时间",
            initialTime = startTime,
            onTimeSelected = { startTime = it; showStartTimePicker = false },
            onDismiss = { showStartTimePicker = false }
        )
    }
    if (showEndTimePicker) {
        TimePickerDialog(
            title = "选择下课时间",
            initialTime = endTime,
            onTimeSelected = { endTime = it; showEndTimePicker = false },
            onDismiss = { showEndTimePicker = false }
        )
    }
}

@Composable
private fun TimePickerDialog(
    title: String,
    initialTime: String,
    onTimeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val parts = initialTime.split(":")
    var hour by remember { mutableStateOf((parts.getOrNull(0)?.toIntOrNull() ?: 8).coerceIn(0, 23)) }
    var minute by remember { mutableStateOf((parts.getOrNull(1)?.toIntOrNull() ?: 0).coerceIn(0, 59)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = String.format("%02d:%02d", hour, minute),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 小时
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("时", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { hour = if (hour > 0) hour - 1 else 23 }, modifier = Modifier.size(36.dp)) {
                                Text("▲", fontSize = 14.sp)
                            }
                            Text(
                                text = String.format("%02d", hour),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(50.dp),
                                textAlign = TextAlign.Center
                            )
                            IconButton(onClick = { hour = if (hour < 23) hour + 1 else 0 }, modifier = Modifier.size(36.dp)) {
                                Text("▼", fontSize = 14.sp)
                            }
                        }
                    }
                    Text(":", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    // 分钟
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("分", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { minute = if (minute >= 5) minute - 5 else 55 }, modifier = Modifier.size(36.dp)) {
                                Text("▲", fontSize = 14.sp)
                            }
                            Text(
                                text = String.format("%02d", minute),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(50.dp),
                                textAlign = TextAlign.Center
                            )
                            IconButton(onClick = { minute = if (minute <= 54) minute + 5 else 0 }, modifier = Modifier.size(36.dp)) {
                                Text("▼", fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onTimeSelected(String.format("%02d:%02d", hour, minute)) }) {
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
