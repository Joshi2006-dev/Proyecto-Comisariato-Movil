package com.example.comisapp.ui.PaginationSource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.comisapp.data.models.ProductModel
import com.example.comisapp.data.repository.ProductRepository
import com.google.firebase.firestore.DocumentSnapshot

class ProductPagination(
    private val repo: ProductRepository,
    private val categoriaId: String? = null,
    private val filter: String? = null
) : PagingSource<DocumentSnapshot, ProductModel>() {

    override fun getRefreshKey(state: PagingState<DocumentSnapshot, ProductModel>): DocumentSnapshot? {
        return null
    }

    override suspend fun load(params: LoadParams<DocumentSnapshot>): LoadResult<DocumentSnapshot, ProductModel> {
        return try {
            Log.d("Paging", "Cargando página, key: ${params.key}, loadSize: ${params.loadSize}")

            val pageResult = repo.getProductos(
                limit = params.loadSize,
                lastDocument = params.key,
                CategoryId = categoriaId,
                filter = filter
            )

            LoadResult.Page(
                data = pageResult.data,
                prevKey = null,
                nextKey = pageResult.lastDocument
            )
        } catch (e: Exception) {
            Log.e("Paging", "Error al cargar: ${e.message}", e)
            LoadResult.Error(e)
        }
    }
}