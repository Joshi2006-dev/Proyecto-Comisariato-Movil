package com.example.comisapp.data.repository

import com.example.comisapp.data.models.SuggestionModel
import com.example.comisapp.data.models.UserModel
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class SuggestionRepository {

    private val db = Firebase.firestore
    private var lastDocument: DocumentSnapshot? = null

    private val currentUserId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

    suspend fun getAllSuggestions(): List<SuggestionModel> {
        val userId = currentUserId ?: return emptyList()

        val result = db.collection("sugerencias")
            .whereEqualTo("usuarioId", userId)
            .get()
            .await()
        return result.documents.mapNotNull { document ->
            val sugerencia = document.toObject(SuggestionModel::class.java)
            sugerencia?.copy(sugerenciaId = document.id)
        }
    }

    suspend fun getSugerencias(limit: Int): List<SuggestionModel> {
        val userId = currentUserId ?: return emptyList()

        var query = db.collection("sugerencias")
            .whereEqualTo("usuarioId", userId)
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

    suspend fun addSugerencia(
        usuarioId: String,
        asunto: String,
        descripcion: String,
        usuario: UserModel
    ) {
        try {
            val now = Timestamp.now()

            val newDocRef = db.collection("sugerencias").document()
            val sugerenciaId = newDocRef.id

            val sugerenciaMap = hashMapOf(
                "sugerenciaId" to sugerenciaId,
                "usuarioId" to usuarioId,
                "asunto" to asunto,
                "descripcion" to descripcion,
                "fechaRegistro" to now,
                "fechaModificacion" to now,
                "nombreUsuario" to usuario.nombre,
                "emailUsuario" to usuario.correo,
                "fotoUsuario" to usuario.fotoUrl
            )

            newDocRef.set(sugerenciaMap).await()

            println("Sugerencia agregada con ID: $sugerenciaId")

        } catch (e: Exception) {
            throw e
        }
    }
    fun resetPagination() {
        lastDocument = null
    }
}