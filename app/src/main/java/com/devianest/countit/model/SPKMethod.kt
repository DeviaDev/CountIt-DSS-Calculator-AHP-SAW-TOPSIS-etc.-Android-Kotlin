package com.devianest.countit.model

enum class SPKMethod(val displayName: String) {
    SAW("Simple Additive Weighting (SAW)"),
    WP("Weighted Product (WP)"),
    TOPSIS("TOPSIS"),
    AHP("Analytical Hierarchy Process (AHP)"),
    ELECTRE("ELECTRE"),
    PROMETHEE("PROMETHEE"),
    MOORA("MOORA"),
    VIKOR("VIKOR")
}