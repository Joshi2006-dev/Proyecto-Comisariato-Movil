package com.example.comisapp.ui.PaginationSource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.comisapp.data.models.NotificationModel
import com.example.comisapp.data.repository.NotificationRepository
import com.google.firebase.firestore.DocumentSnapshot

class NotificationPaginationSource(
    private val repo: NotificationRepository
) : PagingSource<DocumentSnapshot, NotificationModel>() {

    override fun getRefreshKey(
        state: PagingState<DocumentSnapshot, NotificationModel>
    ): DocumentSnapshot? {
        return null
    }

    override suspend fun load(
        params: LoadParams<DocumentSnapshot>
    ): LoadResult<DocumentSnapshot, NotificationModel> {

        return try {

            Log.d(
                "Paging",
                "Cargando notificaciones, key: ${params.key}, loadSize: ${params.loadSize}"
            )

            val result =
                repo.getNotificacionesPaginadas(
                    lastVisible = params.key
                )

            LoadResult.Page(
                data = result.first,
                prevKey = null,
                nextKey = result.second
            )

        } catch (e: Exception) {

            Log.e(
                "Paging",
                "Error al cargar notificaciones: ${e.message}",
                e
            )

            LoadResult.Error(e)
        }
    }
}