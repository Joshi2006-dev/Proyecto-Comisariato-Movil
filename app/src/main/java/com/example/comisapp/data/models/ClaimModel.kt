package com.example.comisapp.data.models

import com.google.firebase.Timestamp

data class ClaimModel(
    val id: String = "",
    val asunto: String = "",
    val descripcion: String = "",
    val estado: String = "",
    val respuesta: String? = null,
    val fechaEmision: Timestamp? = null,
    val fechaInicioRevision: Timestamp? = null,
    val revisadoPor: String? = null,
    val imagenURL: String = "",
    val usuarioId: String = ""
)