package com.example.comisapp.ui.PaginationSource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.comisapp.data.models.ProductModel
import com.example.comisapp.data.repository.ProductRepository
import com.google.firebase.firestore.DocumentSnapshot

class SavePaginationSource(
    private val repo: ProductRepository,
    private val userId: String,
): PagingSource<DocumentSnapshot, ProductModel>() {

    override fun getRefreshKey(state: PagingState<DocumentSnapshot, ProductModel>): DocumentSnapshot? {
        return null
    }

    override suspend fun load(params: LoadParams<DocumentSnapshot>): LoadResult<DocumentSnapshot, ProductModel> {
        return try {
            val pageResult = repo.getFavoriteProducts(
                limit = params.loadSize,
                lastDocument = params.key,
                userId = userId
            )

            LoadResult.Page(
                data = pageResult.data,
                prevKey = null,
                nextKey = if (pageResult.data.size < params.loadSize) null else pageResult.lastDocument
            )
        } catch (e: Exception) {
            Log.e("Paging", "Error al cargar: ${e.message}", e)
            LoadResult.Error(e)
        }
    }
}