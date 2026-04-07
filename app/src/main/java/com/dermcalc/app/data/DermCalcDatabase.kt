package com.dermcalc.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CalculationRecord::class], version = 1, exportSchema = false)
abstract class DermCalcDatabase : RoomDatabase() {

    abstract fun calculationDao(): CalculationDao

    companion object {
        @Volatile
        private var INSTANCE: DermCalcDatabase? = null

        fun getDatabase(context: Context): DermCalcDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DermCalcDatabase::class.java,
                    "dermcalc_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
