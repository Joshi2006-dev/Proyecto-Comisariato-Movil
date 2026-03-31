package com.example.comisapp.ui.PaginationSource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.comisapp.data.models.CategoryModel
import com.example.comisapp.data.models.ProductModel
import com.example.comisapp.data.models.credits.CreditModel
import com.example.comisapp.data.repository.CreditRepository
import com.google.firebase.firestore.DocumentSnapshot
class CreditPagination(
    private val repo: CreditRepository,
    private val userId: String,
) : PagingSource<DocumentSnapshot, CreditModel>() {

    override fun getRefreshKey(state: PagingState<DocumentSnapshot, CreditModel>): DocumentSnapshot? {
        return null
    }

    override suspend fun load(
        params: LoadParams<DocumentSnapshot>
    ): LoadResult<DocumentSnapshot, CreditModel> {
        return try {
            val lastDocument = params.key
            val response = repo.getCreditByUserId(
                userId = userId,
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