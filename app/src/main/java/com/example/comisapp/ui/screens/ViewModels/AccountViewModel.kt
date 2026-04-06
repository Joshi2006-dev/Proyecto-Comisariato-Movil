package com.example.comisapp.ui.screens.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.comisapp.data.models.EmployeeModel
import com.example.comisapp.data.repository.EmployeeRepository
import com.example.comisapp.data.repository.UserRepository
import kotlinx.coroutines.launch


sealed class EmployeeDetailState {
    object Loading : EmployeeDetailState()
    data class Success(val employee: EmployeeModel) : EmployeeDetailState()
    data class Error(val message: String) : EmployeeDetailState()
}
class AccountViewModel(
    private val repoEmpleado: EmployeeRepository,
    private val repoUser: UserRepository
): ViewModel() {
    companion object {
        fun Factory(
            empRepo: EmployeeRepository,
            userRepo: UserRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AccountViewModel(empRepo, userRepo) as T
            }
        }
    }

    var uiState by mutableStateOf<EmployeeDetailState>(EmployeeDetailState.Loading)
        private set

    var limiteUsado by mutableStateOf<Double?>(null)
        private set

    fun loadLimiteUsado(usuarioId: String) {
        viewModelScope.launch {
            limiteUsado = repoEmpleado.calcularLimiteUsado(usuarioId)
        }
    }

    fun loadEmployee() {
        uiState = EmployeeDetailState.Loading
        viewModelScope.launch {
            val empleadoId = repoUser.getCurrentUser()?.empleadoId
            val usuarioId = repoUser.getCurrentUser()?.id
            if (empleadoId != null) {
                try {
                    val employee = repoEmpleado.getEmployeeById(empleadoId)
                    if (employee != null) {
                        uiState = EmployeeDetailState.Success(employee)
                        if (usuarioId != null){
                            loadLimiteUsado(usuarioId)
                        }else{
                            uiState = EmployeeDetailState.Error("Usuario no encontrado")
                        }
                    }else{
                        uiState = EmployeeDetailState.Error("Empleado no encontrado")
                    }

                }catch (e: Exception){
                    uiState = EmployeeDetailState.Error(e.message ?: "Error desconocido")
                }
            }

        }
    }

    init{
        loadEmployee()
    }

}