package com.example.comisapp.ui.screens.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.comisapp.data.models.CategoryModel
import com.example.comisapp.data.models.ProductModel
import com.example.comisapp.data.repository.CategoryRepository
import com.example.comisapp.data.repository.ProductRepository
import com.example.comisapp.ui.PaginationSource.CategoryPaginationSource
import com.example.comisapp.ui.PaginationSource.ProductPagination
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class CatalogViewModel(
    private val categoryRepo: CategoryRepository,
    private val productosRepo: ProductRepository
): ViewModel() {

    private val _categorias = MutableStateFlow<List<CategoryModel>>(emptyList())
    val categorias: StateFlow<List<CategoryModel>> =  _categorias


    private val _categoriaSeleccionada = MutableStateFlow<String?>(null)
    val categoriaSeleccionada: StateFlow<String?> = _categoriaSeleccionada


    val productsFlow: Flow<PagingData<ProductModel>> =
        categoriaSeleccionada.flatMapLatest { categoriaId ->
            Pager(
                config = PagingConfig(
                    pageSize = 10,
                    prefetchDistance = 3,
                    enablePlaceholders = false,
                    initialLoadSize = 10,
                ),
                pagingSourceFactory = {
                    ProductPagination(productosRepo, categoriaId)
                }
            ).flow
                .cachedIn(viewModelScope)
        }




    companion object {
        fun Factory(
            repoCat: CategoryRepository,
            repoProd: ProductRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CatalogViewModel(repoCat,repoProd) as T
            }
        }
    }

    private fun cargarCategorias() {
        viewModelScope.launch {
            try {
                _categorias.value = categoryRepo.getAllCategorias()
            }catch (e: Exception){

            }
        }
    }

    fun seleccionarCategoria(categoriaId: String?){
        _categoriaSeleccionada.value = categoriaId
    }

    init {
        cargarCategorias()
    }

}