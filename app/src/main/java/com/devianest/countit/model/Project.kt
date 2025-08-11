package com.devianest.countit.model

data class Project(
    val title: String = "",
    val method: SPKMethod? = null,
    val categories: List<Category> = emptyList(),
    val alternatives: List<Alternative> = emptyList(),
    val matrix: List<List<Double>> = emptyList(),
    val weights: List<Double> = emptyList(),
    val isCompleted: Boolean = false
)