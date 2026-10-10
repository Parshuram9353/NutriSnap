package com.aurafit.app.data.repository

import com.aurafit.app.data.database.AuraDatabase
import com.aurafit.app.data.entities.*
import com.aurafit.app.domain.model.*
import com.aurafit.app.domain.repository.HealthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HealthRepositoryImpl @Inject constructor(
    private val db: AuraDatabase
) : HealthRepository {

    override fun getStepsForDate(date: String): Flow<Steps?> {
        return db.stepDao().getStepsForDate(date).map { entity ->
            entity?.let {
                Steps(it.id, it.date, it.steps, it.calories, it.distanceMeters, it.durationMinutes, it.timestamp)
            }
        }
    }

    override fun getAllSteps(): Flow<List<Steps>> {
        return db.stepDao().getAllStepLogs().map { list ->
            list.map {
                Steps(it.id, it.date, it.steps, it.calories, it.distanceMeters, it.durationMinutes, it.timestamp)
            }
        }
    }

    override suspend fun saveSteps(steps: Steps) {
        db.stepDao().insertOrUpdateStepLog(
            StepLogEntity(
                steps.id, steps.date, steps.steps, steps.calories, steps.distanceMeters, steps.durationMinutes, steps.timestamp
            )
        )
    }

    override fun getLatestWeight(): Flow<Weight?> {
        return db.weightDao().getLatestWeightLog().map { entity ->
            entity?.let {
                Weight(it.id, it.date, it.weightKg, it.bodyFatPercentage, it.timestamp)
            }
        }
    }

    override fun getAllWeights(): Flow<List<Weight>> {
        return db.weightDao().getAllWeightLogs().map { list ->
            list.map {
                Weight(it.id, it.date, it.weightKg, it.bodyFatPercentage, it.timestamp)
            }
        }
    }

    override suspend fun saveWeight(weight: Weight) {
        db.weightDao().insertWeightLog(
            WeightLogEntity(
                weight.id, weight.date, weight.weightKg, weight.bodyFatPercentage, weight.timestamp
            )
        )
    }

    override fun getMacrosForDate(date: String): Flow<List<MacroLog>> {
        return db.macroDao().getMacrosForDate(date).map { list ->
            list.map {
                MacroLog(it.id, it.date, it.foodName, it.calories, it.protein, it.carbs, it.fat, it.rawPrompt, it.timestamp)
            }
        }
    }

    override suspend fun saveMacro(macro: MacroLog) {
        db.macroDao().insertMacroLog(
            MacroLogEntity(
                macro.id, macro.date, macro.foodName, macro.calories, macro.protein, macro.carbs, macro.fat, macro.rawPrompt, macro.timestamp
            )
        )
    }

    override suspend fun deleteMacro(macro: MacroLog) {
        db.macroDao().deleteMacroLog(
            MacroLogEntity(
                macro.id, macro.date, macro.foodName, macro.calories, macro.protein, macro.carbs, macro.fat, macro.rawPrompt, macro.timestamp
            )
        )
    }

    override fun getLatestMetrics(): Flow<List<HealthMetric>> {
        return db.healthMetricDao().getLatestMetrics().map { list ->
            list.map {
                HealthMetric(it.timestamp, it.heartRate, it.spo2)
            }
        }
    }

    override suspend fun saveMetric(metric: HealthMetric) {
        db.healthMetricDao().insertMetric(
            HealthMetricLogEntity(
                timestamp = metric.timestamp,
                heartRate = metric.heartRate,
                spo2 = metric.spo2
            )
        )
    }

    override fun getSleepForDate(date: String): Flow<Sleep?> {
        return db.sleepDao().getSleepForDate(date).map { entity ->
            entity?.let {
                Sleep(it.id, it.date, it.durationMinutes, it.sleepScore, it.timestamp)
            }
        }
    }

    override fun getAllSleeps(): Flow<List<Sleep>> {
        return db.sleepDao().getAllSleepLogs().map { list ->
            list.map {
                Sleep(it.id, it.date, it.durationMinutes, it.sleepScore, it.timestamp)
            }
        }
    }

    override suspend fun saveSleep(sleep: Sleep) {
        db.sleepDao().insertSleepLog(
            SleepLogEntity(
                sleep.id, sleep.date, sleep.durationMinutes, sleep.sleepScore, sleep.timestamp
            )
        )
    }

    override fun getAllWorkouts(): Flow<List<Workout>> {
        return db.workoutDao().getAllWorkouts().map { list ->
            list.map {
                Workout(it.id, it.workoutType, it.durationSeconds, it.calories, it.distanceMeters, it.gpxPathData, it.timestamp)
            }
        }
    }

    override suspend fun saveWorkout(workout: Workout) {
        db.workoutDao().insertWorkout(
            WorkoutLogEntity(
                workout.id, workout.workoutType, workout.durationSeconds, workout.calories, workout.distanceMeters, workout.gpxPathData, workout.timestamp
            )
        )
    }

    override fun getUserGoals(): Flow<UserGoal> {
        return db.userGoalDao().getUserGoals().map { entity ->
            entity?.let {
                UserGoal(
                    stepTarget = it.stepTarget,
                    waterTargetMl = it.waterTargetMl,
                    calorieTarget = it.calorieTarget,
                    proteinTargetG = it.proteinTargetG,
                    carbTargetG = it.carbTargetG,
                    fatTargetG = it.fatTargetG,
                    age = it.age,
                    heightCm = it.heightCm,
                    weightKg = it.weightKg,
                    gender = it.gender
                )
            } ?: UserGoal() // Default goal metrics fallback
        }
    }

    override suspend fun saveUserGoals(goals: UserGoal) {
        db.userGoalDao().insertOrUpdateGoals(
            UserGoalEntity(
                id = 1,
                stepTarget = goals.stepTarget,
                waterTargetMl = goals.waterTargetMl,
                calorieTarget = goals.calorieTarget,
                proteinTargetG = goals.proteinTargetG,
                carbTargetG = goals.carbTargetG,
                fatTargetG = goals.fatTargetG,
                age = goals.age,
                heightCm = goals.heightCm,
                weightKg = goals.weightKg,
                gender = goals.gender
            )
        )
    }

    override fun getWaterForDate(date: String): Flow<Int> {
        return db.waterDao().getWaterIntakeForDate(date).map { value ->
            value ?: 0
        }
    }

    override suspend fun saveWater(water: WaterLog) {
        db.waterDao().insertWaterLog(
            WaterLogEntity(
                date = water.date,
                amountMl = water.amountMl,
                timestamp = water.timestamp
            )
        )
    }
}
