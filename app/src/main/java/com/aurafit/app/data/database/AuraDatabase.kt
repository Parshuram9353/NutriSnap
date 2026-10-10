package com.aurafit.app.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aurafit.app.data.database.dao.*
import com.aurafit.app.data.entities.*
import net.sqlcipher.database.SupportFactory
import net.sqlcipher.database.SQLiteDatabase

@Database(
    entities = [
        StepLogEntity::class,
        WeightLogEntity::class,
        MacroLogEntity::class,
        HealthMetricLogEntity::class,
        SleepLogEntity::class,
        WorkoutLogEntity::class,
        UserGoalEntity::class,
        WaterLogEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AuraDatabase : RoomDatabase() {
    abstract fun stepDao(): StepDao
    abstract fun weightDao(): WeightDao
    abstract fun macroDao(): MacroDao
    abstract fun healthMetricDao(): HealthMetricDao
    abstract fun sleepDao(): SleepDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun userGoalDao(): UserGoalDao
    abstract fun waterDao(): WaterDao

    companion object {
        @Volatile
        private var INSTANCE: AuraDatabase? = null

        fun getInstance(context: Context, databasePassphraseHex: ByteArray? = null): AuraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = buildDatabase(context, databasePassphraseHex)
                INSTANCE = instance
                instance
            }
        }

        private fun buildDatabase(context: Context, passphrase: ByteArray?): AuraDatabase {
            val builder = Room.databaseBuilder(
                context.applicationContext,
                AuraDatabase::class.java,
                "aurafit_encrypted_db"
            )

            // Secure local database via SQLCipher integration if passphrase is supplied
            if (passphrase != null) {
                SQLiteDatabase.loadLibs(context)
                val factory = SupportFactory(passphrase)
                builder.openHelperFactory(factory)
            }

            return builder
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
