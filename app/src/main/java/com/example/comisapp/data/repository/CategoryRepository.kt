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
            .whereEqualTo("estado", "Activo")
            .get()
            .await()
        return result.documents.mapNotNull {
            document ->
            val categoria = document.toObject(CategoryModel::class.java)
            categoria?.copy(id= document.id)
        }
    }

    suspend fun getCategorias(limit: Int): List<CategoryModel>{

        var query = db.collection("categorias")
            .whereEqualTo("estado", "Activo")
            .limit(limit.toLong())

        lastDocument?.let {
            query = query.startAfter(it)
        }

        val result = query.get().await()

        if(!result.isEmpty){
            lastDocument = result.documents.last()
        }

        return result.documents.mapNotNull {
            document ->
            val categoria = document.toObject(CategoryModel::class.java)
            categoria?.copy(id = document.id)
        }

    }

    fun resetPagination(){
        lastDocument = null
    }

}