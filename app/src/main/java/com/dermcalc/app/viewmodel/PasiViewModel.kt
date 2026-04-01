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
    val area: String = "0"
) {
    // Validazione istantanea con blocco ed evidenziazione rossa
    val erythemaError: Boolean get() = (erythema.toIntOrNull() ?: 0) !in 0..4
    val indurationError: Boolean get() = (induration.toIntOrNull() ?: 0) !in 0..4
    val desquamationError: Boolean get() = (desquamation.toIntOrNull() ?: 0) !in 0..4
    val areaError: Boolean get() = (area.toIntOrNull() ?: 0) !in 0..6
    val hasError: Boolean get() = erythemaError || indurationError || desquamationError || areaError
}

class PasiViewModel : ViewModel() {
    private val _currentStep = MutableStateFlow(0)
    val currentStep: StateFlow<Int> = _currentStep.asStateFlow()

    private val _regionStates = MutableStateFlow(List(4) { PasiRegionState() })
    val regionStates: StateFlow<List<PasiRegionState>> = _regionStates.asStateFlow()

    fun updateField(stepIndex: Int, field: String, value: String) {
        // Accetta input vuoto o numeri. Blocca lettere testuali
        if (value.isNotEmpty() && value.toIntOrNull() == null) return 

        val currentList = _regionStates.value.toMutableList()
        val currentState = currentList[stepIndex]
        val newState = when(field) {
            "erythema" -> currentState.copy(erythema = value)
            "induration" -> currentState.copy(induration = value)
            "desquamation" -> currentState.copy(desquamation = value)
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
            val e = state.erythema.toIntOrNull() ?: 0
            val i = state.induration.toIntOrNull() ?: 0
            val d = state.desquamation.toIntOrNull() ?: 0
            val a = state.area.toIntOrNull() ?: 0
            
            // Per il PASI l'area va convertita in fattore proporzionale (1=10%, 2=20-29%...).
            // Ci semplifichiamo usandola come moltiplicatore diretto da formula:
            val weight = PasiRegion.entries[index].weight
            total += (e + i + d) * a * weight
        }
        return total
    }
}
