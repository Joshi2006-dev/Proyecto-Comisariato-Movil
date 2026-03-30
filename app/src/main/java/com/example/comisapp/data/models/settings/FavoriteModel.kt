package com.example.comisapp.data.models.settings

import com.google.firebase.Timestamp

data class FavoriteModel(
    val uid: String = "",
    val marcado: Boolean = false,
    val fechaGuardado: Timestamp? = null,

    )
