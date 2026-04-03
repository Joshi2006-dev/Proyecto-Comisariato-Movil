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

    //login
    suspend fun login(email: String, pass: String) {
        auth.signInWithEmailAndPassword(email, pass).await()
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

}