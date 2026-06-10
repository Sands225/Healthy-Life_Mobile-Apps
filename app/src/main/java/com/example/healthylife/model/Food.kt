package com.example.healthylife.model

data class Food(
    val id: Int,
    val name: String,
    val emoji: String,
    val calories: Int,
    val carbs: Float,
    val protein: Float,
    val fat: Float,
    val mealType: String  // Breakfast, Lunch, Dinner, Snack
)