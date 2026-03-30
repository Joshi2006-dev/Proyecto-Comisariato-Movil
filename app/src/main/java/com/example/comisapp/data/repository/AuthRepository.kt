package com.example.comisapp.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import com.google.firebase.auth.EmailAuthProvider

class AuthRepository {
    private val auth    = FirebaseAuth.getInstance()
    private val db      = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    //login
    suspend fun login(email: String, pass: String) {
        auth.signInWithEmailAndPassword(email, pass).await()
    }

    //Logout
    fun logout() { auth.signOut() }

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