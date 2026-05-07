package com.courseschedule.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.courseschedule.app.data.Course
import com.courseschedule.app.ui.screen.getCourseEndTime
import com.courseschedule.app.ui.screen.getCourseStartTime
import com.courseschedule.app.ui.theme.courseColors

@Composable
fun CourseCard(
    course: Course,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = courseColors.getOrElse(course.colorIndex) { courseColors[0] }
    val sectionCount = course.endSection - course.startSection + 1
    val cardHeight = (sectionCount * 52).dp
    val timeText = "${getCourseStartTime(course)}-${getCourseEndTime(course)}"

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(cardHeight)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor.copy(alpha = 0.85f))
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = course.name,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = timeText,
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 8.sp,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
            if (course.classroom.isNotBlank()) {
                Text(
                    text = "@${course.classroom}",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 8.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
