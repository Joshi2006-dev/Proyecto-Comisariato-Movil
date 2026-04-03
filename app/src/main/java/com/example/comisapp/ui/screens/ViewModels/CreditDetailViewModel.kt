package com.example.comisapp.ui.screens.ViewModels

import android.net.Uri
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
import com.example.comisapp.data.models.UserModel
import com.example.comisapp.data.models.credits.ClaimModel
import com.example.comisapp.data.models.credits.CreditModel
import com.example.comisapp.data.models.credits.QuotaModel
import com.example.comisapp.data.models.settings.PlazoModel
import com.example.comisapp.data.repository.CreditRepository
import com.example.comisapp.data.repository.ProductRepository
import com.example.comisapp.data.repository.SettingRepository
import com.example.comisapp.data.repository.UserRepository
import com.example.comisapp.ui.PaginationSource.CreditPagination
import com.example.comisapp.ui.PaginationSource.QuotasPaginationSourcePagination
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch



sealed class EditCreditState {
    object Idle    : EditCreditState()
    object Loading : EditCreditState()
    object Success : EditCreditState()
    data class Error(val message: String) : EditCreditState()
}

sealed class ClaimSubmitState {
    object Idle    : ClaimSubmitState()
    object Loading : ClaimSubmitState()
    object Success : ClaimSubmitState()
    data class Error(val message: String) : ClaimSubmitState()
}



sealed class CreditDetailState {
    object Loading : CreditDetailState()
    data class Success(val credit: CreditModel) : CreditDetailState()
    data class Error(val message: String) : CreditDetailState()
}
class CreditDetailViewModel(
    private val repoCredit: CreditRepository,
    private val userRepo: UserRepository,
    private val settingRepo: SettingRepository,
    private val creditId: String
): ViewModel() {
    companion object {
        fun Factory(
            repoCre: CreditRepository,
            repoSet: SettingRepository,
            repoUser: UserRepository,
            id: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CreditDetailViewModel(repoCre,repoUser,repoSet,id) as T
            }
        }
    }

    private val _claims = MutableStateFlow<List<ClaimModel>>(emptyList())
    val claims: StateFlow<List<ClaimModel>> = _claims

    var editCreditState by mutableStateOf<EditCreditState>(EditCreditState.Idle)
        private set

    var claimSubmitState by mutableStateOf<ClaimSubmitState>(ClaimSubmitState.Idle)
        private set


    var uiState by mutableStateOf<CreditDetailState>(CreditDetailState.Loading)
        private set

    var plazos by mutableStateOf<List<PlazoModel>>(emptyList())
        private set

    var currentUser by mutableStateOf<UserModel?>(null)
        private set

    init {
        loadCredit()
        loadClaims()
        loadSettings()
        loadCurrentUser()
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

    private fun loadSettings(){
        viewModelScope.launch {
            try {
                plazos = settingRepo.getPlazos()
            }catch (e: Exception){

            }
        }
    }

    private fun loadClaims(){
        viewModelScope.launch {
            try{
                _claims.value = repoCredit.getClaimsByCreditId(creditId)
            }catch (e: Exception){

            }
        }
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            currentUser = userRepo.getCurrentUser()
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
    fun editCredit(
        cantidad: Int,
        plazoMeses: Int,
        precioCredito: Double   // precio unitario del producto
    ) {
        val credit = (uiState as? CreditDetailState.Success)?.credit ?: return

        viewModelScope.launch {
            editCreditState = EditCreditState.Loading
            try {
                val totalCredito  = precioCredito * cantidad
                val cuotaMensual  = totalCredito / plazoMeses

                repoCredit.updateCredit(
                    creditId     = creditId,
                    cantidad     = cantidad,
                    plazoMeses   = plazoMeses,
                    cuotaMensual = cuotaMensual,
                    totalCredito = totalCredito
                )

                uiState = CreditDetailState.Success(
                    credit.copy(
                        cantidad     = cantidad,
                        plazoMeses   = plazoMeses,
                        cuotaMensual = cuotaMensual,
                        totalCredito = totalCredito
                    )
                )
                editCreditState = EditCreditState.Success
            } catch (e: Exception) {
                editCreditState = EditCreditState.Error(e.message ?: "Error al editar")
            }
        }
    }

    fun submitClaim(asunto: String, descripcion: String, imageUri: Uri) {
        if (asunto.isBlank() || descripcion.isBlank()) {
            claimSubmitState = ClaimSubmitState.Error("Completa todos los campos")
            return
        }

        viewModelScope.launch {
            claimSubmitState = ClaimSubmitState.Loading
            try {
                val newClaim = repoCredit.addClaim(
                    creditId = creditId,
                    claim    = ClaimModel(
                        asunto      = asunto,
                        descripcion = descripcion
                    ),
                    imageUri = imageUri,
                    solicitante = currentUser?.nombre ?: ""
                )
                _claims.value = _claims.value + newClaim
                claimSubmitState = ClaimSubmitState.Success
            } catch (e: Exception) {
                claimSubmitState = ClaimSubmitState.Error(e.message ?: "Error al enviar")
            }
        }
    }

    fun deleteClaim(claimId: String) {
        viewModelScope.launch {
            try {
                repoCredit.deleteClaim(creditId)
                _claims.value = _claims.value.filter { it.id != claimId }
            } catch (e: Exception) {
                // silencioso por ahora
            }
        }
    }

    fun resetEditState()  { editCreditState  = EditCreditState.Idle }
    fun resetClaimState() { claimSubmitState = ClaimSubmitState.Idle }



}