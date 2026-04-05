package com.dermcalc.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dermcalc.app.viewmodel.EasiRegion
import com.dermcalc.app.viewmodel.EasiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EasiScreen(
    onNavigateBack: () -> Unit,
    onNavigateToResult: (Float) -> Unit,
    viewModel: EasiViewModel = viewModel()
) {
    val currentStep by viewModel.currentStep.collectAsState()
    val regionStates by viewModel.regionStates.collectAsState()

    val currentRegion = EasiRegion.entries[currentStep]
    val currentState = regionStates[currentStep]

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("EASI - ${currentRegion.title} (${currentStep + 1}/4)") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                    label = "Eritema (0-3)",
                    value = currentState.erythema,
                    isError = currentState.erythemaError,
                    onValueChange = { viewModel.updateField(currentStep, "erythema", it) }
                )
                PasiInputField(
                    label = "Edema/Papule (0-3)",
                    value = currentState.edema,
                    isError = currentState.edemaError,
                    onValueChange = { viewModel.updateField(currentStep, "edema", it) }
                )
                PasiInputField(
                    label = "Escoriazioni (0-3)",
                    value = currentState.excoriation,
                    isError = currentState.excoriationError,
                    onValueChange = { viewModel.updateField(currentStep, "excoriation", it) }
                )
                PasiInputField(
                    label = "Lichenificazione (0-3)",
                    value = currentState.lichenification,
                    isError = currentState.lichenificationError,
                    onValueChange = { viewModel.updateField(currentStep, "lichenification", it) }
                )
                PasiInputField(
                    label = "Area coperta (0-6)",
                    value = currentState.area,
                    isError = currentState.areaError,
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