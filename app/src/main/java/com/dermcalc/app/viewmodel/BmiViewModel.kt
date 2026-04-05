package com.dermcalc.app.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class BmiState(
    val weightKg: String = "",
    val heightCm: String = ""
) {
    val weightError: Boolean get() = weightKg.isNotEmpty() && (weightKg.toFloatOrNull() ?: 0f) <= 0
    val heightError: Boolean get() = heightCm.isNotEmpty() && (heightCm.toFloatOrNull() ?: 0f) <= 0

    val isValid: Boolean get() = weightKg.isNotEmpty() && heightCm.isNotEmpty() && !weightError && !heightError
}

class BmiViewModel : ViewModel() {
    private val _bmiState = MutableStateFlow(BmiState())
    val bmiState: StateFlow<BmiState> = _bmiState.asStateFlow()

    fun updateWeight(value: String) {
        val sanitized = value.replace(",", ".")
        if (sanitized.isNotEmpty() && sanitized.toFloatOrNull() == null) return
        _bmiState.value = _bmiState.value.copy(weightKg = sanitized)
    }

    fun updateHeight(value: String) {
        val sanitized = value.replace(",", ".")
        if (sanitized.isNotEmpty() && sanitized.toFloatOrNull() == null) return
        _bmiState.value = _bmiState.value.copy(heightCm = sanitized)
    }

    fun calculateBmi(): Float {
        val weight = _bmiState.value.weightKg.toFloatOrNull() ?: return 0f
        val heightCm = _bmiState.value.heightCm.toFloatOrNull() ?: return 0f
        if (heightCm <= 0f) return 0f

        val heightM = heightCm / 100f
        return weight / (heightM * heightM)
    }
}