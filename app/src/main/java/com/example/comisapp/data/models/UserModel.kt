package com.example.comisapp.data.models

import com.google.firebase.Timestamp

data class UserModel(
    val id: String = "",
    val claveModificada: Boolean = false,
    val correo: String = "",
    val empleadoId: String = "",
    val estado: String = "",
    val fechaModificacion: Timestamp?=null,
    val fechaRegistro: Timestamp?=null,
    val imagenUrl: String = "",
    val nombre: String = "",
    val rol: List<String> = emptyList(),
    )
