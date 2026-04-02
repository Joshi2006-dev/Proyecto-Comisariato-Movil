package com.example.comisapp.ui.screens.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.comisapp.data.models.ProductModel
import com.example.comisapp.data.repository.ProductRepository
import com.example.comisapp.data.repository.UserRepository
import com.example.comisapp.ui.PaginationSource.SavePaginationSource
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow

class SaveViewModel(
    private val productosRepo: ProductRepository
): ViewModel() {
    companion object {
        fun Factory(
            repoProd: ProductRepository,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SaveViewModel(repoProd) as T
            }
        }
    }

    private val currentUserId:String?
        get() = FirebaseAuth.getInstance().currentUser?.email



    val productsFlow: Flow<PagingData<ProductModel>> = Pager(
        config = PagingConfig(
            pageSize = 6,
            prefetchDistance = 4,
            enablePlaceholders = false,
            initialLoadSize = 6,
        ),
        pagingSourceFactory = {
            SavePaginationSource(productosRepo, currentUserId?:"")
        }
    ).flow
        .cachedIn(viewModelScope)
}