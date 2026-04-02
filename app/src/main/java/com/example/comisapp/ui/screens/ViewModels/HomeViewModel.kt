package com.example.comisapp.ui.screens.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.comisapp.data.repository.CategoryRepository
import com.example.comisapp.data.models.CategoryModel
import com.example.comisapp.data.models.ProductModel
import com.example.comisapp.data.models.UserModel
import com.example.comisapp.data.repository.ProductRepository
import com.example.comisapp.data.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import com.example.comisapp.ui.PaginationSource.CategoryPaginationSource
import com.example.comisapp.ui.PaginationSource.ProductPagination
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(
    private val categoryRepo: CategoryRepository,
    private val userRepo: UserRepository,
    private val productosRepo: ProductRepository
): ViewModel() {

    val categoriesFlow: Flow<PagingData<CategoryModel>> = Pager(
        config = PagingConfig(
            pageSize = 6,
            prefetchDistance = 2,
            enablePlaceholders = false,
            initialLoadSize = 6,
        ),
        pagingSourceFactory = {
            CategoryPaginationSource(categoryRepo)
        }
        ).flow
        .cachedIn(viewModelScope)


    var currentUser by mutableStateOf<UserModel?>(null)
        private set

    private fun loadCurrentUser() {
        viewModelScope.launch {
            currentUser = userRepo.getCurrentUser()
        }
    }

    init {
        loadCurrentUser()
    }

    val productsFlow: Flow<PagingData<ProductModel>> = Pager(
        config = PagingConfig(
            pageSize = 8,
            prefetchDistance = 3,
            enablePlaceholders = false,
            initialLoadSize = 8,
        ),
        pagingSourceFactory = {
            ProductPagination(productosRepo, filter = "home")
        }
    ).flow
        .cachedIn(viewModelScope)

    companion object {
        fun Factory(
            repoCat: CategoryRepository,
            repoProd: ProductRepository,
            repoUser: UserRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(repoCat,repoUser,repoProd) as T
            }
        }
    }
}