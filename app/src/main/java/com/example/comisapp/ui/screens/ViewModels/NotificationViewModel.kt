package com.example.comisapp.ui.screens.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.comisapp.data.models.NotificationModel
import com.example.comisapp.data.repository.NotificationRepository
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val repository: NotificationRepository
) : ViewModel() {

    val noLeidasCount: StateFlow<Int> =
        repository.getNoLeidasCount()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _notificaciones =
        MutableStateFlow<List<NotificationModel>>(emptyList())

    val notificaciones: StateFlow<List<NotificationModel>> =
        _notificaciones.asStateFlow()

    private val _isLoading =
        MutableStateFlow(false)

    val isLoading: StateFlow<Boolean> =
        _isLoading.asStateFlow()

    private val _hayMas =
        MutableStateFlow(false)

    val hayMas: StateFlow<Boolean> =
        _hayMas.asStateFlow()

    private var estaCargando = false
    private var lastVisible: DocumentSnapshot? = null

    init {
        cargarPrimeraPagina()
    }

    fun cargarPrimeraPagina() {

        if (estaCargando) return

        estaCargando = true

        viewModelScope.launch {

            try {

                _isLoading.value = true

                lastVisible = null

                val (lista, ultimo) =
                    repository.getNotificacionesPaginadas(null)

                _notificaciones.value = lista

                lastVisible = ultimo


                _hayMas.value = ultimo != null

            } catch (e: Exception) {

                e.printStackTrace()

                _hayMas.value = false

            } finally {

                _isLoading.value = false

                estaCargando = false
            }
        }
    }

    fun cargarMas() {

        if (estaCargando) return

        if (!_hayMas.value) return

        if (lastVisible == null) return

        estaCargando = true
        _isLoading.value = true // Lo pasamos a true ANTES del launch para que la UI responda ya

        viewModelScope.launch {

            try {
                val (lista, ultimo) =
                    repository.getNotificacionesPaginadas(
                        lastVisible
                    )

                if (lista.isNotEmpty()) {

                    val actuales = _notificaciones.value

                    val nuevos =
                        lista.filter { nueva ->
                            actuales.none {
                                it.notificacionId == nueva.notificacionId
                            }
                        }

                    if (nuevos.isNotEmpty()) {
                        _notificaciones.value =
                            actuales + nuevos
                    }

                    lastVisible = ultimo
                    _hayMas.value = ultimo != null
                } else {
                    _hayMas.value = false
                }

            } catch (e: Exception) {

                e.printStackTrace()

                _hayMas.value = false

            } finally {

                _isLoading.value = false

                estaCargando = false
            }
        }
    }

    fun marcarComoLeida(id: String) {
        viewModelScope.launch {
            try {

                repository.marcarComoLeida(id)


                _notificaciones.value = _notificaciones.value.map {
                    if (it.notificacionId == id) it.copy(leido = true) else it
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun marcarTodasComoLeidas() {
        viewModelScope.launch {
            try {

                repository.marcarTodasComoLeidas()



                _notificaciones.value = _notificaciones.value.map {
                    it.copy(leido = true)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    class Factory(
        private val repository: NotificationRepository
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(
            modelClass: Class<T>
        ): T {

            @Suppress("UNCHECKED_CAST")

            return NotificationViewModel(
                repository
            ) as T
        }
    }
}