package com.dermcalc.app.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class BodyRegion(val displayName: String, val weight: Double) {
    HEAD("Testa", 0.1),
    UPPER_LIMBS("Arti sup.", 0.2),
    TRUNK("Tronco", 0.3),
    LOWER_LIMBS("Arti inf.", 0.4)
}

data class PasiRegionData(
    val erythema: Int? = null,
    val induration: Int? = null,
    val desquamation: Int? = null,
    val area: Int? = null
) {
    val isValid: Boolean
        get() = (erythema in 0..4 || erythema == null) &&
                (induration in 0..4 || induration == null) &&
                (desquamation in 0..4 || desquamation == null) &&
                (area in 0..6 || area == null)
}

data class PasiUiState(
    val currentRegion: BodyRegion = BodyRegion.HEAD,
    val regionsData: Map<BodyRegion, PasiRegionData> = BodyRegion.values().associateWith { PasiRegionData() },
    val calculatedScore: Double? = null,
    val validationError: String? = null
)

class PasiViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PasiUiState())
    val uiState: StateFlow<PasiUiState> = _uiState.asStateFlow()

    fun selectRegion(region: BodyRegion) {
        _uiState.update { 
            it.copy(
                currentRegion = region,
                validationError = null
            ) 
        }
    }

    fun updateParameter(
        erythema: Int? = null,
        induration: Int? = null,
        desquamation: Int? = null,
        area: Int? = null
    ) {
        val currentState = _uiState.value
        val region = currentState.currentRegion
        val currentData = currentState.regionsData[region] ?: PasiRegionData()

        val e = erythema ?: currentData.erythema
        val i = induration ?: currentData.induration
        val d = desquamation ?: currentData.desquamation
        val a = area ?: currentData.area

        var error: String? = null
        if ((e != null && e !in 0..4) || (i != null && i !in 0..4) || (d != null && d !in 0..4)) {
            error = "I valori delle lesioni devono essere compresi tra 0 e 4."
        } else if (a != null && a !in 0..6) {
            error = "L'area deve essere compresa tra 0 e 6."
        }

        val updatedData = PasiRegionData(e, i, d, a)
        val updatedMap = currentState.regionsData.toMutableMap().apply {
            put(region, updatedData)
        }

        _uiState.update {
            it.copy(
                regionsData = updatedMap,
                validationError = error
            )
        }
    }

    fun calculateScore() {
        val state = _uiState.value
        
        if (state.validationError != null) return

        var totalScore = 0.0
        for ((region, data) in state.regionsData) {
            val e = data.erythema ?: 0
            val i = data.induration ?: 0
            val d = data.desquamation ?: 0
            val a = data.area ?: 0

            val regionScore = (e + i + d) * a * region.weight
            totalScore += regionScore
        }

        _uiState.update { it.copy(calculatedScore = totalScore) }
    }
    
    fun getClinicalInterpretation(score: Double): String {
        return when {
            score == 0.0 -> "Niente"
            score < 10.0 -> "Lieve"
            score <= 20.0 -> "Moderata"
            else -> "Severa"
        }
    }
}
