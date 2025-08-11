package com.devianest.countit.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.devianest.countit.utils.ExcelReader
import com.devianest.countit.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatrixInputScreen(
    navController: NavController,
    viewModel: MainViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val excelData = ExcelReader.readExcelMatrix(context, it)
            excelData?.let { data ->
                viewModel.updateMatrix(data.matrix)
                // Auto-populate alternatives and categories if they match
                if (data.rowHeaders.isNotEmpty() && data.columnHeaders.isNotEmpty()) {
                    // You might want to show a dialog to confirm overwrite
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Input Matriks") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { filePickerLauncher.launch("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") }
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Import Excel")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Progress indicator
                LinearProgressIndicator(
                    progress = { 0.75f },
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Langkah 3 dari 4",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Excel import instruction
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Anda dapat mengimport data dari file Excel dengan menekan tombol upload di atas",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // Matrix input
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Matriks Keputusan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (uiState.matrix.isNotEmpty() && uiState.categories.isNotEmpty() && uiState.alternatives.isNotEmpty()) {
                            // Matrix table
                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState())
                            ) {
                                Column {
                                    // Empty cell for top-left
                                    Box(
                                        modifier = Modifier
                                            .width(120.dp)
                                            .height(48.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "Alternatif/Kriteria",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center
                                        )
                                    }

                                    // Alternative names (row headers)
                                    uiState.alternatives.forEach { alternative ->
                                        Box(
                                            modifier = Modifier
                                                .width(120.dp)
                                                .height(56.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = alternative.name,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Medium,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }

                                // Matrix columns
                                uiState.categories.forEachIndexed { colIndex, category ->
                                    Column {
                                        // Column header (criteria name)
                                        Box(
                                            modifier = Modifier
                                                .width(80.dp)
                                                .height(48.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = category.name,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Bold,
                                                textAlign = TextAlign.Center
                                            )
                                        }

                                        // Matrix cells
                                        uiState.matrix.forEachIndexed { rowIndex, row ->
                                            OutlinedTextField(
                                                value = if (colIndex < row.size) row[colIndex].toString() else "0.0",
                                                onValueChange = { value ->
                                                    val doubleValue = value.toDoubleOrNull() ?: 0.0
                                                    viewModel.updateMatrixCell(rowIndex, colIndex, doubleValue)
                                                },
                                                modifier = Modifier
                                                    .width(80.dp)
                                                    .height(56.dp),
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                                textStyle = androidx.compose.ui.text.TextStyle(textAlign = TextAlign.Center)
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            Text(
                                text = "Silakan tambahkan kriteria dan alternatif terlebih dahulu",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Weights input
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Bobot Kriteria",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Masukkan bobot untuk setiap kriteria (total harus = 1.0)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        uiState.categories.forEachIndexed { index, category ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = category.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.weight(1f)
                                )

                                OutlinedTextField(
                                    value = if (index < uiState.weights.size) uiState.weights[index].toString() else "0.0",
                                    onValueChange = { value ->
                                        val doubleValue = value.toDoubleOrNull() ?: 0.0
                                        val newWeights = uiState.weights.toMutableList()
                                        if (index < newWeights.size) {
                                            newWeights[index] = doubleValue
                                        }
                                        viewModel.updateWeights(newWeights)
                                    },
                                    modifier = Modifier.width(100.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    textStyle = androidx.compose.ui.text.TextStyle(textAlign = TextAlign.Center)
                                )
                            }
                        }

                        // Weight sum indicator
                        val weightSum = uiState.weights.sum()
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "Total: ${String.format("%.3f", weightSum)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (kotlin.math.abs(weightSum - 1.0) < 0.001) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.error
                                }
                            )
                        }
                    }
                }
            }

            // Calculate button
            item {
                val canCalculate = uiState.matrix.isNotEmpty() &&
                        uiState.weights.isNotEmpty() &&
                        uiState.weights.all { it > 0.0 } &&
                        kotlin.math.abs(uiState.weights.sum() - 1.0) < 0.001

                Button(
                    onClick = {
                        viewModel.calculateResult()
                        navController.navigate("results")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = canCalculate,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Favorite, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Mulai Hitung")
                }

                if (!canCalculate) {
                    Text(
                        text = "Pastikan semua data telah diisi dan total bobot = 1.0",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}