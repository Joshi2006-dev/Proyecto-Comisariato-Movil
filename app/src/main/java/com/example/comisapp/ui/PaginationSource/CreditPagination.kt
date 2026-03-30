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
) : PagingSource<Int, CreditModel>() {

    override fun getRefreshKey(state: PagingState<Int, CreditModel>): Int? {
        return state.anchorPosition?.let{
            state.closestPageToPosition(it)?.prevKey?.plus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CreditModel> {
        val currentPage = params.key ?: 1
        return try{
            val response = repo.getCreditByUserId(userId ,params.loadSize)

            LoadResult.Page(
                data = response,
                prevKey = null,
                nextKey = if (response.isEmpty()) null else 1
            )

        }catch (e: Exception){
            LoadResult.Error(e)
        }
    }
}