package com.devianest.countit.model

data class Category(
    val id: String,
    val name: String,
    val type: CriteriaType = CriteriaType.BENEFIT
)
