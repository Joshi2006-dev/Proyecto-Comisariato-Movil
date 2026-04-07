package com.example.comisapp.data.repository

import android.net.Uri
import com.example.comisapp.data.models.credits.ClaimModel
import com.example.comisapp.data.models.credits.CreditModel
import com.example.comisapp.data.models.credits.QuotaModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await
import java.util.UUID



class CreditRepository {
    private val db = Firebase.firestore

    suspend fun addNewCredit(credit: CreditModel){
        try {
            val newCreditRef = hashMapOf(
                "cantidad" to credit.cantidad,
                "cuotaMensual" to credit.cuotaMensual,
                "estado" to credit.estado,
                "fechaRegistro" to com.google.firebase.Timestamp.now(),
                "imagenProductoURL" to credit.imagenProductoURL,
                "nombreProducto" to credit.nombreProducto,
                "plazoMeses" to credit.plazoMeses,
                "productoId" to credit.productoId,
                "plazoGarantia" to credit.plazoGarantia,
                "plazoGarantiaDia" to credit.plazoGarantiaDia,
                "totalCredito" to credit.totalCredito,
                "usuarioId" to credit.usuarioId,
                "usuario" to credit.usuario,
                "empleadoId" to credit.empleadoId,
                "cuotasPagadas" to 0
            )
            db.collection("creditos").add(newCreditRef).await()
        } catch (e: Exception){
            println("Error al crear el crédito: $e")
        }
    }

    suspend fun deleteCredit(creditId: String) {
        try {
            db.collection("creditos").document(creditId).delete().await()
        } catch (e: Exception) {
            println("Error al eliminar el crédito: $e")
        }
    }

    suspend fun getCreditByUserId(userId: String, limit: Int, lastDocument: DocumentSnapshot? = null, estado: String? = null): PageResult<CreditModel> {
        var query = db.collection("creditos").whereEqualTo("usuarioId", userId)
        if(estado != null) query = query.whereEqualTo("estado", estado)
        query = query.orderBy("fechaRegistro", Query.Direction.DESCENDING).limit(limit.toLong())
        lastDocument?.let { query = query.startAfter(it) }
        val result = query.get().await()
        val credits = result.documents.mapNotNull { doc -> doc.toObject(CreditModel::class.java)?.copy(id = doc.id) }
        return PageResult(data = credits, lastDocument = if (!result.isEmpty) result.documents.last() else null)
    }

    suspend fun getCreditById(creditId: String): CreditModel? {
        return try {
            val doc = db.collection("creditos").document(creditId).get().await()
            if(doc.exists()) doc.toObject(CreditModel::class.java)?.copy(id = doc.id) else null
        } catch (e: Exception) { null }
    }

    suspend fun getQoutasByCreditId(creditId: String, limit: Int, lastDocument: DocumentSnapshot? = null): PageResult<QuotaModel> {
        var query = db.collection("creditos").document(creditId).collection("cuotas").limit(limit.toLong())
        lastDocument?.let { query = query.startAfter(it) }
        val result = query.get().await()
        val cuotas = result.documents.mapNotNull { doc -> doc.toObject(QuotaModel::class.java)?.copy(id = doc.id) }
        return PageResult(data = cuotas, lastDocument = if (!result.isEmpty) result.documents.last() else null)
    }

    suspend fun getClaimsByCreditId(creditId: String): List<ClaimModel> {
        val result = db.collection("reclamos").whereEqualTo("creditId", creditId).get().await()
        return result.documents.mapNotNull { document -> document.toObject(ClaimModel::class.java)?.copy(id = document.id) }
    }

    suspend fun addClaim(creditId: String, claim: ClaimModel, imageUri: Uri, solicitante: String): ClaimModel {
        val storageRef = Firebase.storage.reference.child("reclamos/$creditId/${UUID.randomUUID()}.jpg")
        storageRef.putFile(imageUri).await()
        val downloadUrl = storageRef.downloadUrl.await().toString()
        val userEmail = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.email ?: ""
        val data = hashMapOf(
            "creditId" to creditId,
            "asunto" to claim.asunto,
            "descripcion" to claim.descripcion,
            "estado" to "Pendiente",
            "evidenciaUrl" to downloadUrl,
            "fechaEmision" to com.google.firebase.Timestamp.now(),
            "usuarioId" to userEmail,
            "solicitante" to solicitante
        )
        val docRef = db.collection("reclamos").add(data).await()
        return claim.copy(id = docRef.id, evidenciaUrl = downloadUrl, estado = "Pendiente")
    }

    suspend fun deleteClaim(claimId: String) {
        db.collection("reclamos").document(claimId).delete().await()
    }

    suspend fun updateCredit(creditId: String, cantidad: Int, plazoMeses: Int, cuotaMensual: Double, totalCredito: Double) {
        db.collection("creditos").document(creditId).update(mapOf(
            "cantidad" to cantidad,
            "plazoMeses" to plazoMeses,
            "cuotaMensual" to cuotaMensual,
            "totalCredito" to totalCredito
        )).await()
    }

    suspend fun updateCreditStatus(creditId: String, nuevoEstado: String, usuarioId: String) {
        try {
            db.collection("creditos").document(creditId).update(mapOf(
                "estado" to nuevoEstado,
                "fechaRevision" to com.google.firebase.Timestamp.now()
            )).await()
        } catch (e: Exception) {
            println("Error al actualizar estado: $e")
        }
    }
}