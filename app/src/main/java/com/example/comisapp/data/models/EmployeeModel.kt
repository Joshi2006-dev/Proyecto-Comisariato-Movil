package com.example.comisapp.data.models

data class EmployeeModel(
    val id: String = "",
    val nombres: String = "",
    val apellidos: String = "",
    val cargo: String = "",
    val correo: String = "",
    val departamento: String = "",
    val dni: String = "",
    val estado: String = "",
    val fechaIngreso: String = "",
    val limiteCredito:Double=0.0,
    val salario:Double=0.0,
    val telefono: String = "",
    val fotoUrl: String = "",
)
