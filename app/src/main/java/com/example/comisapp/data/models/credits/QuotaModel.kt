package com.example.comisapp.data.models.credits

import com.google.firebase.Timestamp

data class QuotaModel(
    val id: String = "",
    val creditId: String = "",
    val numeroCuota: Int = 0,
    val fechaRegistro: Timestamp? = null,
    val monto: Double = 0.0,
)
