package com.example.comisapp.data.models

import com.google.firebase.Timestamp

data class NotificationModel(
    val notificacionId: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val tipo: String = "",
    val destinatarioId: String = "",
    val leido: Boolean = false,
    val fechaEnvioNotificacion: Timestamp? = null
)