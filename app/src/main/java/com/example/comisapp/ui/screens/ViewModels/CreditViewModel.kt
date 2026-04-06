package com.example.comisapp.ui.screens.ViewModels

    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.ViewModelProvider
    import androidx.lifecycle.viewModelScope
    import androidx.paging.Pager
    import androidx.paging.PagingConfig
    import androidx.paging.PagingData
    import androidx.paging.cachedIn
    import com.example.comisapp.data.models.credits.CreditModel
    import com.example.comisapp.data.repository.CreditRepository
    import com.example.comisapp.ui.PaginationSource.CreditPagination
    import com.google.firebase.auth.FirebaseAuth
    import kotlinx.coroutines.ExperimentalCoroutinesApi
    import kotlinx.coroutines.flow.Flow
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.combine
    import kotlinx.coroutines.flow.flatMapLatest
    import kotlinx.coroutines.launch

    class CreditViewModel(
        private val repoCredit: CreditRepository,
    ): ViewModel() {

    private val repoCredit: CreditRepository,
): ViewModel() {

    companion object {
        fun Factory(
            repoCre: CreditRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CreditViewModel(repoCre) as T
            }
        }
    }



        private val refreshTrigger = MutableStateFlow(0)


    private val currentUserId: String?
        get() = FirebaseAuth.getInstance().currentUser?.email

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: MutableStateFlow<Int> = _selectedTab

    private fun tabToEstado(tab: Int): String? = when (tab) {
        0 -> null
        1 -> "Aprobado"
        2 -> "Pendiente"
        3 -> "Rechazado"
        4 -> "Liquidado"
        else -> null
    }

        private fun tabToEstado(tab: Int): String? = when (tab){
            0 -> null
            1 -> "Aprobado"
            2 -> "Pendiente"
            3 -> "Rechazado"
            4 -> "Liquidado"
            else -> null
        }

        fun deleteClaim(creditId: String) {
            viewModelScope.launch {
                try {
                    repoCredit.deleteCredit(creditId)
                    refreshTrigger.value++
                } catch (e: Exception) {
                }
            }
        }


        fun onTabSelected(tab: Int) {
            _selectedTab.value = tab
        }

    fun cancelarSolicitud(creditId: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                repoCredit.deleteCredit(creditId)

                onComplete()
            } catch (e: Exception) {
                e.printStackTrace()
        @OptIn(ExperimentalCoroutinesApi::class)
        val creditsFlow: Flow<PagingData<CreditModel>> =
            combine(_selectedTab, refreshTrigger) { tab, _ -> tab }.flatMapLatest {
                tab->
                Pager(
                    config = PagingConfig(
                        pageSize = 6,
                        prefetchDistance = 2,
                        enablePlaceholders = false,
                        initialLoadSize = 6,
                    ),
                    pagingSourceFactory = {
                        CreditPagination(repoCredit,
                            currentUserId ?: "",
                            estado = tabToEstado(tab)
                        )
                    }
                ).flow.cachedIn(viewModelScope)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val creditsFlow: Flow<PagingData<CreditModel>> =
        _selectedTab.flatMapLatest { tab ->
            Pager(
                config = PagingConfig(
                    pageSize = 6,
                    prefetchDistance = 2,
                    enablePlaceholders = false,
                    initialLoadSize = 6,
                ),
                pagingSourceFactory = {
                    CreditPagination(
                        repoCredit,
                        currentUserId ?: "",
                        estado = tabToEstado(tab)
                    )
                }
            ).flow.cachedIn(viewModelScope)
        }
}