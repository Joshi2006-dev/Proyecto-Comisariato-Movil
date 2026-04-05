package com.example.comisapp.ui.screens.secondaryNavigationScreens

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

fun ejecutarBiometria(
    activity: FragmentActivity,
    onExito: () -> Unit
) {
    val executor = ContextCompat.getMainExecutor(activity)
    val biometricPrompt = BiometricPrompt(activity, executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                // Usamos un pequeño delay para que el diálogo del sistema se cierre limpio
                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                    onExito()
                }, 150)
            }
        })

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Confirmar Solicitud")
        .setSubtitle("Autentícate para procesar tu pedido en ComisApp")
        .setAllowedAuthenticators(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )
        .build()

    biometricPrompt.authenticate(promptInfo)
}