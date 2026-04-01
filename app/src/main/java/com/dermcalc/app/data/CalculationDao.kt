package com.dermcalc.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CalculationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: CalculationRecord)

    @Query("SELECT * FROM calculation_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<CalculationRecord>>

    @Query("SELECT * FROM calculation_records WHERE calculatorType = :type ORDER BY timestamp DESC")
    fun getRecordsByType(type: String): Flow<List<CalculationRecord>>

    @Query("DELETE FROM calculation_records")
    suspend fun clearHistory()
}