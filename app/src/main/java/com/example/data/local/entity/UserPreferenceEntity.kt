package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_preferences")
data class UserPreferenceEntity(
    @PrimaryKey
    val id: Int = 1,
    val speechRate: Float = 1.0f,
    val ttsPitch: Float = 1.0f,
    val highContrast: Boolean = true,
    val faceOptIn: Boolean = false,
    val obstacleAlerts: Boolean = true,
    val hapticFeedback: Boolean = true,
    val backendUrl: String = "http://10.0.2.2:8000",
    val themeMode: String = "cyber_tactile",
    val enable3DTilt: Boolean = true,
    val isDarkMode: Boolean = true,
    val onboardingCompleted: Boolean = false,
    val isLoggedIn: Boolean = false,
    val userName: String = "VisionGuard User",
    val userEmail: String = ""
)
