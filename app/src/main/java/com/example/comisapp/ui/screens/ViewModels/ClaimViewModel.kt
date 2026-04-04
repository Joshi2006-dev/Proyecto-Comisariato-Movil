package com.example.comisapp.ui.screens.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.comisapp.data.models.ClaimModel
import com.example.comisapp.data.repository.ClaimRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ClaimViewModel(private val repo: ClaimRepository) : ViewModel() {

    private val _reclamos = MutableStateFlow<List<ClaimModel>>(emptyList())
    val reclamos: StateFlow<List<ClaimModel>> = _reclamos

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun cargarReclamos(email: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repo.obtenerMisReclamos(email).collect { lista ->
                _reclamos.value = lista
                _isLoading.value = false
            }
        }
    }

    class Factory(private val repo: ClaimRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ClaimViewModel(repo) as T
        }
    }
}