package com.example.comisapp.data.repository

import com.example.comisapp.data.models.NotificationModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class NotificationRepository {

    private val db = Firebase.firestore

    // Email porque así se guarda usuarioId en Firestore
    private val currentUserId: String?
        get() = FirebaseAuth.getInstance().currentUser?.email

    companion object {
        const val PAGE_SIZE = 5L
    }

    fun getNotificacionesCombinadas(): Flow<List<NotificationModel>> = callbackFlow {
        val userId = currentUserId ?: run {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        var listaPersonal: List<NotificationModel> = emptyList()
        var listaGlobal:   List<NotificationModel> = emptyList()

        fun emitir() {
            val combinada = (listaPersonal + listaGlobal)
                .sortedByDescending { it.fechaEnvioNotificacion }
            trySend(combinada)
        }

        val listenerPersonal = db.collection("notificaciones")
            .whereEqualTo("destinatarioId", userId)
            .orderBy("fechaEnvioNotificacion", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, _ ->
                listaPersonal = snap?.documents?.mapNotNull { doc ->
                    doc.toObject(NotificationModel::class.java)
                        ?.copy(notificacionId = doc.id)
                } ?: emptyList()
                emitir()
            }

        val listenerGlobal = db.collection("notificaciones")
            .whereEqualTo("destinatarioId", "GLOBAL")
            .orderBy("fechaEnvioNotificacion", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, _ ->
                listaGlobal = snap?.documents?.mapNotNull { doc ->
                    doc.toObject(NotificationModel::class.java)
                        ?.copy(notificacionId = doc.id)
                } ?: emptyList()
                emitir()
            }

        awaitClose {
            listenerPersonal.remove()
            listenerGlobal.remove()
        }
    }

    suspend fun getNotificacionesPaginadas(
        lastVisible: DocumentSnapshot? = null
    ): Pair<List<NotificationModel>, DocumentSnapshot?> {

        val userId = currentUserId ?: return Pair(emptyList(), null)

        var queryPersonal = db.collection("notificaciones")
            .whereEqualTo("destinatarioId", userId)
            .orderBy("fechaEnvioNotificacion", Query.Direction.DESCENDING)
            .limit(PAGE_SIZE)

        if (lastVisible != null) {
            queryPersonal = queryPersonal.startAfter(lastVisible)
        }

        val queryGlobal = db.collection("notificaciones")
            .whereEqualTo("destinatarioId", "GLOBAL")
            .orderBy("fechaEnvioNotificacion", Query.Direction.DESCENDING)
            .limit(PAGE_SIZE)

        val snapPersonal = queryPersonal.get().await()
        val snapGlobal   = queryGlobal.get().await()

        val personal = snapPersonal.documents.mapNotNull { doc ->
            doc.toObject(NotificationModel::class.java)
                ?.copy(notificacionId = doc.id)
        }

        val global = snapGlobal.documents.mapNotNull { doc ->
            doc.toObject(NotificationModel::class.java)
                ?.copy(notificacionId = doc.id)
        }

        val combinada = (personal + global)
            .sortedByDescending { it.fechaEnvioNotificacion }

        // 🔧 CORREGIDO — evita crash en scroll
        val nuevoLastVisible =
            if (snapPersonal.documents.isNotEmpty())
                snapPersonal.documents.last()
            else
                lastVisible

        return Pair(combinada, nuevoLastVisible)
    }

    fun getNoLeidasCount(): Flow<Int> = callbackFlow {
        val userId = currentUserId ?: run {
            trySend(0)
            close()
            return@callbackFlow
        }

        var countPersonal = 0
        var countGlobal   = 0

        fun emitir() = trySend(countPersonal + countGlobal)

        val l1 = db.collection("notificaciones")
            .whereEqualTo("destinatarioId", userId)
            .whereEqualTo("leido", false)
            .addSnapshotListener { snap, _ ->
                countPersonal = snap?.size() ?: 0
                emitir()
            }

        val l2 = db.collection("notificaciones")
            .whereEqualTo("destinatarioId", "GLOBAL")
            .whereEqualTo("leido", false)
            .addSnapshotListener { snap, _ ->
                countGlobal = snap?.size() ?: 0
                emitir()
            }

        awaitClose {
            l1.remove()
            l2.remove()
        }
    }

    suspend fun marcarComoLeida(notificacionId: String) {
        db.collection("notificaciones")
            .document(notificacionId)
            .update("leido", true)
            .await()
    }

    suspend fun marcarTodasComoLeidas() {

        val userId = currentUserId ?: return

        val personales = db.collection("notificaciones")
            .whereEqualTo("destinatarioId", userId)
            .whereEqualTo("leido", false)
            .get()
            .await()

        val globales = db.collection("notificaciones")
            .whereEqualTo("destinatarioId", "GLOBAL")
            .whereEqualTo("leido", false)
            .get()
            .await()

        (personales.documents + globales.documents).forEach {
            it.reference.update("leido", true)
        }
    }
}