package com.dermcalc.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dermcalc.app.viewmodel.BodyRegion
import com.dermcalc.app.viewmodel.PasiRegionData
import com.dermcalc.app.viewmodel.PasiViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasiScreen(
    onClose: () -> Unit,
    viewModel: PasiViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Schermata Risultati
    if (uiState.calculatedScore != null) {
        ResultScreen(
            score = uiState.calculatedScore!!,
            interpretation = viewModel.getClinicalInterpretation(uiState.calculatedScore!!),
            onClose = onClose
        )
        return
    }

    // Schermata Calcolatore
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calcolatore PASI", color = PrimaryBlue, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Chiudi", tint = PrimaryBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundWhite)
            )
        },
        containerColor = BackgroundWhite
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Dropdown Menu (Menu a tendina)
            RegionSelector(
                currentRegion = uiState.currentRegion,
                onRegionSelected = { viewModel.selectRegion(it) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Error Message
            if (uiState.validationError != null) {
                Text(
                    text = uiState.validationError!!,
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 16.dp),
                    textAlign = TextAlign.Center
                )
            }

            val regionData = uiState.regionsData[uiState.currentRegion] ?: PasiRegionData()

            // Input Fields
            ParameterInput(
                label = "Eritema (0 - 4)",
                value = regionData.erythema,
                onValueChange = { viewModel.updateParameter(erythema = it, induration = regionData.induration, desquamation = regionData.desquamation, area = regionData.area) },
                maxLimit = 4
            )
            
            ParameterInput(
                label = "Indurimento (0 - 4)",
                value = regionData.induration,
                onValueChange = { viewModel.updateParameter(erythema = regionData.erythema, induration = it, desquamation = regionData.desquamation, area = regionData.area) },
                maxLimit = 4
            )
            
            ParameterInput(
                label = "Desquamazione (0 - 4)",
                value = regionData.desquamation,
                onValueChange = { viewModel.updateParameter(erythema = regionData.erythema, induration = regionData.induration, desquamation = it, area = regionData.area) },
                maxLimit = 4
            )
            
            ParameterInput(
                label = "Area (0 - 6)",
                value = regionData.area,
                onValueChange = { viewModel.updateParameter(erythema = regionData.erythema, induration = regionData.induration, desquamation = regionData.desquamation, area = it) },
                maxLimit = 6
            )

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(24.dp))

            // Calcola Button
            Button(
                onClick = { viewModel.calculateScore() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                enabled = uiState.validationError == null
            ) {
                Text("Calcola PASI", fontSize = 18.sp, color = Color.White)
            }
        }
    }
}

@Composable
fun RegionSelector(
    currentRegion: BodyRegion,
    onRegionSelected: (BodyRegion) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = currentRegion.displayName,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Distretto Anatomico") },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Espandi menu",
                    modifier = Modifier.clickable { expanded = !expanded }
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = PrimaryBlue,
                focusedBorderColor = PrimaryBlue
            ),
            shape = RoundedCornerShape(12.dp)
        )
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            BodyRegion.values().forEach { region ->
                DropdownMenuItem(
                    text = { Text(region.displayName) },
                    onClick = {
                        onRegionSelected(region)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun ParameterInput(
    label: String,
    value: Int?,
    onValueChange: (Int) -> Unit,
    maxLimit: Int
) {
    val isError = value != null && (value < 0 || value > maxLimit)
    val displayValue = value?.toString() ?: "0"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        // Frecce direzionali / Pulsanti -
        Button(
            onClick = { 
                val current = value ?: 0
                if (current > 0) onValueChange(current - 1)
            },
            modifier = Modifier.size(40.dp),
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("-", color = Color.Black, fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.width(8.dp))

        OutlinedTextField(
            value = displayValue,
            onValueChange = { 
                val parsed = it.toIntOrNull() ?: 0
                onValueChange(parsed)
            },
            modifier = Modifier.width(80.dp),
            isError = isError,
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontSize = 18.sp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Frecce direzionali / Pulsanti +
        Button(
            onClick = { 
                val current = value ?: 0
                if (current < maxLimit) onValueChange(current + 1)
                else onValueChange(current + 1) // Lasciamo superare per mostrare l'errore come da PRD
            },
            modifier = Modifier.size(40.dp),
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("+", color = Color.Black, fontSize = 20.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    score: Double,
    interpretation: String,
    onClose: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Risultato PASI", color = PrimaryBlue, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Chiudi", tint = PrimaryBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundWhite)
            )
        },
        containerColor = BackgroundWhite
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Punteggio PASI",
                fontSize = 24.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .border(8.dp, PrimaryBlue, RoundedCornerShape(100.dp))
                    .background(Color(0xFFF0F4FA), RoundedCornerShape(100.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = String.format("%.1f", score),
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Interpretazione",
                fontSize = 18.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = interpretation,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = when(interpretation) {
                    "Niente", "Lieve" -> Color(0xFF4CAF50)
                    "Moderata" -> Color(0xFFFF9800)
                    else -> Color(0xFFF44336)
                }
            )

            Spacer(modifier = Modifier.height(48.dp))
            
            Text(
                text = "Salvato automaticamente in cronologia",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}
