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
import com.example.comisapp.ui.PaginationSource.ProductPagination
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch


class CatalogViewModel(
    private val categoryRepo: CategoryRepository,
    private val productosRepo: ProductRepository
) : ViewModel() {

    private val _categorias = MutableStateFlow<List<CategoryModel>>(emptyList())
    val categorias: StateFlow<List<CategoryModel>> = _categorias

    private val _categoriaSeleccionada = MutableStateFlow<String?>(null)
    val categoriaSeleccionada: StateFlow<String?> = _categoriaSeleccionada

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab

    private val _suggestions = MutableStateFlow<List<String>>(emptyList())
    val suggestions: StateFlow<List<String>> = _suggestions

    private val _searchQuery = MutableSharedFlow<String?>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    private var searchJob: Job? = null
    fun onSearchQueryChanged(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (query.isBlank()) {
                _suggestions.value = emptyList()
            } else {
                delay(300)
                _suggestions.value = productosRepo.getSuggestions(
                    query = query,
                    categoriaId = _categoriaSeleccionada.value
                )
            }
        }
    }

    fun applySearch(query: String) {
        _suggestions.value = emptyList()
        viewModelScope.launch {
            _searchQuery.emit(query.ifBlank { null })
        }
    }

    fun clearSearch() {
        _suggestions.value = emptyList()
        viewModelScope.launch {
            _searchQuery.emit(null)
        }
    }

    val productsFlow: Flow<PagingData<ProductModel>> =
        combine(_categoriaSeleccionada, _searchQuery) { categoriaId, searchQuery ->
            Pair(categoriaId, searchQuery)
        }.flatMapLatest { (categoriaId, searchQuery) ->
            Pager(
                config = PagingConfig(
                    pageSize = 6,
                    prefetchDistance = 4,
                    enablePlaceholders = false,
                    initialLoadSize = 6,
                ),
                pagingSourceFactory = {
                    ProductPagination(
                        repo = productosRepo,
                        categoriaId = categoriaId,
                        filter = "catalogo",
                        searchQuery = searchQuery
                    )
                }
            ).flow
        }.cachedIn(viewModelScope)

    fun inicializarCategoria(categoriaId: String?) {
        _categoriaSeleccionada.value = categoriaId
    }

    fun seleccionarCategoria(categoriaId: String?) {
        _categoriaSeleccionada.value = categoriaId
    }

    fun setSelectedTab(index: Int) {
        _selectedTab.value = index
    }

    private fun cargarCategorias() {
        viewModelScope.launch {
            try {
                _categorias.value = categoryRepo.getAllCategorias()
            } catch (e: Exception) { }
        }
    }

    init {
        cargarCategorias()
        viewModelScope.launch {
            _searchQuery.emit(null)
        }
    }

    companion object {
        fun Factory(
            repoCat: CategoryRepository,
            repoProd: ProductRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CatalogViewModel(repoCat, repoProd) as T
            }
        }
    }
}