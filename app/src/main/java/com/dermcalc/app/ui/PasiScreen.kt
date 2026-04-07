package com.dermcalc.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dermcalc.app.viewmodel.PasiRegion
import com.dermcalc.app.viewmodel.PasiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasiScreen(
    onNavigateBack: () -> Unit,
    onNavigateToResult: (Float) -> Unit,
    viewModel: PasiViewModel = viewModel()
) {
    val currentStep by viewModel.currentStep.collectAsState()
    val regionStates by viewModel.regionStates.collectAsState()

    val currentRegion = PasiRegion.entries[currentStep]
    val currentState = regionStates[currentStep]

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PASI - ${currentRegion.title} (${currentStep + 1}/4)") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                PasiInputField(
                    label = "Eritema (0-4)",
                    value = currentState.erythema,
                    isError = currentState.erythemaError,
                    errorMessage = "Il valore deve essere compreso tra 0 e 4",
                    onValueChange = { viewModel.updateField(currentStep, "erythema", it) }
                )
                PasiInputField(
                    label = "Indurimento (0-4)",
                    value = currentState.induration,
                    isError = currentState.indurationError,
                    errorMessage = "Il valore deve essere compreso tra 0 e 4",
                    onValueChange = { viewModel.updateField(currentStep, "induration", it) }
                )
                PasiInputField(
                    label = "Desquamazione (0-4)",
                    value = currentState.desquamation,
                    isError = currentState.desquamationError,
                    errorMessage = "Il valore deve essere compreso tra 0 e 4",
                    onValueChange = { viewModel.updateField(currentStep, "desquamation", it) }
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Inserisci Area come Percentuale (%)", style = MaterialTheme.typography.bodyLarge)
                    Switch(
                        checked = currentState.isAreaPercentage,
                        onCheckedChange = { viewModel.toggleAreaMode(it) }
                    )
                }
                PasiInputField(
                    label = if (currentState.isAreaPercentage) "Area coperta (0-100%)" else "Area coperta (0-6)",
                    value = currentState.area,
                    isError = currentState.areaError,
                    errorMessage = if (currentState.isAreaPercentage) "Inserisci un valore % valido tra 0 e 100" else "Il valore deve essere compreso tra 0 e 6",
                    onValueChange = { viewModel.updateField(currentStep, "area", it) }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                if (currentStep > 0) {
                    OutlinedButton(
                        onClick = { viewModel.previousStep() },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                    ) {
                        Text(
                            "Indietro",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(16.dp))
                }

                Button(
                    onClick = {
                        if (currentStep < 3) {
                            viewModel.nextStep()
                        } else {
                            onNavigateToResult(viewModel.calculateTotalScore())
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    enabled = !currentState.hasError
                ) {
                    Text(
                        if (currentStep < 3) "Continua" else "Calcola",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun PasiInputField(
    label: String,
    value: String,
    isError: Boolean,
    errorMessage: String = "Valore non valido",
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            isError = isError,
            supportingText = if (isError) { { Text(errorMessage) } } else null,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f),
            shape = MaterialTheme.shapes.medium,
            colors = OutlinedTextFieldDefaults.colors(
                errorBorderColor = MaterialTheme.colorScheme.error,
                errorLabelColor = MaterialTheme.colorScheme.error,
                errorTextColor = MaterialTheme.colorScheme.error
            )
        )
        
        Column {
            IconButton(
                onClick = { 
                    val intVal = value.toIntOrNull() ?: 0
                    onValueChange((intVal + 1).toString()) 
                }
            ) { 
                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Aumenta") 
            }
            IconButton(
                onClick = { 
                    val intVal = value.toIntOrNull() ?: 0
                    onValueChange((maxOf(0, intVal - 1)).toString()) 
                }
            ) { 
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Diminuisci") 
            }
        }
    }
}
