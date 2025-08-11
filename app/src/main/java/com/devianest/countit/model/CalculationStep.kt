package com.devianest.countit.model

data class CalculationStep(
    val stepName: String,
    val description: String,
    val matrix: List<List<Double>>,
    val columnHeaders: List<String>,
    val rowHeaders: List<String>
)
