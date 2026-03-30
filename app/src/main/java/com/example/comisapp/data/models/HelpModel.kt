package com.example.comisapp.data.models
import com.google.firebase.Timestamp

data class AyudaModel(
    val ayudaId: String = "",
    val titulo: String = "",
    var explicacion: String = "",
    val fechaCreacion: Timestamp? = null,
    val fechaModificacion: Timestamp? = null
)