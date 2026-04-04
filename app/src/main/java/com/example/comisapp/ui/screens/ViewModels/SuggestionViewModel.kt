package com.example.comisapp.ui.screens.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.comisapp.data.models.SuggestionModel
import com.example.comisapp.data.repository.SuggestionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.comisapp.data.models.UserModel
import com.example.comisapp.data.repository.UserRepository
import com.example.comisapp.ui.PaginationSource.SuggestionPaginationSource
import kotlinx.coroutines.flow.Flow

class SuggestionViewModel(
    private val suggestionRepo: SuggestionRepository,
    private val userRepo: UserRepository
) : ViewModel() {

    private val _sugerencias = MutableStateFlow<List<SuggestionModel>>(emptyList())
    val sugerencias: StateFlow<List<SuggestionModel>> = _sugerencias

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _enviado = MutableStateFlow(false)
    val enviado: StateFlow<Boolean> = _enviado

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    val suggestionsPaging: Flow<PagingData<SuggestionModel>> =
        Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                SuggestionPaginationSource(suggestionRepo)
            }
        ).flow.cachedIn(viewModelScope)


    var currentUser by mutableStateOf<UserModel?>(null)
        private set

    private fun loadCurrentUser() {
        viewModelScope.launch {
            currentUser = userRepo.getCurrentUser()
        }
    }


    companion object {
        fun Factory(
            repo: SuggestionRepository,
            repoUser: UserRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SuggestionViewModel(repo, repoUser) as T
            }
        }
    }

    private fun cargarSugerencias() {
        viewModelScope.launch {
            try {
                _sugerencias.value = suggestionRepo.getAllSuggestions()
            } catch (e: Exception) {

            }
        }
    }

    fun enviarSugerencia(usuarioId: String, asunto: String, descripcion: String) {
        if (asunto.isBlank() || descripcion.isBlank()) {
            _errorMessage.value = "Por favor completa todos los campos"
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            try {
                suggestionRepo.addSugerencia(usuarioId, asunto, descripcion, currentUser ?: UserModel())
                _enviado.value = true
                cargarSugerencias()
            } catch (e: Exception) {
                _errorMessage.value = "Error al enviar sugerencia: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetEnviado() {
        _enviado.value = false
    }

    fun resetError() {
        _errorMessage.value = null
    }

    init {
        cargarSugerencias()
        loadCurrentUser()
    }
}