package com.dermcalc.app.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class EasiRegion(val title: String, val multiplier: Float) {
    HEAD_NECK("Testa e Collo", 0.1f),
    UPPER_LIMBS("Arti Superiori", 0.2f),
    TRUNK("Tronco", 0.3f),
    LOWER_LIMBS("Arti Inferiori", 0.4f)
}

data class EasiRegionState(
    val erythema: String = "0",
    val edema: String = "0",
    val excoriation: String = "0",
    val lichenification: String = "0",
    val area: String = "0",
    val isAreaPercentage: Boolean = false
) {
    
    val erythemaError: Boolean get() = (erythema.replace(",", ".").toFloatOrNull() ?: -1f) !in 0f..3f
    val edemaError: Boolean get() = (edema.replace(",", ".").toFloatOrNull() ?: -1f) !in 0f..3f
    val excoriationError: Boolean get() = (excoriation.replace(",", ".").toFloatOrNull() ?: -1f) !in 0f..3f
    val lichenificationError: Boolean get() = (lichenification.replace(",", ".").toFloatOrNull() ?: -1f) !in 0f..3f
    
    val areaError: Boolean get() = if (isAreaPercentage) {
        val v = area.replace(",", ".").toFloatOrNull()
        v == null || v !in 0f..100f
    } else {
        val v = area.toIntOrNull()
        v == null || v !in 0..6
    }
    
    val hasError: Boolean get() = erythemaError || edemaError || excoriationError || lichenificationError || areaError
}

class EasiViewModel : ViewModel() {
    private val _currentStep = MutableStateFlow(0)
    val currentStep: StateFlow<Int> = _currentStep.asStateFlow()

    private val _regionStates = MutableStateFlow(List(4) { EasiRegionState() })
    val regionStates: StateFlow<List<EasiRegionState>> = _regionStates.asStateFlow()

    fun updateField(stepIndex: Int, field: String, value: String) {
        val sanitizedValue = value.replace(",", ".")
        
        if (sanitizedValue.isNotEmpty() && sanitizedValue != ".") {
            if (field == "area") {
                val currentMode = _regionStates.value[stepIndex].isAreaPercentage
                if (currentMode) {
                    if (sanitizedValue.toFloatOrNull() == null) return
                } else {
                    if (sanitizedValue.toIntOrNull() == null) return
                }
            } else {
                if (sanitizedValue.toFloatOrNull() == null) return
            }
        }

        val currentList = _regionStates.value.toMutableList()
        val currentState = currentList[stepIndex]
        val newState = when(field) {
            "erythema" -> currentState.copy(erythema = sanitizedValue)
            "edema" -> currentState.copy(edema = sanitizedValue)
            "excoriation" -> currentState.copy(excoriation = sanitizedValue)
            "lichenification" -> currentState.copy(lichenification = sanitizedValue)
            "area" -> currentState.copy(area = sanitizedValue)
            else -> currentState
        }
        currentList[stepIndex] = newState
        _regionStates.value = currentList
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

    fun toggleAreaMode(isPercentage: Boolean) {
        _regionStates.value = _regionStates.value.map {
            it.copy(isAreaPercentage = isPercentage, area = "0")
        }
    }

    fun calculateTotalScore(): Float {
        var total = 0f
        val states = _regionStates.value
        states.forEachIndexed { index, state ->
            val erythema = state.erythema.replace(",", ".").toFloatOrNull() ?: 0f
            val edema = state.edema.replace(",", ".").toFloatOrNull() ?: 0f
            val excoriation = state.excoriation.replace(",", ".").toFloatOrNull() ?: 0f
            val lichenification = state.lichenification.replace(",", ".").toFloatOrNull() ?: 0f
            val area = if (state.isAreaPercentage) {
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
            
            val multiplier = EasiRegion.entries[index].multiplier
            total += (erythema + edema + excoriation + lichenification) * area * multiplier
        }
        return total
    }
}
