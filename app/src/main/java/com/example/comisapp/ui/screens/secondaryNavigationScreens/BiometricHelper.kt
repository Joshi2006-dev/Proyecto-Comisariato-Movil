package com.example.comisapp.ui.screens.secondaryNavigationScreens

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

fun ejecutarBiometria(
    activity: FragmentActivity,
    subtitulo: String = "Autentícate para procesar tu pedido en ComisApp",
    onExito: () -> Unit
) {
    val executor = ContextCompat.getMainExecutor(activity)
    val biometricPrompt = BiometricPrompt(activity, executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    onExito()
                }, 150)
            }
        })

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Confirmar Solicitud")
        .setSubtitle(subtitulo) // <--- AQUÍ USA EL PARÁMETRO
        .setAllowedAuthenticators(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )
        .build()

    biometricPrompt.authenticate(promptInfo)
}