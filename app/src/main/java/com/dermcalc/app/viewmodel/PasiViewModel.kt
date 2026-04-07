package com.dermcalc.app.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class PasiRegion(val title: String, val weight: Float) {
    HEAD("Testa", 0.1f),
    UPPER_LIMBS("Arti Superiori", 0.2f),
    TRUNK("Tronco", 0.3f),
    LOWER_LIMBS("Arti Inferiori", 0.4f)
}

data class PasiRegionState(
    val erythema: String = "0",
    val induration: String = "0",
    val desquamation: String = "0",
    val area: String = "0",
    val isAreaPercentage: Boolean = false
) {
    
    val erythemaError: Boolean get() = (erythema.toIntOrNull() ?: -1) !in 0..4
    val indurationError: Boolean get() = (induration.toIntOrNull() ?: -1) !in 0..4
    val desquamationError: Boolean get() = (desquamation.toIntOrNull() ?: -1) !in 0..4
    val areaError: Boolean get() = if (isAreaPercentage) {
        val v = area.replace(",", ".").toFloatOrNull()
        v == null || v !in 0f..100f
    } else {
        val v = area.toIntOrNull()
        v == null || v !in 0..6
    }
    val hasError: Boolean get() = erythemaError || indurationError || desquamationError || areaError
}

class PasiViewModel : ViewModel() {
    private val _currentStep = MutableStateFlow(0)
    val currentStep: StateFlow<Int> = _currentStep.asStateFlow()

    private val _regionStates = MutableStateFlow(List(4) { PasiRegionState() })
    val regionStates: StateFlow<List<PasiRegionState>> = _regionStates.asStateFlow()

    fun updateField(stepIndex: Int, field: String, value: String) {
        val currentList = _regionStates.value.toMutableList()
        val currentState = currentList[stepIndex]
        
        val sanitized = value.replace(",", ".")
        if (sanitized.isNotEmpty() && sanitized != ".") {
            if (field == "area" && currentState.isAreaPercentage) {
                if (sanitized.toFloatOrNull() == null) return
            } else {
                if (sanitized.toIntOrNull() == null) return
            }
        }

        val newState = when(field) {
            "erythema" -> currentState.copy(erythema = value)
            "induration" -> currentState.copy(induration = value)
            "desquamation" -> currentState.copy(desquamation = sanitized)
            "area" -> currentState.copy(area = sanitized)
            else -> currentState
        }
        currentList[stepIndex] = newState
        _regionStates.value = currentList
    }

    fun toggleAreaMode(isPercentage: Boolean) {
        _regionStates.value = _regionStates.value.map {
            it.copy(isAreaPercentage = isPercentage, area = "0")
        }
    }

    fun nextStep() {
        if (_currentStep.value < 3) {
            _currentStep.value++
        }
    }

    fun previousStep() {
        if (_currentStep.value > 0) {
            _currentStep.value--
        }
    }

    fun calculateTotalScore(): Float {
        var total = 0f
        val states = _regionStates.value
        states.forEachIndexed { index, state ->
            val e = state.erythema.toIntOrNull() ?: 0
            val i = state.induration.toIntOrNull() ?: 0
            val d = state.desquamation.toIntOrNull() ?: 0

            val a = if (state.isAreaPercentage) {
                val p = state.area.replace(",", ".").toFloatOrNull() ?: 0f
                when {
                    p <= 0f -> 0
                    p < 10f -> 1
                    p < 30f -> 2
                    p < 50f -> 3
                    p < 70f -> 4
                    p < 90f -> 5
                    else -> 6
                }
            } else {
                state.area.toIntOrNull() ?: 0
            }
            
            
            
            val weight = PasiRegion.entries[index].weight
            total += (e + i + d) * a * weight
        }
        return total
    }
}
