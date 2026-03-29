package com.example.comisapp.ui.PaginationSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.comisapp.data.models.CategoryModel
import com.example.comisapp.data.repository.CategoryRepository

class CategoryPaginationSource(
    private val repo: CategoryRepository,
): PagingSource<Int, CategoryModel>() {

    override fun getRefreshKey(state: PagingState<Int, CategoryModel>): Int? {
        return state.anchorPosition?.let{
            state.closestPageToPosition(it)?.prevKey?.plus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CategoryModel> {
        val currentPage = params.key ?: 1
        return try{
            val response = repo.getCategorias( params.loadSize)

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