package com.example.healthylife.model

data class SleepRecord(
    val id: Int,
    val date: String,
    val bedTime: String,
    val wakeTime: String,
    val durationHours: Float,
    val quality: String  // Excellent, Normal, Poor
)
