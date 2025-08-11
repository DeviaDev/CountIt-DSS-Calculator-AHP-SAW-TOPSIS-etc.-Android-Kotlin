package com.devianest.countit.calculator

import com.devianest.countit.model.*

object SPKCalculatorFactory {
    fun getCalculator(method: SPKMethod): SPKCalculator {
        return when (method) {
            SPKMethod.SAW -> SAWCalculator()
            SPKMethod.WP -> WPCalculator()
            SPKMethod.TOPSIS -> TOPSISCalculator()
            SPKMethod.AHP -> AHPCalculator()
            SPKMethod.ELECTRE -> ELECTRECalculator()
            SPKMethod.PROMETHEE -> PROMETHEECalculator()
            SPKMethod.MOORA -> MOORACalculator()
            SPKMethod.VIKOR -> VIKORCalculator()
        }
    }
}