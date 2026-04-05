package com.dermcalc.app.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.sqrt

// We use the Mosteller formula: BSA = sqrt((weight_kg * height_cm) / 3600)
// This is the most widely used formula in practice.

data class BsaState(
    val weightKg: String = "",
    val heightCm: String = ""
) {
    val weightError: Boolean get() = weightKg.isNotEmpty() && (weightKg.toFloatOrNull() ?: 0f) <= 0
    val heightError: Boolean get() = heightCm.isNotEmpty() && (heightCm.toFloatOrNull() ?: 0f) <= 0

    val isValid: Boolean get() = weightKg.isNotEmpty() && heightCm.isNotEmpty() && !weightError && !heightError
}

class BsaViewModel : ViewModel() {
    private val _bsaState = MutableStateFlow(BsaState())
    val bsaState: StateFlow<BsaState> = _bsaState.asStateFlow()

    fun updateWeight(value: String) {
        val sanitized = value.replace(",", ".")
        if (sanitized.isNotEmpty() && sanitized.toFloatOrNull() == null) return
        _bsaState.value = _bsaState.value.copy(weightKg = sanitized)
    }

    fun updateHeight(value: String) {
        val sanitized = value.replace(",", ".")
        if (sanitized.isNotEmpty() && sanitized.toFloatOrNull() == null) return
        _bsaState.value = _bsaState.value.copy(heightCm = sanitized)
    }

    fun calculateBsa(): Float {
        val weight = _bsaState.value.weightKg.toFloatOrNull() ?: return 0f
        val height = _bsaState.value.heightCm.toFloatOrNull() ?: return 0f
        if (weight <= 0f || height <= 0f) return 0f

        return sqrt((weight * height) / 3600.0).toFloat()
    }
}