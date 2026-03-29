package com.example.comisapp.data.models

import com.google.firebase.Timestamp

data class SuggestionModel(
    val usuarioId: String = "",
    val asunto: String = "",
    val descripcion: String = "",
    val fechaModificacion: Timestamp? = null,
    val fechaRegistro: Timestamp? = null,
    val  sugerenciaId: String = ""


    )
