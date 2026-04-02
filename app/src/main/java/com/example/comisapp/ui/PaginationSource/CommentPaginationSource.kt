package com.example.comisapp.ui.PaginationSource

import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadParams
import androidx.paging.PagingSource.LoadResult
import androidx.paging.PagingState
import com.example.comisapp.data.models.CommentModel
import com.example.comisapp.data.models.credits.QuotaModel
import com.example.comisapp.data.repository.CreditRepository
import com.example.comisapp.data.repository.ProductRepository
import com.google.firebase.firestore.DocumentSnapshot



class CommentPaginationSource(
    private val repo: ProductRepository,
    private val productId: String,
) : PagingSource<DocumentSnapshot, CommentModel>() {

    override fun getRefreshKey(state: PagingState<DocumentSnapshot, CommentModel>): DocumentSnapshot? {
        return null
    }

    override suspend fun load(
        params: LoadParams<DocumentSnapshot>
    ): LoadResult<DocumentSnapshot, CommentModel> {
        return try {
            val lastDocument = params.key
            val response = repo.getCommentsByProductId(
                productId = productId,
                limit = params.loadSize,
                lastDocument = lastDocument
            )

            LoadResult.Page(
                data = response.data,
                prevKey = null,
                nextKey = response.lastDocument
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}