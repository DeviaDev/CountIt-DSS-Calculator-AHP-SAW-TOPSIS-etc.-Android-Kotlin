package com.devianest.countit.calculator

import com.devianest.countit.model.*
import kotlin.math.max

class SAWCalculator : SPKCalculator {
    override suspend fun calculate(
        alternatives: List<String>,
        criteria: List<String>,
        matrix: List<List<Double>>,
        weights: List<Double>,
        criteriaTypes: List<CriteriaType>
    ): CalculationResult {
        val steps = mutableListOf<CalculationStep>()

        // Step 1: Original Matrix
        steps.add(
            CalculationStep(
                stepName = "Matriks Keputusan Awal",
                description = "Matriks awal yang berisi nilai setiap alternatif terhadap kriteria",
                matrix = matrix,
                columnHeaders = criteria,
                rowHeaders = alternatives
            )
        )

        // Step 2: Normalization
        val normalizedMatrix = normalizeMatrix(matrix, criteriaTypes)
        steps.add(
            CalculationStep(
                stepName = "Matriks Normalisasi",
                description = "Matriks yang telah dinormalisasi menggunakan rumus SAW",
                matrix = normalizedMatrix,
                columnHeaders = criteria,
                rowHeaders = alternatives
            )
        )

        // Step 3: Weighted Matrix
        val weightedMatrix = applyWeights(normalizedMatrix, weights)
        steps.add(
            CalculationStep(
                stepName = "Matriks Terbobot",
                description = "Matriks normalisasi dikalikan dengan bobot masing-masing kriteria",
                matrix = weightedMatrix,
                columnHeaders = criteria.mapIndexed { i, name -> "$name (${weights[i]})" },
                rowHeaders = alternatives
            )
        )

        // Step 4: Final Scores
        val finalScores = calculateFinalScores(weightedMatrix, alternatives)
        val finalScoreMatrix = finalScores.map { listOf(it.second) }
        steps.add(
            CalculationStep(
                stepName = "Nilai Preferensi",
                description = "Hasil penjumlahan nilai terbobot untuk setiap alternatif",
                matrix = finalScoreMatrix,
                columnHeaders = listOf("Nilai Preferensi"),
                rowHeaders = alternatives
            )
        )

        // Ranking
        val ranking = finalScores.sortedByDescending { it.second }

        // Conclusion
        val conclusion = "Berdasarkan perhitungan menggunakan metode SAW (Simple Additive Weighting), " +
                "alternatif terbaik adalah '${ranking.first().first}' dengan nilai preferensi ${String.format("%.4f", ranking.first().second)}."

        return CalculationResult(
            method = SPKMethod.SAW,
            steps = steps,
            finalScores = finalScores,
            ranking = ranking,
            conclusion = conclusion
        )
    }

    private fun normalizeMatrix(matrix: List<List<Double>>, criteriaTypes: List<CriteriaType>): List<List<Double>> {
        val normalizedMatrix = mutableListOf<List<Double>>()

        for (row in matrix) {
            val normalizedRow = mutableListOf<Double>()
            for (colIndex in row.indices) {
                val columnValues = matrix.map { it[colIndex] }
                val normalizedValue = when (criteriaTypes[colIndex]) {
                    CriteriaType.BENEFIT -> {
                        val maxValue = columnValues.maxOrNull() ?: 1.0
                        if (maxValue != 0.0) row[colIndex] / maxValue else 0.0
                    }
                    CriteriaType.COST -> {
                        val minValue = columnValues.minOrNull() ?: 1.0
                        if (row[colIndex] != 0.0) minValue / row[colIndex] else 0.0
                    }
                }
                normalizedRow.add(normalizedValue)
            }
            normalizedMatrix.add(normalizedRow)
        }

        return normalizedMatrix
    }

    private fun applyWeights(matrix: List<List<Double>>, weights: List<Double>): List<List<Double>> {
        return matrix.map { row ->
            row.mapIndexed { index, value -> value * weights[index] }
        }
    }

    private fun calculateFinalScores(weightedMatrix: List<List<Double>>, alternatives: List<String>): List<Pair<String, Double>> {
        return weightedMatrix.mapIndexed { index, row ->
            alternatives[index] to row.sum()
        }
    }
}






private fun createPlaceholderResult(
    method: SPKMethod,
    alternatives: List<String>,
    criteria: List<String>,
    matrix: List<List<Double>>
): CalculationResult {
    val steps = listOf(
        CalculationStep(
            stepName = "Matriks Keputusan",
            description = "Implementasi untuk metode ${method.displayName} akan segera tersedia",
            matrix = matrix,
            columnHeaders = criteria,
            rowHeaders = alternatives
        )
    )

    // Simple placeholder ranking berdasarkan rata-rata
    val scores = alternatives.mapIndexed { index, name ->
        val average = if (matrix[index].isNotEmpty()) matrix[index].average() else 0.0
        name to average
    }.sortedByDescending { it.second }

    return CalculationResult(
        method = method,
        steps = steps,
        finalScores = scores,
        ranking = scores,
        conclusion = "Implementasi lengkap untuk metode ${method.displayName} akan segera tersedia. " +
                "Hasil sementara menunjukkan '${scores.firstOrNull()?.first ?: ""}' sebagai alternatif terbaik."
    )
}