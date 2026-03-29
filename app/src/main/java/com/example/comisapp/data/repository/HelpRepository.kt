
package com.example.comisapp.data.repository

import com.example.comisapp.data.models.AyudaModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class AyudaRepository {

    private val db = Firebase.firestore

    suspend fun getAllAyudas(): List<AyudaModel> {
        val result = db.collection("ayudas")
            .get()
            .await()
        return result.documents.mapNotNull { document ->
            val ayuda = document.toObject(AyudaModel::class.java)
            ayuda?.copy(ayudaId = document.id)
        }
    }
}