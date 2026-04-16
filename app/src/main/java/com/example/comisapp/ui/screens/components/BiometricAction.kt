package com.example.comisapp.ui.screens.components

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import com.example.comisapp.ui.helpers.BiometricHelper

@Composable
fun BiometricAction(
    title: String = "Verificación biométrica",
    subtitle: String = "Confirma tu identidad para continuar",
    onNotAvailable: () -> Unit = {},
    content: @Composable (trigger: (() -> Unit) -> Unit) -> Unit
) {
    val context = LocalContext.current
    val activity = context as FragmentActivity

    val helper = remember {
        BiometricHelper(activity, title, subtitle)
    }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    content { onSuccess ->
        if (helper.canAuthenticate()) {
            helper.authenticate(
                onSuccess = onSuccess,
                onError = { msg -> errorMessage = msg }
            )
        } else {
            onNotAvailable()
        }
    }

    errorMessage?.let { _ -> }
}