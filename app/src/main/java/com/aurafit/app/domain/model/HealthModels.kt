package com.aurafit.app.domain.model

data class Steps(
    val id: Long = 0,
    val date: String,
    val steps: Int,
    val calories: Int,
    val distanceMeters: Double,
    val durationMinutes: Double,
    val timestamp: Long
)

data class Weight(
    val id: Long = 0,
    val date: String,
    val weightKg: Double,
    val bodyFatPercentage: Double,
    val timestamp: Long
)

data class MacroLog(
    val id: Long = 0,
    val date: String,
    val foodName: String,
    val calories: Int,
    val protein: Int,
    val carbs: Int,
    val fat: Int,
    val rawPrompt: String,
    val timestamp: Long
)

data class HealthMetric(
    val timestamp: Long,
    val heartRate: Int,
    val spo2: Int
)

data class Sleep(
    val id: Long = 0,
    val date: String,
    val durationMinutes: Int,
    val sleepScore: Int,
    val timestamp: Long
)

data class Workout(
    val id: Long = 0,
    val workoutType: String,
    val durationSeconds: Int,
    val calories: Int,
    val distanceMeters: Double,
    val gpxPathData: String,
    val timestamp: Long
)

data class UserGoal(
    val stepTarget: Int = 10000,
    val waterTargetMl: Int = 2500,
    val calorieTarget: Int = 2200,
    val proteinTargetG: Int = 150,
    val carbTargetG: Int = 220,
    val fatTargetG: Int = 70,
    val age: Int = 25,
    val heightCm: Int = 175,
    val weightKg: Double = 78.0,
    val gender: String = "Male"
)

data class WaterLog(
    val date: String,
    val amountMl: Int,
    val timestamp: Long
)
