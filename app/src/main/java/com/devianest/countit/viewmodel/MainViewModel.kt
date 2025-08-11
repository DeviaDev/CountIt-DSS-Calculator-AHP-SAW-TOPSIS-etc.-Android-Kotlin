package com.devianest.countit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devianest.countit.model.*
import com.devianest.countit.calculator.SPKCalculatorFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProjectUiState())
    val uiState: StateFlow<ProjectUiState> = _uiState.asStateFlow()

    private val _calculationResult = MutableStateFlow<CalculationResult?>(null)
    val calculationResult: StateFlow<CalculationResult?> = _calculationResult.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(title = title)
    }

    fun updateMethod(method: SPKMethod) {
        _uiState.value = _uiState.value.copy(method = method)
    }

    fun addCategory(name: String, type: CriteriaType = CriteriaType.BENEFIT) {
        val currentCategories = _uiState.value.categories.toMutableList()
        val newCategory = Category(
            id = UUID.randomUUID().toString(),
            name = name,
            type = type
        )
        currentCategories.add(newCategory)
        _uiState.value = _uiState.value.copy(categories = currentCategories)
    }

    fun removeCategory(categoryId: String) {
        val currentCategories = _uiState.value.categories.toMutableList()
        currentCategories.removeAll { it.id == categoryId }
        _uiState.value = _uiState.value.copy(categories = currentCategories)

        // Reset matrix if categories changed
        resetMatrix()
    }

    fun addAlternative(name: String) {
        val currentAlternatives = _uiState.value.alternatives.toMutableList()
        val newAlternative = Alternative(
            id = UUID.randomUUID().toString(),
            name = name
        )
        currentAlternatives.add(newAlternative)
        _uiState.value = _uiState.value.copy(alternatives = currentAlternatives)
    }

    fun removeAlternative(alternativeId: String) {
        val currentAlternatives = _uiState.value.alternatives.toMutableList()
        currentAlternatives.removeAll { it.id == alternativeId }
        _uiState.value = _uiState.value.copy(alternatives = currentAlternatives)

        // Reset matrix if alternatives changed
        resetMatrix()
    }

    fun updateMatrix(matrix: List<List<Double>>) {
        _uiState.value = _uiState.value.copy(matrix = matrix)
    }

    fun updateWeights(weights: List<Double>) {
        _uiState.value = _uiState.value.copy(weights = weights)
    }

    fun updateMatrixCell(row: Int, col: Int, value: Double) {
        val currentMatrix = _uiState.value.matrix.toMutableList()
        if (row < currentMatrix.size && col < currentMatrix[row].size) {
            val currentRow = currentMatrix[row].toMutableList()
            currentRow[col] = value
            currentMatrix[row] = currentRow
            _uiState.value = _uiState.value.copy(matrix = currentMatrix)
        }
    }

    fun initializeMatrix() {
        val rows = _uiState.value.alternatives.size
        val cols = _uiState.value.categories.size

        if (rows > 0 && cols > 0) {
            val matrix = List(rows) { List(cols) { 0.0 } }
            val weights = List(cols) { 0.0 }

            _uiState.value = _uiState.value.copy(
                matrix = matrix,
                weights = weights
            )
        }
    }

    private fun resetMatrix() {
        _uiState.value = _uiState.value.copy(
            matrix = emptyList(),
            weights = emptyList()
        )
    }

    fun calculateResult() {
        val state = _uiState.value

        if (!isDataValid(state)) {
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val calculator = SPKCalculatorFactory.getCalculator(state.method!!)
                val result = calculator.calculate(
                    alternatives = state.alternatives.map { it.name },
                    criteria = state.categories.map { it.name },
                    matrix = state.matrix,
                    weights = state.weights,
                    criteriaTypes = state.categories.map { it.type }
                )
                _calculationResult.value = result
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun isDataValid(state: ProjectUiState): Boolean {
        return state.title.isNotBlank() &&
                state.method != null &&
                state.categories.isNotEmpty() &&
                state.alternatives.isNotEmpty() &&
                state.matrix.isNotEmpty() &&
                state.weights.isNotEmpty() &&
                state.weights.all { it > 0.0 } &&
                state.matrix.all { row -> row.all { it >= 0.0 } }
    }

    fun resetProject() {
        _uiState.value = ProjectUiState()
        _calculationResult.value = null
    }
}

data class ProjectUiState(
    val title: String = "",
    val method: SPKMethod? = null,
    val categories: List<Category> = emptyList(),
    val alternatives: List<Alternative> = emptyList(),
    val matrix: List<List<Double>> = emptyList(),
    val weights: List<Double> = emptyList()
)