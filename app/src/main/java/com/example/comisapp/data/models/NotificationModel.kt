package com.example.comisapp.data.models

import com.google.firebase.Timestamp

data class NotificationModel(
    val notificacionId: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val tipo: String = "",
    // CAMBIO CLAVE: Cambia 'destinatarioId' por 'usuarioId'
    val usuarioId: String = "",
    val leido: Boolean = false,
    val creditoId: String? = null,
    val fechaEnvioNotificacion: Timestamp? = null
)