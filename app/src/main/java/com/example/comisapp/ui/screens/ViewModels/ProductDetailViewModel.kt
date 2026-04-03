package com.example.comisapp.ui.screens.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.comisapp.data.models.ProductModel
import com.example.comisapp.data.models.credits.CreditModel
import com.example.comisapp.data.models.settings.GuaranteePoliticModel
import com.example.comisapp.data.models.settings.PlazoModel
import com.example.comisapp.data.repository.CreditRepository
import com.example.comisapp.data.repository.ProductRepository
import com.example.comisapp.data.repository.SettingRepository
import com.example.comisapp.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import com.example.comisapp.data.models.UserModel


sealed class ProductDetailState {
    object Loading : ProductDetailState()
    data class Success(val product: ProductModel) : ProductDetailState()
    data class Error(val message: String) : ProductDetailState()
}

sealed class CreditRequestState{
    object Idle: CreditRequestState()
    object Loading: CreditRequestState()
    object Success: CreditRequestState()
    data class Error(val message: String): CreditRequestState()
}

class ProductDetailViewModel (
    private val repo: ProductRepository,
    private val creditRepo: CreditRepository,
    private val userRepo: UserRepository,
    private val settingRepo: SettingRepository,
    private val productId: String
): ViewModel() {

    var uiState by mutableStateOf<ProductDetailState>(ProductDetailState.Loading)
        private set

    var isFavorite by mutableStateOf(false)
        private set

    var isFavoriteLoading by mutableStateOf(false)
        private set

    var creditRequestState by mutableStateOf<CreditRequestState>(CreditRequestState.Idle)
        private set


    var plazos by mutableStateOf<List<PlazoModel>>(emptyList())
        private set


    var garantia by mutableStateOf<GuaranteePoliticModel?>(null)
        private set

    private val currentUserId:String?
        get() = FirebaseAuth.getInstance().currentUser?.email

    var currentUser by mutableStateOf<UserModel?>(null)
        private set

    init {
        loadProduct()
        loadSettings()
        loadCurrentUser()
    }

    private fun loadProduct(){
        viewModelScope.launch {
            uiState = ProductDetailState.Loading
            try{
                val product = repo.getProductoById(productId)
                uiState = if(product != null){
                    loadFavoriteStatus()
                    ProductDetailState.Success(product)
                }else{
                    ProductDetailState.Error("Producto no encontrado")
                }
            }catch (e: Exception){
                uiState = ProductDetailState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            currentUser = userRepo.getCurrentUser()
        }
    }

    private fun loadSettings(){
        viewModelScope.launch {
            try {
                plazos = settingRepo.getPlazos()
                garantia = settingRepo.getGuaranteePolitics()
            }catch (e: Exception){

            }
        }
    }

    private fun loadFavoriteStatus(){
        val uid = currentUserId?: return
        viewModelScope.launch {
            isFavorite = repo.isFavorite(productId, uid)
        }
    }

    fun toggleFavorite() {
        val uid = currentUserId ?: return
        if (isFavoriteLoading) return
        val currentlyFavorite = isFavorite
        viewModelScope.launch {
            isFavoriteLoading = true
            isFavorite = !currentlyFavorite
            try {
                val product = repo.getProductoById(productId)
                if(currentlyFavorite){
                    repo.removeFavorite(productId, uid)
                }else{
                    repo.addFavorite(productId, uid, product)
                }
            }catch (e: Exception){

            }finally{
                isFavoriteLoading = false
            }

        }
    }

    fun requestCredit(
        product: ProductModel,
        quantity: Int,
        plazoMeses: Int,
        isSingle:Boolean
    ){
        val uid = currentUserId ?: run{
            creditRequestState = CreditRequestState.Error("No hay usuario logueado")
            return
        }
        viewModelScope.launch {
            creditRequestState = CreditRequestState.Loading
            try {
                val currentUser = userRepo.getCurrentUser()
                val nombreUsuario = currentUser?.nombre?:""
                val empleadoId = currentUser?.empleadoId?:""

                val precio = if(isSingle) product.precioContado?:0.0 else product.precioCredito?:0.0

                val totalCredito = precio * quantity
                val cuotaMensual = totalCredito / plazoMeses

                val credit = CreditModel(
                    cantidad = quantity,
                    cuotaMensual = cuotaMensual,
                    estado = "Pendiente",
                    imagenProductoURL = product.imagenFrontalUrl?:"",
                    nombreProducto = product.nombre?:"",
                    plazoMeses = plazoMeses,
                    productoId = product.id,
                    plazoGarantia = product.garantiaMeses?:0,
                    totalCredito = totalCredito,
                    usuarioId = uid,
                    usuario = nombreUsuario,
                    empleadoId = empleadoId
                )
                creditRepo.addNewCredit(credit)
                creditRequestState = CreditRequestState.Success

            }catch (e: Exception){
                creditRequestState = CreditRequestState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun resetCreditState() {
        creditRequestState = CreditRequestState.Idle
    }

    companion object {
        fun Factory(
            repoProd: ProductRepository,
            repoCredit: CreditRepository,
            repoUser: UserRepository,
            repoSet: SettingRepository,
            id: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProductDetailViewModel(repoProd,repoCredit,repoUser,repoSet,id) as T
            }
        }
    }
}