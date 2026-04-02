package com.example.comisapp.data.repository

import android.util.Log
import com.example.comisapp.data.models.CategoryModel
import com.example.comisapp.data.models.CommentModel
import com.example.comisapp.data.models.ProductModel
import com.example.comisapp.data.models.FavoriteModel
import com.example.comisapp.data.models.credits.QuotaModel
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

// Modelo de resultado paginado
data class PageResult<T>(
    val data: List<T>,
    val lastDocument: DocumentSnapshot?
)

class ProductRepository {
    private val db = Firebase.firestore

    suspend fun getProductos(
        limit: Int,
        lastDocument: DocumentSnapshot? = null,
        CategoryId: String? = null,
        filter: String? = null
    ): PageResult<ProductModel> {

        var query = db.collection("productos")
            .whereEqualTo("activo", true)

        if (CategoryId != null) query = query.whereEqualTo("categoriaId", CategoryId)

        query = when (filter) {
            "home"     -> query.orderBy("fechaRegistro", Query.Direction.DESCENDING)
            "catalogo" -> query
                .orderBy("rate", Query.Direction.DESCENDING)
                .orderBy("fechaRegistro", Query.Direction.DESCENDING)
            "favs"     -> query.orderBy("fechaRegistro", Query.Direction.DESCENDING)
            else       -> query.orderBy("fechaRegistro", Query.Direction.DESCENDING)
        }

        query = query.limit(limit.toLong())

        lastDocument?.let {
            when (filter) {
                "catalogo" -> {
                    val rate = it.getDouble("rate")
                    val fechaRegistro = it.getTimestamp("fechaRegistro")
                    query = query.startAfter(rate, fechaRegistro)
                }
                else -> {
                    val fechaRegistro = it.getTimestamp("fechaRegistro")
                    query = query.startAfter(fechaRegistro)
                }
            }
        }

        val result = query.get().await()

        val productos = result.documents.mapNotNull { doc ->
            doc.toObject(ProductModel::class.java)?.copy(id = doc.id)
        }

        return PageResult(
            data = productos,
            lastDocument = if (!result.isEmpty) result.documents.last() else null
        )
    }




    suspend fun getProductoById(id: String): ProductModel? {
        return try {
            val doc = db.collection("productos").document(id).get().await()

            if(doc.exists()){
                doc.toObject(ProductModel::class.java)?.copy(id = doc.id)
            }else{
                null
            }
        }catch (e: Exception) {
            null
        }
    }

    suspend fun isFavorite(productId:String, userId: String): Boolean{
        return try {
            val doc = db.collection("productos")
                .document(productId)
                .collection("favoritos")
                .document( userId)
                .get().await()

            doc.exists() && doc.getBoolean("marcado") == true
        }catch (e: Exception){
            false
        }
    }

    suspend fun addFavorite(productId:String, userId: String){
        db.collection("productos")
            .document(productId)
            .collection("favoritos")
            .document(userId)
            .set(
                FavoriteModel(
                    uid = userId,
                    marcado = true,
                    fechaGuardado = Timestamp.now()
                )
            )
            .await()
    }

    suspend fun removeFavorite(productId:String, userId: String){
        db.collection("productos")
            .document(productId)
            .collection("favoritos")
            .document(userId)
            .delete().await()
    }



    suspend fun getCommentsByProductId(productId: String, limit: Int, lastDocument: DocumentSnapshot? = null):
            PageResult<CommentModel>
    {
        var query = db.collection("productos")
            .document(productId)
            .collection("comentarios")
            .limit(limit.toLong())

        lastDocument?.let { query = query.startAfter(it) }

        val result = query.get().await()

        val comentarios = result.documents.mapNotNull { doc ->
            doc.toObject(CommentModel::class.java)?.copy(id = doc.id)
        }
        return PageResult(
            data = comentarios,
            lastDocument = if (!result.isEmpty) result.documents.last() else null
        )

    }

}