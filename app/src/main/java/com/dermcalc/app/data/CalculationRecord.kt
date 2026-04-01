package com.dermcalc.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calculation_records")
data class CalculationRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val calculatorType: String, // PASI, EASI, BMI, BSA
    val score: Float,
    val resultInterpretation: String, // lieve, moderata, severa...
    val timestamp: Long = System.currentTimeMillis()
)