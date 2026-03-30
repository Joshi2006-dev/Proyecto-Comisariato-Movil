package com.example.comisapp.ui.PaginationSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.comisapp.data.models.SuggestionModel
import com.example.comisapp.data.repository.SuggestionRepository

class SuggestionPaginationSource(
    private val repo: SuggestionRepository,
) : PagingSource<Int, SuggestionModel>() {

    override fun getRefreshKey(state: PagingState<Int, SuggestionModel>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SuggestionModel> {
        val currentPage = params.key ?: 1

        return try {

            val response = repo.getAllSuggestions()

            LoadResult.Page(
                data = response,
                prevKey = null,
                nextKey = if (response.isEmpty()) null else 1
            )

        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}