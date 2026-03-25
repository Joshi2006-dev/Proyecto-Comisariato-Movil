package com.example.comisapp.ui.navigation

import com.example.comisapp.R

sealed class BottomBarScreen(var route: String, var icon: Int, var title: String) {
    object Home : BottomBarScreen("home", R.drawable.ic_home, "Inicio")
    object Catalogo : BottomBarScreen("catalogo", R.drawable.ic_category, "Catálogo")
    object Creditos : BottomBarScreen("creditos", R.drawable.ic_creditos, "Créditos")
    object Guardados : BottomBarScreen("guardados", R.drawable.ic_favorite, "Guardados")
}