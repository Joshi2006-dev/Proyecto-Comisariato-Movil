package com.example.comisapp.ui.PaginationSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.comisapp.data.models.CategoryModel
import com.example.comisapp.data.repository.CategoryRepository
import com.google.firebase.firestore.DocumentSnapshot

class CategoryPaginationSource(
    private val repo: CategoryRepository,
): PagingSource<DocumentSnapshot, CategoryModel>() {

    override fun getRefreshKey(state: PagingState<DocumentSnapshot, CategoryModel>): DocumentSnapshot? {
        return null
    }

    override suspend fun load(params: LoadParams<DocumentSnapshot>): LoadResult<DocumentSnapshot, CategoryModel> {
        return try {
            val lastDocument = params.key

            val (items, nextCursor) = repo.getCategorias(
                limit = params.loadSize,
                startAfter = lastDocument
            )

            LoadResult.Page(
                data = items,
                prevKey = null,
                nextKey = nextCursor
            )

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}