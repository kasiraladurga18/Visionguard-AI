package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vision_history")
data class VisionHistoryEntity(
    @PrimaryKey
    val id: String,
    val taskType: String, // scene_describe, ocr_read, ask_ai, obstacle_scan
    val prompt: String,
    val resultText: String,
    val conciseSpeech: String,
    val timestamp: Long = System.currentTimeMillis()
)
