# 课程表 App

一款简洁实用的 Android 课程表应用，支持周视图展示、课程管理、JSON 导入导出。

## 功能特性

- 周一到周日完整课表展示，每天 10 节课
- 添加/编辑/删除课程（名称、教室、老师、颜色标记）
- 自定义上课时间和下课时间
- 周数切换，支持设置当前周（自动保存）
- 显示当前日期
- JSON 格式导入导出，方便备份和分享
- Material Design 3 风格界面

## 技术栈

- Kotlin + Jetpack Compose
- Room (SQLite) 本地数据库
- MVVM 架构
- Navigation Compose 页面导航

## 构建

1. 安装 [Android Studio](https://developer.android.com/studio)
2. 打开项目目录
3. 等待 Gradle 同步完成
4. Build → Build APK(s)

需要 JDK 17+ 和 Android SDK 34。

## 导入课表 JSON 格式

```json
[
  {
    "name": "高等数学",
    "classroom": "A101",
    "teacher": "张老师",
    "dayOfWeek": 1,
    "startSection": 1,
    "endSection": 2,
    "startWeek": 1,
    "endWeek": 16,
    "colorIndex": 0,
    "note": "",
    "startTime": "08:00",
    "endTime": "09:40"
  }
]
```

| 字段 | 类型 | 说明 |
|------|------|------|
| name | 字符串 | 课程名称（必填） |
| classroom | 字符串 | 教室 |
| teacher | 字符串 | 老师 |
| dayOfWeek | 数字 | 星期几（1=周一，7=周日） |
| startSection | 数字 | 开始节次（1-10） |
| endSection | 数字 | 结束节次（1-10） |
| startWeek | 数字 | 开始周（1-30） |
| endWeek | 数字 | 结束周（1-30） |
| colorIndex | 数字 | 颜色（0=红, 1=橙, 2=黄, 3=绿, 4=蓝, 5=紫, 6=青, 7=粉） |
| note | 字符串 | 备注（可选） |
| startTime | 字符串 | 上课时间 HH:mm（可选） |
| endTime | 字符串 | 下课时间 HH:mm（可选） |

## 使用说明

1. 点击右下角 **+** 按钮添加课程
2. 点击课表中的课程卡片可编辑或删除
3. 顶部可切换周数，点击周数标签可直接跳转到指定周
4. 底部"设置"页面可导入导出 JSON

## 截图

<img src="screenshots/home.png" width="270" /> <img src="screenshots/add.png" width="270" />
