package com.example.comisapp.data.repository

import com.example.comisapp.data.models.settings.GuaranteePoliticModel
import com.example.comisapp.data.models.settings.PlazoModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class SettingRepository {
    private val db = Firebase.firestore


    suspend fun getPlazos(): List<PlazoModel> {
        val result = db.collection("configuraciones")
            .document("config_global")
            .collection("plazos")
            .get()
            .await()

        return result.documents.mapNotNull { document ->
            val plazo = document.toObject(PlazoModel::class.java)
            plazo?.copy(id = document.id)
        }

    }

    suspend fun getGuaranteePolitics(): GuaranteePoliticModel?{
        return try{
            val document = db.collection("configuraciones")
                .document("config_global")
                .collection("politicasGarantia")
                .document("polit_gloabl")
                .get()
                .await()
            document.toObject(GuaranteePoliticModel::class.java)?.copy(id = document.id)
        }catch (e: Exception){
            null
        }

    }

}