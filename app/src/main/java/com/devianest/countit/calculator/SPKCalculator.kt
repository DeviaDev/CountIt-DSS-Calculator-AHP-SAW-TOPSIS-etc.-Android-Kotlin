package com.devianest.countit.calculator

import com.devianest.countit.model.*

interface SPKCalculator {
    suspend fun calculate(
        alternatives: List<String>,
        criteria: List<String>,
        matrix: List<List<Double>>,
        weights: List<Double>,
        criteriaTypes: List<CriteriaType>
    ): CalculationResult
}