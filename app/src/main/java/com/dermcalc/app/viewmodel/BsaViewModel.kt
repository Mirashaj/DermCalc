package com.dermcalc.app.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.sqrt




data class BsaState(
    val weightKg: String = "",
    val heightCm: String = ""
) {
    val weightError: Boolean get() {
        if (weightKg.isEmpty()) return false
        val w = weightKg.toFloatOrNull() ?: 0f
        return w < 2f || w > 500f
    }
    val heightError: Boolean get() {
        if (heightCm.isEmpty()) return false
        val h = heightCm.toFloatOrNull() ?: 0f
        return h < 30f || h > 300f
    }

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
