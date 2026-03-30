package com.example.comisapp.ui.PaginationSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.comisapp.data.models.credits.QuotaModel
import com.example.comisapp.data.repository.CreditRepository
import com.google.firebase.firestore.DocumentSnapshot


class QuotasPaginationSourcePagination(
    private val repo: CreditRepository,
    private val creditId: String,
) : PagingSource<DocumentSnapshot, QuotaModel>() {

    override fun getRefreshKey(state: PagingState<DocumentSnapshot, QuotaModel>): DocumentSnapshot? {
        return null
    }

    override suspend fun load(
        params: LoadParams<DocumentSnapshot>
    ): LoadResult<DocumentSnapshot, QuotaModel> {
        return try {
            val lastDocument = params.key
            val response = repo.getQoutasByCreditId(
                creditId = creditId,
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