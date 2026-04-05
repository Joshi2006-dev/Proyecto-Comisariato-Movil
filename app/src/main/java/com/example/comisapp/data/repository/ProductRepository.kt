package com.example.comisapp.data.repository

import android.util.Log
import com.example.comisapp.data.models.CommentModel
import com.example.comisapp.data.models.ProductModel
import com.example.comisapp.data.models.FavoriteModel
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

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
        filter: String? = null,
        searchQuery: String? = null
    ): PageResult<ProductModel> {

        // --- MODO BÚSQUEDA ---
        if (!searchQuery.isNullOrBlank()) {
            val q = searchQuery.trim().lowercase()

            var searchBase = db.collection("productos")
                .whereEqualTo("activo", true)
                .orderBy("nombreNormalizado")
                .startAt(q)
                .endAt(q + "\uf8ff")
                .limit((limit * 3).toLong()) // pedir más por si hay filtro de categoría

            val result = searchBase.get().await()

            val productos = result.documents
                .mapNotNull { doc ->
                    doc.toObject(ProductModel::class.java)?.copy(id = doc.id)
                }
                .filter { CategoryId == null || it.categoriaId == CategoryId }
                .take(limit) // recortar al límite real

            return PageResult(
                data = productos,
                lastDocument = null // sin paginación en modo búsqueda por ahora
            )
        }

        // --- MODO NORMAL (sin búsqueda) ---
        var query = db.collection("productos")
            .whereEqualTo("activo", true)

        if (CategoryId != null) query = query.whereEqualTo("categoriaId", CategoryId)

        query = when (filter) {
            "home" -> query.orderBy("fechaRegistro", Query.Direction.DESCENDING)
            "catalogo" -> query
                .orderBy("rate", Query.Direction.DESCENDING)
                .orderBy("fechaRegistro", Query.Direction.DESCENDING)
            else -> query.orderBy("fechaRegistro", Query.Direction.DESCENDING)
        }

        query = query.limit(limit.toLong())

        lastDocument?.let {
            query = when (filter) {
                "catalogo" -> {
                    val rate = it.getDouble("rate")
                    val fechaRegistro = it.getTimestamp("fechaRegistro")
                    query.startAfter(rate, fechaRegistro)
                }
                else -> {
                    val fechaRegistro = it.getTimestamp("fechaRegistro")
                    query.startAfter(fechaRegistro)
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

    suspend fun getSuggestions(query: String, categoriaId: String? = null): List<String> {
        return try {
            val q = query.trim().lowercase()

            var firestoreQuery = db.collection("productos")
                .whereEqualTo("activo", true)
                .orderBy("nombreNormalizado")
                .startAt(q)
                .endAt(q + "\uf8ff")

            if (categoriaId != null) {
                firestoreQuery = firestoreQuery.whereEqualTo("categoriaId", categoriaId)
            }

            firestoreQuery
                .limit(5)
                .get()
                .await()
                .documents
                .mapNotNull { doc -> doc.getString("nombre") }

        } catch (e: Exception) {
            Log.e("Suggestions", "Error: ${e.message}")
            emptyList()
        }
    }
    suspend fun getProductoById(id: String): ProductModel? {
        return try {
            val doc = db.collection("productos").document(id).get().await()
            if (doc.exists()) {
                doc.toObject(ProductModel::class.java)?.copy(id = doc.id)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun isFavorite(productId: String, userId: String): Boolean {
        return try {
            val doc = db.collection("productos")
                .document(productId)
                .collection("favoritos")
                .document(userId)
                .get().await()
            doc.exists() && doc.getBoolean("marcado") == true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun addFavorite(productId: String, userId: String, product: ProductModel?) {
        val batch = db.batch()

        val productoFavRef = db.collection("productos")
            .document(productId)
            .collection("favoritos")
            .document(userId)

        batch.set(productoFavRef, FavoriteModel(
            uid = userId,
            marcado = true,
            fechaGuardado = Timestamp.now()
        ))

        val usuarioFavRef = db.collection("usuarios")
            .document(userId)
            .collection("favoritos")
            .document(productId)

        batch.set(usuarioFavRef, mapOf(
            "nombre"           to product?.nombre,
            "imagenFrontalUrl" to product?.imagenFrontalUrl,
            "categoria"        to product?.categoria,
            "fechaGuardado"    to Timestamp.now()
        ))

        batch.commit().await()
    }

    suspend fun removeFavorite(productId: String, userId: String) {
        val batch = db.batch()

        val productoFavRef = db.collection("productos")
            .document(productId)
            .collection("favoritos")
            .document(userId)

        val usuarioFavRef = db.collection("usuarios")
            .document(userId)
            .collection("favoritos")
            .document(productId)

        batch.delete(productoFavRef)
        batch.delete(usuarioFavRef)

        batch.commit().await()
    }

    suspend fun getFavoriteProducts(
        userId: String,
        limit: Int,
        lastDocument: DocumentSnapshot? = null
    ): PageResult<ProductModel> {
        return try {
            var query = db.collection("usuarios")
                .document(userId)
                .collection("favoritos")
                .orderBy("fechaGuardado", Query.Direction.DESCENDING)
                .limit(limit.toLong())

            lastDocument?.let { query = query.startAfter(it) }

            val result = query.get().await()

            val productos = result.documents.mapNotNull { doc ->
                ProductModel(
                    id               = doc.id,  // el ID del doc es el ID del producto
                    nombre           = doc.getString("nombre") ?: "",
                    imagenFrontalUrl = doc.getString("imagenFrontalUrl") ?: "",
                    categoria        = doc.getString("categoria") ?: "",
                )
            }

            PageResult(
                data = productos,
                lastDocument = if (!result.isEmpty) result.documents.last() else null
            )
        } catch (e: Exception) {
            Log.e("FavRepository", "Error: ${e.message}")
            PageResult(emptyList(), null)
        }
    }

    suspend fun getCommentsByProductId(
        productId: String,
        limit: Int,
        lastDocument: DocumentSnapshot? = null
    ): PageResult<CommentModel> {
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