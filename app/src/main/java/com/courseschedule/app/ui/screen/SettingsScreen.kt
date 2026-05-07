package com.courseschedule.app.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.courseschedule.app.data.Course
import com.courseschedule.app.util.JsonHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    courses: List<Course>,
    onImport: (List<Course>) -> Unit,
    onDeleteAll: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var importMessage by remember { mutableStateOf<String?>(null) }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val json = inputStream?.bufferedReader()?.use { reader -> reader.readText() }
                if (json != null) {
                    val imported = JsonHelper.fromJson(json)
                    if (imported.isNotEmpty()) {
                        onImport(imported)
                        importMessage = "成功导入 ${imported.size} 门课程"
                    } else {
                        importMessage = "文件格式错误或为空"
                    }
                }
            } catch (e: Exception) {
                importMessage = "导入失败: ${e.message}"
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
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
            // 数据管理
            Text(
                text = "数据管理",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // 导出课表
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val json = JsonHelper.toJson(courses)
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "application/json"
                        putExtra(Intent.EXTRA_TEXT, json)
                        putExtra(Intent.EXTRA_SUBJECT, "课程表数据")
                    }
                    context.startActivity(Intent.createChooser(intent, "导出课表"))
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("导出课表", fontWeight = FontWeight.Bold)
                        Text(
                            "将课表数据导出为JSON，可通过微信/QQ分享",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    Text("${courses.size} 门", color = MaterialTheme.colorScheme.primary)
                }
            }

            // 导入课表
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = { importLauncher.launch("application/json") }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.FileUpload, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("导入课表", fontWeight = FontWeight.Bold)
                        Text(
                            "从JSON文件导入课表数据",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            // 导入提示消息
            importMessage?.let {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Text(
                        text = it,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 危险操作
            Text(
                text = "危险操作",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // 清空所有数据
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                onClick = { showDeleteDialog = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.DeleteForever, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("清空所有课程", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                        Text(
                            "删除所有课程数据，此操作不可恢复",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 使用帮助
            Text(
                text = "使用帮助",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    HelpItem("1. 点击右下角 + 按钮添加课程")
                    HelpItem("2. 填写课程名称、教室、老师等信息")
                    HelpItem("3. 选择上课星期、节次和周数")
                    HelpItem("4. 选择颜色标记方便区分")
                    HelpItem("5. 点击课表中的课程可以编辑或删除")
                    HelpItem("6. 通过导出功能可以备份和分享课表")
                    HelpItem("7. JSON格式示例:")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = """[{
  "name": "高等数学",
  "classroom": "A101",
  "teacher": "张老师",
  "dayOfWeek": 1,
  "startSection": 1,
  "endSection": 2,
  "startWeek": 1,
  "endWeek": 16,
  "colorIndex": 0
}]""",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
            }
        }
    }

    // 删除确认对话框
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("确认清空") },
            text = { Text("确定要删除所有课程数据吗？此操作不可恢复。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteAll()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("确认删除")
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

@Composable
private fun HelpItem(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        modifier = Modifier.padding(vertical = 2.dp)
    )
}
