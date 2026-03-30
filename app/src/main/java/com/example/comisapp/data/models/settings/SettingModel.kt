package com.example.comisapp.data.models.settings

data class SettingModel(
    val id : String = "",
    val CorreoNotificaciones:String = "",
    val porcSueldo: Double = 0.0,
    val mensajeEspera: String = "",
)