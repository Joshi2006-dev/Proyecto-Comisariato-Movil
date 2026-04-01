package com.example.comisapp.data.models.credits

import com.google.firebase.Timestamp

data class CreditModel(
    val id: String = "",
    val cantidad: Int = 0,
    val cuotaMensual: Double = 0.0,
    val estado: String = "",
    val fechaInicio: Timestamp? = null,
    val fechaRegistro: Timestamp? = null,
    val fechaRevision: Timestamp? = null,
    val imagenProductoURL: String = "",
    val nombreProducto: String = "",
    val plazoMeses: Int = 0,
    val productoId: String = "",
    val revisadoPor: String = "",
    val totalCredito: Double = 0.0,
    val usuarioId: String = "",
    val usuario: String = "",
    val cuotasPagadas: Int = 0,
    val respuesta: String? = "",
    val empleadoId: String? = ""
)
