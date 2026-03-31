package com.example.comisapp.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.comisapp.R
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onCreate() {
        super.onCreate()

        // FORZAR OBTENER TOKEN
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->

                if (!task.isSuccessful) {
                    Log.e("FCM", "Error obteniendo token")
                    return@addOnCompleteListener
                }

                val token = task.result

                Log.d("FCM", "TOKEN:")
                Log.d("FCM", token)
            }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        android.util.Log.d("FCM", "NUEVO TOKEN:")
        android.util.Log.d("FCM", token)

        val userId =
            com.google.firebase.auth.FirebaseAuth
                .getInstance()
                .currentUser
                ?.uid

        if (userId == null) {
            android.util.Log.d("FCM", "Usuario no logueado")
            return
        }

        val db =
            com.google.firebase.firestore
                .FirebaseFirestore
                .getInstance()

        db.collection("usuarios")
            .document(userId)
            .set(
                mapOf(
                    "fcmToken" to token
                ),
                com.google.firebase.firestore.SetOptions.merge()
            )

        android.util.Log.d("FCM", "TOKEN GUARDADO EN FIRESTORE")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val titulo =
            message.notification?.title
                ?: "Notificación"

        val descripcion =
            message.notification?.body
                ?: ""

        val manager =
            getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager

        val channelId = "notificaciones"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                channelId,
                "Notificaciones",
                NotificationManager.IMPORTANCE_HIGH
            )

            manager.createNotificationChannel(channel)
        }

        val notification =
            NotificationCompat.Builder(
                this,
                channelId
            )
                .setContentTitle(titulo)
                .setContentText(descripcion)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setAutoCancel(true)
                .build()

        manager.notify(1, notification)
    }
}