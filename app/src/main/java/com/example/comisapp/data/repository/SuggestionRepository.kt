package com.example.comisapp.data.repository

import com.example.comisapp.data.models.SuggestionModel
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class SuggestionRepository {

    private val db = Firebase.firestore
    private var lastDocument: DocumentSnapshot? = null

    suspend fun getAllSuggestions(): List<SuggestionModel> {
        val result = db.collection("sugerencias")
            .get()
            .await()
        return result.documents.mapNotNull { document ->
            val sugerencia = document.toObject(SuggestionModel::class.java)
            sugerencia?.copy(sugerenciaId = document.id)
        }
    }

    suspend fun getSugerencias(limit: Int): List<SuggestionModel> {
        var query = db.collection("sugerencias")
            .limit(limit.toLong())

        lastDocument?.let {
            query = query.startAfter(it)
        }

        val result = query.get().await()

        if (!result.isEmpty) {
            lastDocument = result.documents.last()
        }

        return result.documents.mapNotNull { document ->
            val sugerencia = document.toObject(SuggestionModel::class.java)
            sugerencia?.copy(sugerenciaId = document.id)
        }
    }

    suspend fun addSugerencia(usuarioId: String, asunto: String, descripcion: String) {
        val now = Timestamp.now()
        val nuevaSugerencia = SuggestionModel(
            usuarioId = usuarioId,
            asunto = asunto,
            descripcion = descripcion,
            fechaRegistro = now,
            fechaModificacion = now
        )
        val docRef = db.collection("sugerencias").add(nuevaSugerencia).await()
        db.collection("sugerencias").document(docRef.id)
            .update("sugerenciaId", docRef.id)
            .await()
    }

    fun resetPagination() {
        lastDocument = null
    }
}