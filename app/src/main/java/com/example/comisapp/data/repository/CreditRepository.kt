package com.example.comisapp.data.repository

import android.net.Uri
import android.util.Log.e
import com.example.comisapp.data.models.CategoryModel
import com.example.comisapp.data.models.ProductModel
import com.example.comisapp.data.models.credits.ClaimModel
import com.example.comisapp.data.models.credits.CreditModel
import com.example.comisapp.data.models.credits.QuotaModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await
import java.util.UUID


class CreditRepository {
    private val db = Firebase.firestore
    private var lastDocument: DocumentSnapshot? = null

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
                "totalCredito" to credit.totalCredito,
                "usuarioId" to credit.usuarioId,
                "usuario" to credit.usuario,
                "empleadoId" to credit.empleadoId,
                "cuotasPagadas" to 0
            )

            db.collection("creditos")
                .add(newCreditRef)
                .await()
        }catch (e: Exception){
            println("Error al crear el crédito: $e")
        }

    }

    suspend fun getCreditByUserId(
        userId: String,
        limit: Int,
        lastDocument: DocumentSnapshot? = null
    ): PageResult<CreditModel> {
        var query = db.collection("creditos")
            .whereEqualTo("usuarioId", userId)
            .limit(limit.toLong())

        lastDocument?.let { query = query.startAfter(it) }

        val result = query.get().await()

        val credits = result.documents.mapNotNull { doc ->
            doc.toObject(CreditModel::class.java)?.copy(id = doc.id)
        }

        return PageResult(
            data = credits,
            lastDocument = if (!result.isEmpty) result.documents.last() else null
        )
    }


    suspend fun getCreditById(creditId: String): CreditModel? {
        return try{
            val doc = db.collection("creditos")
                .document(creditId)
                .get()
                .await()
            if(doc.exists()){
                doc.toObject(CreditModel::class.java)?.copy(id = doc.id)
            }else{
                null
            }
        }catch (e: Exception){
            null
        }
    }

    suspend fun getQoutasByCreditId(creditId: String, limit: Int, lastDocument: DocumentSnapshot? = null):
            PageResult<QuotaModel>
    {
        var query = db.collection("creditos")
            .document(creditId)
            .collection("cuotas")
            .limit(limit.toLong())

        lastDocument?.let { query = query.startAfter(it) }

        val result = query.get().await()

        val cuotas = result.documents.mapNotNull { doc ->
            doc.toObject(QuotaModel::class.java)?.copy(id = doc.id)
        }
        return PageResult(
            data = cuotas,
            lastDocument = if (!result.isEmpty) result.documents.last() else null
        )

    }


    suspend fun getClaimsByCreditId(creditId:String) : List<ClaimModel>{
        val result = db.collection("creditos")
            .document(creditId)
            .collection("reclamos")
            .get()
            .await()
        return result.documents.mapNotNull { document ->
            val reclamo = document.toObject(ClaimModel::class.java)
            reclamo?.copy(id = document.id)
        }

    }

    suspend fun addClaim(
        creditId: String,
        claim: ClaimModel,
        imageUri: Uri
    ): ClaimModel {
        val storageRef = Firebase.storage.reference
            .child("reclamos/$creditId/${UUID.randomUUID()}.jpg")

        storageRef.putFile(imageUri).await()
        val downloadUrl = storageRef.downloadUrl.await().toString()

        val data = hashMapOf(
            "asunto"       to claim.asunto,
            "descripcion"  to claim.descripcion,
            "estado"       to "Pendiente",
            "evidenciaUrl" to downloadUrl,
            "fechaEmision" to com.google.firebase.Timestamp.now(),
            "respuesta"    to null,
            "atendidoPor"  to null,
            "fechaRespuesta" to null
        )

        val docRef = db.collection("creditos")
            .document(creditId)
            .collection("reclamos")
            .add(data)
            .await()

        return claim.copy(
            id = docRef.id,
            evidenciaUrl = downloadUrl,
            estado = "Pendiente"
        )
    }

    suspend fun deleteClaim(creditId: String, claimId: String) {
        db.collection("creditos")
            .document(creditId)
            .collection("reclamos")
            .document(claimId)
            .delete()
            .await()
    }

    suspend fun updateCredit(
        creditId: String,
        cantidad: Int,
        plazoMeses: Int,
        cuotaMensual: Double,
        totalCredito: Double
    ) {
        db.collection("creditos")
            .document(creditId)
            .update(
                mapOf(
                    "cantidad"     to cantidad,
                    "plazoMeses"   to plazoMeses,
                    "cuotaMensual" to cuotaMensual,
                    "totalCredito" to totalCredito
                )
            ).await()
    }

    suspend fun updateCreditStatus(
        creditId: String,
        nuevoEstado: String,
        usuarioId: String
    ) {
        try {
            val creditRef = db.collection("creditos")
                .document(creditId)

            // 1. Actualizar estado
            creditRef.update(
                mapOf(
                    "estado" to nuevoEstado,
                    "fechaRevision" to com.google.firebase.Timestamp.now()
                )
            ).await()

            // 2. Crear notificación
            val notificationData = hashMapOf(
                "titulo" to "Cambio de estado de crédito",
                "descripcion" to "Su crédito cambió a: $nuevoEstado",
                "tipo" to "credito",
                "destinatarioId" to usuarioId,
                "leido" to false,
                "fechaEnvioNotificacion" to com.google.firebase.Timestamp.now()
            )

            db.collection("notificaciones")
                .add(notificationData)
                .await()

        } catch (e: Exception) {
            println("Error al actualizar estado y crear notificación: $e")
        }
    }
}