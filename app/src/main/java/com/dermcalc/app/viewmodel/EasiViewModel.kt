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
    val area: String = "0"
) {
    // Segni clinici 0-3 (EASI severity scale)
    val erythemaError: Boolean get() = (erythema.toIntOrNull() ?: 0) !in 0..3
    val edemaError: Boolean get() = (edema.toIntOrNull() ?: 0) !in 0..3
    val excoriationError: Boolean get() = (excoriation.toIntOrNull() ?: 0) !in 0..3
    val lichenificationError: Boolean get() = (lichenification.toIntOrNull() ?: 0) !in 0..3
    // Area 0-6 (EASI area scale)
    val areaError: Boolean get() = (area.toIntOrNull() ?: 0) !in 0..6
    
    val hasError: Boolean get() = erythemaError || edemaError || excoriationError || lichenificationError || areaError
}

class EasiViewModel : ViewModel() {
    private val _currentStep = MutableStateFlow(0)
    val currentStep: StateFlow<Int> = _currentStep.asStateFlow()

    private val _regionStates = MutableStateFlow(List(4) { EasiRegionState() })
    val regionStates: StateFlow<List<EasiRegionState>> = _regionStates.asStateFlow()

    fun updateField(stepIndex: Int, field: String, value: String) {
        if (value.isNotEmpty() && value.toIntOrNull() == null) return 

        val currentList = _regionStates.value.toMutableList()
        val currentState = currentList[stepIndex]
        val newState = when(field) {
            "erythema" -> currentState.copy(erythema = value)
            "edema" -> currentState.copy(edema = value)
            "excoriation" -> currentState.copy(excoriation = value)
            "lichenification" -> currentState.copy(lichenification = value)
            "area" -> currentState.copy(area = value)
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

    fun calculateTotalScore(): Float {
        var total = 0f
        val states = _regionStates.value
        states.forEachIndexed { index, state ->
            val erythema = state.erythema.toIntOrNull() ?: 0
            val edema = state.edema.toIntOrNull() ?: 0
            val excoriation = state.excoriation.toIntOrNull() ?: 0
            val lichenification = state.lichenification.toIntOrNull() ?: 0
            val area = state.area.toIntOrNull() ?: 0
            
            val multiplier = EasiRegion.entries[index].multiplier
            total += (erythema + edema + excoriation + lichenification) * area * multiplier
        }
        return total
    }
}