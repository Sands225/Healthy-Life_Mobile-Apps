package com.example.healthylife.model

data class Food(
    val id: Int,
    val name: String,
    val emoji: String,
    val calories: Int,
    val carbs: Float,
    val protein: Float,
    val fat: Float,
    val fiber: Float,
    val mealType: String,  // Sarapan, Makan Siang, Makan Malam, Makanan Ringan
    val date: String       // YYYY-MM-DD
)
