package com.example.comisapp.ui.screens.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.comisapp.data.models.CategoryModel
import com.example.comisapp.data.models.credits.CreditModel
import com.example.comisapp.data.repository.CategoryRepository
import com.example.comisapp.data.repository.CreditRepository
import com.example.comisapp.data.repository.ProductRepository
import com.example.comisapp.ui.PaginationSource.CategoryPaginationSource
import com.example.comisapp.ui.PaginationSource.CreditPagination
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow

class CreditViewModel(
    repoCredit: CreditRepository,
): ViewModel() {

    companion object {
        fun Factory(
            repoCre:CreditRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CreditViewModel(repoCre) as T
            }
        }
    }
    private val currentUserId:String?
        get() = FirebaseAuth.getInstance().currentUser?.uid


    val creditsFlow: Flow<PagingData<CreditModel>> = Pager(
        config = PagingConfig(
            pageSize = 6,
            prefetchDistance = 2,
            enablePlaceholders = false,
            initialLoadSize = 6,
        ),
        pagingSourceFactory = {
            CreditPagination(repoCredit,currentUserId?:"")
        }
    ).flow
        .cachedIn(viewModelScope)
}