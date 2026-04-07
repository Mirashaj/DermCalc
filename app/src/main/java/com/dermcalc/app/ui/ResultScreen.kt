package com.dermcalc.app.ui

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dermcalc.app.data.CalculationRecord
import com.dermcalc.app.data.DermCalcDatabase
import kotlinx.coroutines.launch

class ResultViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = DermCalcDatabase.getDatabase(application).calculationDao()

    fun saveResult(type: String, score: Float, interpretation: String) {
        viewModelScope.launch {
            dao.insertRecord(
                CalculationRecord(
                    calculatorType = type,
                    score = score,
                    resultInterpretation = interpretation
                )
            )
        }
    }
    
    fun interpretResult(type: String, score: Float): String {
        if (type == "PASI") {
            return when {
                score < 5 -> "Lieve"
                score <= 10 -> "Moderata"
                else -> "Severa"
            }
        } else if (type == "EASI") {
            return when {
                score == 0f -> "Sano"
                score <= 1.0f -> "Quasi Sano"
                score <= 7.0f -> "Lieve"
                score <= 21.0f -> "Moderata"
                score <= 50.0f -> "Severa"
                else -> "Molto Severa"
            }
        } else if (type == "BMI") {
            return when {
                score < 18.5f -> "Sottopeso"
                score < 25.0f -> "Normopeso"
                score < 30.0f -> "Sovrappeso"
                score < 35.0f -> "Obesità Grado I"
                score < 40.0f -> "Obesità Grado II (Severa)"
                else -> "Obesità Grado III (Molto Severa)"
            }
        } else if (type == "BSA") {
            return "m²"
        }
        return "Sconosciuta"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    score: Float,
    calculatorType: String,
    onNavigateHome: () -> Unit,
    viewModel: ResultViewModel = viewModel()
) {
    val interpretation = remember(score) { viewModel.interpretResult(calculatorType, score) }
    
    LaunchedEffect(Unit) {
        viewModel.saveResult(calculatorType, score, interpretation)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Risultato $calculatorType") },
                navigationIcon = {
                    IconButton(onClick = onNavigateHome) {
                        Icon(Icons.Default.Close, contentDescription = "Chiudi e torna alla Home")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Punteggio:",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            
            if (calculatorType == "BSA") {
                Text(
                    text = String.format("%.2f m²", score), 
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Text(
                    text = String.format("%.1f", score),
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(48.dp))
            
            if (calculatorType != "BSA") {
                Text(
                    text = "Interpretazione clinica:",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = interpretation,
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (interpretation.contains("Severa")) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(64.dp))
            } else {
                Spacer(modifier = Modifier.height(64.dp))
            }
            
            Button(
                onClick = onNavigateHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "Torna alla Home",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }
        }
    }
}
