package com.example.comisapp.data.repository

import com.example.comisapp.data.models.ClaimModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class ClaimRepository {
    private val db = FirebaseFirestore.getInstance()

    fun obtenerMisReclamos(email: String): Flow<List<ClaimModel>> = callbackFlow {
        val snapshotListener = db.collection("reclamos")
            .whereEqualTo("usuarioId", email)
            .orderBy("fechaEmision", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null) {
                    val reclamos = snapshot.toObjects(ClaimModel::class.java)
                    trySend(reclamos)
                }
            }
        awaitClose { snapshotListener.remove() }
    }
}