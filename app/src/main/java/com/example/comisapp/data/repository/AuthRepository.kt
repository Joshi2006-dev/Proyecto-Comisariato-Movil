package com.example.comisapp.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.messaging.FirebaseMessaging

class AuthRepository {
    private val auth    = FirebaseAuth.getInstance()
    private val db      = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    suspend fun login(email: String, pass: String) {
        val userDoc = db.collection("usuarios")
            .document(email)
            .get()
            .await()

        if (userDoc.exists()) {
            val estado = userDoc.getString("estado") ?: "INACTIVO"
            if (estado == "INACTIVO") {

                throw Exception("Tu cuenta está INACTIVA. Comunícate con los administradores.")
            }
        } else {
            throw Exception("Este correo no está registrado.")
        }
        try {
            auth.signInWithEmailAndPassword(email, pass).await()
        } catch (e: Exception) {

            throw Exception("Correo o contraseña incorrectos")
        }
    }


    //Logout
    suspend fun logout() {
        try {
            val email = auth.currentUser?.email
            if (email != null) {
                // Borra el token de Firestore
                db.collection("usuarios")
                    .document(email)
                    .update("fcmToken", null)
                    .await()
            }
            // Borra el token local de FCM
            FirebaseMessaging.getInstance().deleteToken().await()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            auth.signOut()
        }
    }

    fun currentUser(): String{
        return auth.currentUser?.uid ?: ""
    }

    fun islogged(): Boolean { return auth.currentUser != null }

    suspend fun forgotPassword(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    suspend fun updatePassword(currentPass: String, newPass: String): Result<Unit> {
        return try {
            val user = auth.currentUser ?: throw Exception("Usuario no autenticado")
            val email = user.email ?: throw Exception("Email no encontrado")

            val credential = EmailAuthProvider.getCredential(email, currentPass)

            user.reauthenticate(credential).await()
            user.updatePassword(newPass).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }




    suspend fun register(email: String, pass: String) {
        //  Verificar si el email existe en la colección de Firestore
        val userCheck = db.collection("usuarios")
            .whereEqualTo("correo", email)
            .get()
            .await()

        if (userCheck.isEmpty) {

            throw Exception("Este correo no está autorizado para registrarse.")
        }

        // 2. Intentar crear el usuario en Auth
        // Si ya existe en Auth, Firebase lanzará una excepción automáticamente
        try {
            auth.createUserWithEmailAndPassword(email, pass).await()
        } catch (e: Exception) {
            if (e.message?.contains("already in use") == true) {
                throw Exception("Este usuario ya está registrado en el sistema.")
            } else {
                throw e
            }
        }
    }

}