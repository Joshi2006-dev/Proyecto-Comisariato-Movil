
package com.example.comisapp.ui.screens.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.comisapp.data.models.AyudaModel
import com.example.comisapp.data.repository.AyudaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HelpCenterViewModel(
    private val ayudaRepo: AyudaRepository
) : ViewModel() {

    private val _ayudas = MutableStateFlow<List<AyudaModel>>(emptyList())
    val ayudas: StateFlow<List<AyudaModel>> = _ayudas

    companion object {
        fun Factory(repo: AyudaRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HelpCenterViewModel(repo) as T
                }
            }
    }

    private fun cargarAyudas() {
        viewModelScope.launch {
            try {
                _ayudas.value = ayudaRepo.getAllAyudas()
            } catch (e: Exception) {

            }
        }
    }

    init {
        cargarAyudas()
    }
}