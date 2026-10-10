package com.aurafit.app.domain.repository

import com.aurafit.app.domain.model.*
import kotlinx.coroutines.flow.Flow

interface HealthRepository {
    fun getStepsForDate(date: String): Flow<Steps?>
    fun getAllSteps(): Flow<List<Steps>>
    suspend fun saveSteps(steps: Steps)

    fun getLatestWeight(): Flow<Weight?>
    fun getAllWeights(): Flow<List<Weight>>
    suspend fun saveWeight(weight: Weight)

    fun getMacrosForDate(date: String): Flow<List<MacroLog>>
    suspend fun saveMacro(macro: MacroLog)
    suspend fun deleteMacro(macro: MacroLog)

    fun getLatestMetrics(): Flow<List<HealthMetric>>
    suspend fun saveMetric(metric: HealthMetric)

    fun getSleepForDate(date: String): Flow<Sleep?>
    fun getAllSleeps(): Flow<List<Sleep>>
    suspend fun saveSleep(sleep: Sleep)

    fun getAllWorkouts(): Flow<List<Workout>>
    suspend fun saveWorkout(workout: Workout)

    fun getUserGoals(): Flow<UserGoal>
    suspend fun saveUserGoals(goals: UserGoal)

    fun getWaterForDate(date: String): Flow<Int>
    suspend fun saveWater(water: WaterLog)
}
