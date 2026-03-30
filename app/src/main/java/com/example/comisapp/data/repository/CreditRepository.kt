package com.example.comisapp.data.repository

import com.example.comisapp.data.models.CategoryModel
import com.example.comisapp.data.models.credits.CreditModel
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

    suspend fun getCreditByUserId(userId:String, limit: Int): List<CreditModel>{
        var query = db.collection("creditos")
            .whereEqualTo("usuarioId", userId)
            .limit(limit.toLong())

        lastDocument?.let {
            query = query.startAfter(it)
        }

        val result = query.get().await()

        if(!result.isEmpty){
            lastDocument = result.documents.last()
        }

        return result.documents.mapNotNull {
            document ->
            val credit = document.toObject(CreditModel::class.java)
            credit?.copy(id = document.id)
        }

    }
}