package com.example.comisapp.data.repository

import com.example.comisapp.data.models.CategoryModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class CategoryRepository {
    private val db = Firebase.firestore
    private var lastDocument: DocumentSnapshot? = null

    suspend fun getAllCategorias(): List<CategoryModel> {
        val result = db.collection("categorias")
            .whereEqualTo("estado", "ACTIVO")
            .get()
            .await()
        return result.documents.mapNotNull {
            document ->
            val categoria = document.toObject(CategoryModel::class.java)
            categoria?.copy(id= document.id)
        }
    }

    suspend fun getCategorias(
        limit: Int,
        startAfter: DocumentSnapshot? = null
    ): Pair<List<CategoryModel>, DocumentSnapshot?> {

        var query = db.collection("categorias")
            .whereEqualTo("estado", "ACTIVO")
            .limit(limit.toLong())

        startAfter?.let {
            query = query.startAfter(it)
        }

        val result = query.get().await()

        val items = result.documents.mapNotNull { document ->
            document.toObject(CategoryModel::class.java)?.copy(id = document.id)
        }

        val nextCursor = if (result.isEmpty) null else result.documents.last()

        return Pair(items, nextCursor)  // ✅ Retorna datos + cursor juntos
    }

    fun resetPagination(){
        lastDocument = null
    }

}