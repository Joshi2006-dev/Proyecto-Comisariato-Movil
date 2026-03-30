package com.example.comisapp.data.repository

import com.example.comisapp.data.models.CategoryModel
import com.example.comisapp.data.models.ProductModel
import com.example.comisapp.data.models.credits.CreditModel
import com.example.comisapp.data.models.credits.QuotaModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await


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
}