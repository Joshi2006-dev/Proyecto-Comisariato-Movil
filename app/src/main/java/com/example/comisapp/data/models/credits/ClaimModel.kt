package com.example.comisapp.data.models.credits

import com.google.firebase.Timestamp

data class ClaimModel(
    val id: String = "",
    val asunto: String = "",
    val atendidoPor: String? = "",
    val descripcion: String = "",
    val estado: String = "",
    val evidenciaUrl: String = "",
    val fechaEmision: Timestamp? = null,
    val fechaRespuesta: Timestamp? = null,
    val respuesta: String? = "",
)
