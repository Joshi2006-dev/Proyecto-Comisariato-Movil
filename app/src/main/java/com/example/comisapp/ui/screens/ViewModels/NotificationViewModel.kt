package com.example.comisapp.ui.screens.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.comisapp.data.models.NotificationModel
import com.example.comisapp.data.repository.NotificationRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val repository: NotificationRepository
) : ViewModel() {

    val notificaciones: StateFlow<List<NotificationModel>> =
        repository.getNotificacionesCombinadas()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val noLeidasCount: StateFlow<Int> =
        repository.getNoLeidasCount()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun marcarComoLeida(id: String) {
        viewModelScope.launch { repository.marcarComoLeida(id) }
    }

    fun marcarTodasComoLeidas() {
        viewModelScope.launch { repository.marcarTodasComoLeidas() }
    }

    class Factory(private val repository: NotificationRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return NotificationViewModel(repository) as T
        }
    }
}