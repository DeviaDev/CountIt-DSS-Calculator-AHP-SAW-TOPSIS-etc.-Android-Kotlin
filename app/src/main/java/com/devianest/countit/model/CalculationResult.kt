package com.devianest.countit.model

data class CalculationResult(
    val method: SPKMethod,
    val steps: List<CalculationStep>,
    val finalScores: List<Pair<String, Double>>,
    val ranking: List<Pair<String, Double>>,
    val conclusion: String
)
