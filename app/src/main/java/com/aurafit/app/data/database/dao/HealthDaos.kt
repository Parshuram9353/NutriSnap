package com.aurafit.app.data.database.dao

import androidx.room.*
import com.aurafit.app.data.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StepDao {
    @Query("SELECT * FROM step_logs WHERE date = :date LIMIT 1")
    fun getStepsForDate(date: String): Flow<StepLogEntity?>

    @Query("SELECT * FROM step_logs ORDER BY date DESC")
    fun getAllStepLogs(): Flow<List<StepLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStepLog(log: StepLogEntity)

    @Query("SELECT AVG(steps) FROM step_logs WHERE date >= :startDate")
    suspend fun getAverageSteps(startDate: String): Double?
}

@Dao
interface WeightDao {
    @Query("SELECT * FROM weight_logs ORDER BY date DESC")
    fun getAllWeightLogs(): Flow<List<WeightLogEntity>>

    @Query("SELECT * FROM weight_logs ORDER BY date DESC LIMIT 1")
    fun getLatestWeightLog(): Flow<WeightLogEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeightLog(log: WeightLogEntity)
}

@Dao
interface MacroDao {
    @Query("SELECT * FROM macro_logs WHERE date = :date ORDER BY timestamp DESC")
    fun getMacrosForDate(date: String): Flow<List<MacroLogEntity>>

    @Query("SELECT SUM(calories) FROM macro_logs WHERE date = :date")
    fun getCaloriesConsumedForDate(date: String): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMacroLog(log: MacroLogEntity)

    @Delete
    suspend fun deleteMacroLog(log: MacroLogEntity)
}

@Dao
interface HealthMetricDao {
    @Query("SELECT * FROM health_metric_logs ORDER BY timestamp DESC LIMIT 100")
    fun getLatestMetrics(): Flow<List<HealthMetricLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMetric(log: HealthMetricLogEntity)
}

@Dao
interface SleepDao {
    @Query("SELECT * FROM sleep_logs WHERE date = :date LIMIT 1")
    fun getSleepForDate(date: String): Flow<SleepLogEntity?>

    @Query("SELECT * FROM sleep_logs ORDER BY date DESC")
    fun getAllSleepLogs(): Flow<List<SleepLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSleepLog(log: SleepLogEntity)
}

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workout_logs ORDER BY timestamp DESC")
    fun getAllWorkouts(): Flow<List<WorkoutLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(log: WorkoutLogEntity)
}

@Dao
interface UserGoalDao {
    @Query("SELECT * FROM user_goals WHERE id = 1 LIMIT 1")
    fun getUserGoals(): Flow<UserGoalEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateGoals(goals: UserGoalEntity)
}

@Dao
interface WaterDao {
    @Query("SELECT SUM(amount_ml) FROM water_logs WHERE date = :date")
    fun getWaterIntakeForDate(date: String): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaterLog(log: WaterLogEntity)
}
