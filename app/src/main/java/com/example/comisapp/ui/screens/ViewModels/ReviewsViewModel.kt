package com.example.comisapp.ui.screens.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.comisapp.data.models.CommentModel
import com.example.comisapp.data.repository.ProductRepository
import com.example.comisapp.ui.PaginationSource.CommentPaginationSource
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class ReviewsUiState(
    val reviewInput: String = "",
    val selectedRating: Int = 0,
    val isSending: Boolean = false,
    val sendError: String? = null,
    val sendSuccess: Boolean = false,
    val averageRating: Double = 0.0,   // ← calculado desde comentarios
    val totalComments: Int = 0          // ← contador local
)

class ReviewsViewModel(
    private val productId: String,
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewsUiState())
    val uiState: StateFlow<ReviewsUiState> = _uiState.asStateFlow()

    val comments: Flow<PagingData<CommentModel>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = {
            CommentPaginationSource(repo = repository, productId = productId)
        }
    ).flow.cachedIn(viewModelScope)

    fun onReviewInputChange(value: String) {
        _uiState.value = _uiState.value.copy(reviewInput = value, sendError = null)
    }

    fun onRatingChange(rating: Int) {
        _uiState.value = _uiState.value.copy(selectedRating = rating)
    }

    fun sendReview(
        userId: String,
        userName: String,
        userEmail: String,
        userPhotoUrl: String
    ) {
        val state = _uiState.value
        if (state.reviewInput.isBlank() || state.selectedRating == 0) return

        viewModelScope.launch {
            _uiState.value = state.copy(isSending = true, sendError = null)
            try {
                val db = FirebaseFirestore.getInstance()
                val comment = hashMapOf(
                    "usuario" to userName,
                    "comentario" to state.reviewInput.trim(),
                    "email" to userEmail,
                    "fecha" to Timestamp.now(),
                    "rate" to state.selectedRating,
                    "fotoUrl" to userPhotoUrl
                )
                db.collection("productos")
                    .document(productId)
                    .collection("comentarios")
                    .add(comment)
                    .await()

                _uiState.value = _uiState.value.copy(
                    isSending = false,
                    sendSuccess = true,
                    reviewInput = "",
                    selectedRating = 0
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSending = false,
                    sendError = "No se pudo enviar la reseña. Intenta de nuevo."
                )
            }
        }
    }

    fun clearSendSuccess() {
        _uiState.value = _uiState.value.copy(sendSuccess = false)
    }

    fun updateRatingSummary(comments: List<CommentModel>) {
        if (comments.isEmpty()) return
        val avg = comments.map { it.rate }.average()
        _uiState.value = _uiState.value.copy(
            averageRating = avg,
            totalComments = comments.size
        )
    }

    class Factory(
        private val productId: String,
        private val repository: ProductRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ReviewsViewModel(productId, repository) as T
        }
    }
}