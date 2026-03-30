package com.example.comisapp.data.models

data class ProductModel(
    val id: String = "",
    val activo: Boolean? = false,
    val categoria: String? = "",
    val categoriaId: String? = "",
    val descripcion: String? = "",
    val imagenFrontalUrl: String? = "",
    val imagenLateralUrl: String? = "",
    val imagenTraseraUrl: String? = "",
    val nombre: String? = "",
    val precioContado: Double? = 0.0,
    val precioCredito: Double? = 0.0,
    val stock: Int? = 0,
    val garantiaMeses: Int? = 0
)