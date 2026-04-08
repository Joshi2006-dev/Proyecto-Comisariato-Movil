package com.example.comisapp.ui.helpers

object CreditHelper {

    fun calcularProgreso(limiteUsado: Double, limiteTotal: Double): Float {
        if (limiteTotal <= 0.0) return 0f
        val raw = limiteUsado / limiteTotal
        return (Math.round(raw * 10_000) / 10_000.0).toFloat() // 4 decimales → 2 en %
    }
}