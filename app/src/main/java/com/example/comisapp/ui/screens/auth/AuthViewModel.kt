package com.example.comisapp.ui.screens.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.comisapp.data.repository.AuthRepository
import kotlinx.coroutines.launch
import okhttp3.Call

class AuthViewModel(private val repo: AuthRepository) : ViewModel() {
    var isLoading by mutableStateOf(false)
    var isSuccess by mutableStateOf(false)
    var statusMsg by mutableStateOf("")

    fun changePassword(currentPass: String, newPass: String) {
        viewModelScope.launch {
            isLoading = true
            val result = repo.updatePassword(currentPass, newPass)

            if (result.isSuccess) {
                statusMsg = "Contraseña actualizada exitosamente"
                isSuccess = true
            }else{
                statusMsg = "Error al actualizar la contraseña"
                isSuccess = false
            }
            isLoading = false
        }
    }

    companion object{
        fun Factory(repo: AuthRepository): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(repo) as T
            }
        }
    }
}