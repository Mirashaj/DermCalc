package com.dermcalc.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dermcalc.app.viewmodel.BmiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BmiScreen(
    onNavigateBack: () -> Unit,
    onNavigateToResult: (Float) -> Unit,
    viewModel: BmiViewModel = viewModel()
) {
    val state by viewModel.bmiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Indice di Massa Corporea (BMI)") },
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
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                OutlinedTextField(
                    value = state.weightKg,
                    onValueChange = { viewModel.updateWeight(it) },
                    label = { Text("Peso (kg)") },
                    isError = state.weightError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )
                
                OutlinedTextField(
                    value = state.heightCm,
                    onValueChange = { viewModel.updateHeight(it) },
                    label = { Text("Altezza (cm)") },
                    isError = state.heightError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )
            }

            Button(
                onClick = { onNavigateToResult(viewModel.calculateBmi()) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = state.isValid
            ) {
                Text(
                    "Calcola BMI",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}