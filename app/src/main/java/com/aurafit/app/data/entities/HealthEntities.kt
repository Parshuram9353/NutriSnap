package com.aurafit.app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(tableName = "step_logs")
data class StepLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "date") val date: String, // YYYY-MM-DD
    @ColumnInfo(name = "steps") val steps: Int,
    @ColumnInfo(name = "calories") val calories: Int,
    @ColumnInfo(name = "distance_meters") val distanceMeters: Double,
    @ColumnInfo(name = "duration_minutes") val durationMinutes: Double,
    @ColumnInfo(name = "timestamp") val timestamp: Long
)

@Entity(tableName = "weight_logs")
data class WeightLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "date") val date: String, // YYYY-MM-DD
    @ColumnInfo(name = "weight_kg") val weightKg: Double,
    @ColumnInfo(name = "body_fat_percentage") val bodyFatPercentage: Double,
    @ColumnInfo(name = "timestamp") val timestamp: Long
)

@Entity(tableName = "macro_logs")
data class MacroLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "date") val date: String, // YYYY-MM-DD
    @ColumnInfo(name = "food_name") val foodName: String,
    @ColumnInfo(name = "calories") val calories: Int,
    @ColumnInfo(name = "protein_grams") val protein: Int,
    @ColumnInfo(name = "carb_grams") val carbs: Int,
    @ColumnInfo(name = "fat_grams") val fat: Int,
    @ColumnInfo(name = "raw_prompt") val rawPrompt: String,
    @ColumnInfo(name = "timestamp") val timestamp: Long
)

@Entity(tableName = "health_metric_logs")
data class HealthMetricLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "timestamp") val timestamp: Long,
    @ColumnInfo(name = "heart_rate") val heartRate: Int,
    @ColumnInfo(name = "spo2") val spo2: Int
)

@Entity(tableName = "sleep_logs")
data class SleepLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "date") val date: String, // YYYY-MM-DD
    @ColumnInfo(name = "duration_minutes") val durationMinutes: Int,
    @ColumnInfo(name = "sleep_score") val sleepScore: Int,
    @ColumnInfo(name = "timestamp") val timestamp: Long
)

@Entity(tableName = "workout_logs")
data class WorkoutLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "workout_type") val workoutType: String,
    @ColumnInfo(name = "duration_seconds") val durationSeconds: Int,
    @ColumnInfo(name = "calories") val calories: Int,
    @ColumnInfo(name = "distance_meters") val distanceMeters: Double,
    @ColumnInfo(name = "gpx_path_data") val gpxPathData: String, // GPX or JSON representation of route coordinates
    @ColumnInfo(name = "timestamp") val timestamp: Long
)

@Entity(tableName = "user_goals")
data class UserGoalEntity(
    @PrimaryKey val id: Int = 1, // Singleton row
    @ColumnInfo(name = "step_target") val stepTarget: Int = 10000,
    @ColumnInfo(name = "water_target_ml") val waterTargetMl: Int = 2500,
    @ColumnInfo(name = "calorie_target") val calorieTarget: Int = 2200,
    @ColumnInfo(name = "protein_target_g") val proteinTargetG: Int = 150,
    @ColumnInfo(name = "carb_target_g") val carbTargetG: Int = 220,
    @ColumnInfo(name = "fat_target_g") val fatTargetG: Int = 70,
    @ColumnInfo(name = "age") val age: Int = 25,
    @ColumnInfo(name = "height_cm") val heightCm: Int = 175,
    @ColumnInfo(name = "weight_kg") val weightKg: Double = 78.0,
    @ColumnInfo(name = "gender") val gender: String = "Male"
)

@Entity(tableName = "water_logs")
data class WaterLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "date") val date: String, // YYYY-MM-DD
    @ColumnInfo(name = "amount_ml") val amountMl: Int,
    @ColumnInfo(name = "timestamp") val timestamp: Long
)
