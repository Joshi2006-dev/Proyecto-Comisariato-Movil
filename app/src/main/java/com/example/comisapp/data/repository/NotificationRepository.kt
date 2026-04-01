package com.example.comisapp.data.repository

import com.example.comisapp.data.models.NotificationModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.tasks.await

class NotificationRepository {

    private val db = Firebase.firestore
    private val currentUserId: String?
        get() = FirebaseAuth.getInstance().currentUser?.email

    // Trae notificaciones del usuario actual + las GLOBAL en tiempo real
    fun getNotificaciones(): Flow<List<NotificationModel>> = callbackFlow {
        val userId = currentUserId ?: run {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        // Escucha las del usuario específico
        val listenerPersonal = db.collection("notificaciones")
            .whereEqualTo("destinatarioId", userId)
            .orderBy("fechaEnvioNotificacion", Query.Direction.DESCENDING)
            .addSnapshotListener { snapPersonal, _ ->
                val personal = snapPersonal?.documents?.mapNotNull { doc ->
                    doc.toObject(NotificationModel::class.java)?.copy(notificacionId = doc.id)
                } ?: emptyList()
                trySend(personal) // temporal, se combina abajo
            }

        awaitClose { listenerPersonal.remove() }
    }

    // Flow combinado: GLOBAL + del usuario
    fun getNotificacionesCombinadas(): Flow<List<NotificationModel>> = callbackFlow {
        val userId = currentUserId ?: run {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        var listaPersonal: List<NotificationModel> = emptyList()
        var listaGlobal: List<NotificationModel> = emptyList()

        fun emitirCombinada() {
            val combinada = (listaPersonal + listaGlobal)
                .sortedByDescending { it.fechaEnvioNotificacion }
            trySend(combinada)
        }

        // Listener para notificaciones del usuario
        val listenerPersonal = db.collection("notificaciones")
            .whereEqualTo("destinatarioId", userId)
            .orderBy("fechaEnvioNotificacion", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, _ ->
                listaPersonal = snap?.documents?.mapNotNull { doc ->
                    doc.toObject(NotificationModel::class.java)?.copy(notificacionId = doc.id)
                } ?: emptyList()
                emitirCombinada()
            }

        // Listener para notificaciones GLOBAL
        val listenerGlobal = db.collection("notificaciones")
            .whereEqualTo("destinatarioId", "GLOBAL")
            .orderBy("fechaEnvioNotificacion", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, _ ->
                listaGlobal = snap?.documents?.mapNotNull { doc ->
                    doc.toObject(NotificationModel::class.java)?.copy(notificacionId = doc.id)
                } ?: emptyList()
                emitirCombinada()
            }

        awaitClose {
            listenerPersonal.remove()
            listenerGlobal.remove()
        }
    }

    // Cuenta no leídas (GLOBAL + personales)
    fun getNoLeidasCount(): Flow<Int> = callbackFlow {
        val userId = currentUserId ?: run {
            trySend(0)
            close()
            return@callbackFlow
        }

        var countPersonal = 0
        var countGlobal = 0

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

    // Marca una como leída
    suspend fun marcarComoLeida(notificacionId: String) {
        db.collection("notificaciones")
            .document(notificacionId)
            .update("leido", true)
            .await()
    }

    // Marca todas las del usuario como leídas
    suspend fun marcarTodasComoLeidas() {
        val userId = currentUserId ?: return

        // Personales
        val personales = db.collection("notificaciones")
            .whereEqualTo("destinatarioId", userId)
            .whereEqualTo("leido", false)
            .get().await()

        // Globales
        val globales = db.collection("notificaciones")
            .whereEqualTo("destinatarioId", "GLOBAL")
            .whereEqualTo("leido", false)
            .get().await()

        (personales.documents + globales.documents).forEach { doc ->
            doc.reference.update("leido", true)
        }
    }
}