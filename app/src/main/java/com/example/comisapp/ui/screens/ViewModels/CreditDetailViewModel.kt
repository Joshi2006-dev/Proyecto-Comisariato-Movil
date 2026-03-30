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
import com.example.comisapp.data.models.ProductModel
import com.example.comisapp.data.models.credits.CreditModel
import com.example.comisapp.data.models.credits.QuotaModel
import com.example.comisapp.data.repository.CreditRepository
import com.example.comisapp.data.repository.ProductRepository
import com.example.comisapp.data.repository.UserRepository
import com.example.comisapp.ui.PaginationSource.CreditPagination
import com.example.comisapp.ui.PaginationSource.QuotasPaginationSourcePagination
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch



sealed class CreditDetailState {
    object Loading : CreditDetailState()
    data class Success(val credit: CreditModel) : CreditDetailState()
    data class Error(val message: String) : CreditDetailState()
}
class CreditDetailViewModel(
    private val repoCredit: CreditRepository,
    private val creditId: String
): ViewModel() {
    companion object {
        fun Factory(
            repoCre: CreditRepository,
            id: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CreditDetailViewModel(repoCre,id) as T
            }
        }
    }

    var uiState by mutableStateOf<CreditDetailState>(CreditDetailState.Loading)
        private set

    init {
        loadCredit()
    }


    private fun loadCredit(){
        viewModelScope.launch {
            uiState = CreditDetailState.Loading
            try{
                val credit = repoCredit.getCreditById(creditId)
                uiState = if(credit != null){
                    CreditDetailState.Success(credit)
                }else{
                    CreditDetailState.Error("Producto no encontrado")
                }
            }catch (e: Exception){
                uiState = CreditDetailState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    val quotasFlow: Flow<PagingData<QuotaModel>> = Pager(
        config = PagingConfig(
            pageSize = 4,
            prefetchDistance = 2,
            enablePlaceholders = false,
            initialLoadSize = 4,
        ),
        pagingSourceFactory = {
            QuotasPaginationSourcePagination(repoCredit, creditId)
        }
    ).flow.cachedIn(viewModelScope)



}