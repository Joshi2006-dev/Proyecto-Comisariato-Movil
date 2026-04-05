package com.example.comisapp.data.repository

import com.example.comisapp.data.models.EmployeeModel
import com.example.comisapp.data.models.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.AggregateField
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await




class EmployeeRepository {
    private val db = Firebase.firestore

    suspend fun getEmployeeById( empleadoId:String): EmployeeModel? {
        return try {
            val doc = db.collection("empleados")
                .document(empleadoId)
                .get().await()
            if (doc.exists()) {
                doc.toObject(EmployeeModel::class.java)?.copy(id = doc.id)
            } else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun calcularLimiteUsado(usuarioId: String): Double {
        return try {
            val creditosRef = db.collection("creditos")

            val q = creditosRef
                .whereEqualTo("usuarioId", usuarioId)
                .whereEqualTo("estado", "Aprobado")

            val snapshot = q.aggregate(
                AggregateField.sum("cuotaMensual")
            ).get(AggregateSource.SERVER).await()

            snapshot.get(AggregateField.sum("cuotaMensual")) as? Double ?: 0.0
        } catch (e: Exception) {
            0.0
        }
    }


}