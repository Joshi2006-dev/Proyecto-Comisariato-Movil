package com.example.comisapp.data.repository

import android.util.Log
import com.example.comisapp.data.models.CategoryModel
import com.example.comisapp.data.models.ProductModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

// Modelo de resultado paginado
data class PageResult<T>(
    val data: List<T>,
    val lastDocument: DocumentSnapshot?
)

class ProductRepository {
    private val db = Firebase.firestore

    suspend fun getProductos(limit: Int, lastDocument: DocumentSnapshot? = null, CategoryId: String? = null): PageResult<ProductModel> {
        Log.d("Firestore", "Consultando productos, limit: $limit, desde: ${lastDocument?.id}")
        var query = db.collection("productos")
            .whereEqualTo("activo", true)

        if(CategoryId != null) query = query.whereEqualTo("categoriaId", CategoryId)

        query = query.limit(limit.toLong())
        lastDocument?.let { query = query.startAfter(it) }

        val result = query.get().await()

        val productos = result.documents.mapNotNull { doc ->
            doc.toObject(ProductModel::class.java)?.copy(id = doc.id)
        }
        return PageResult(
            data = productos,
            lastDocument = if (!result.isEmpty) result.documents.last() else null
        )
    }
}