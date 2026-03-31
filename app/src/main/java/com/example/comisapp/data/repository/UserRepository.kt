package com.example.comisapp.data.repository

import com.example.comisapp.data.models.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    suspend fun getCurrentUser(): UserModel? {
        val uid = auth.currentUser?.email ?: return null
        return try {
            val doc = db.collection("usuarios")
                .document(uid)
                .get().await()
            if (doc.exists()) {
                doc.toObject(UserModel::class.java)?.copy(id = doc.id)
            } else null
        } catch (e: Exception) {
            null
        }
    }
}