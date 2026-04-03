package com.example.comisapp.notifications

import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.example.comisapp.MainActivity
import com.example.comisapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        const val CHANNEL_ID = "notificaciones_credito"
    }

    override fun onCreate() {
        super.onCreate()
        crearCanal()
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Nuevo token: $token")

        val userId = FirebaseAuth.getInstance().currentUser?.email ?: run {
            Log.d("FCM", "Usuario no logueado, token no guardado")
            return
        }

        FirebaseFirestore.getInstance()
            .collection("usuarios")
            .document(userId)
            .set(mapOf("fcmToken" to token), SetOptions.merge())
            .addOnSuccessListener { Log.d("FCM", "Token guardado en Firestore") }
            .addOnFailureListener { Log.e("FCM", "Error guardando token", it) }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val titulo       = message.data["titulo"]      ?: message.notification?.title ?: "Notificación"
        val descripcion  = message.data["descripcion"] ?: message.notification?.body  ?: ""
        val imageUrl     = message.data["imagenUrl"]   ?: message.notification?.imageUrl?.toString()
        val appEnForeground = estaEnPrimerPlano()

        Log.d("FCM", "Mensaje recibido — titulo: $titulo")
        Log.d("FCM", "En foreground: $appEnForeground | imageUrl: $imageUrl")

        if (!imageUrl.isNullOrBlank() && !appEnForeground) {
            Thread {
                try {
                    val bitmap: Bitmap = Glide.with(applicationContext)
                        .asBitmap()
                        .load(imageUrl)
                        .submit()
                        .get()
                    mostrarConImagen(titulo, descripcion, bitmap)
                } catch (e: Exception) {
                    Log.e("FCM", "Error cargando imagen: ${e.message}")
                    mostrarSimple(titulo, descripcion)
                }
            }.start()
        } else {
            mostrarSimple(titulo, descripcion)
        }
    }

    private fun estaEnPrimerPlano(): Boolean {
        return try {
            val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val appProcesses = activityManager.runningAppProcesses ?: return false
            appProcesses.any {
                it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                        it.processName == packageName
            }
        } catch (e: Exception) {
            false
        }
    }

    private fun mostrarSimple(titulo: String, cuerpo: String) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificacion = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(titulo)
            .setContentText(cuerpo)
            .setStyle(NotificationCompat.BigTextStyle().bigText(cuerpo))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(getPendingIntent())
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notificacion)
    }


    private fun mostrarConImagen(titulo: String, cuerpo: String, imagen: Bitmap) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        val bigPictureStyle = NotificationCompat.BigPictureStyle()
            .bigPicture(imagen)
            .bigLargeIcon(null as Bitmap?)


        val notificacionBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Tu icono de la app
            .setContentTitle(titulo)
            .setContentText(cuerpo)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(getPendingIntent())
            .setLargeIcon(imagen)
            .setStyle(bigPictureStyle)

        notificacionBuilder.setGroupSummary(true) // A veces ayuda en ciertas versiones de Android
        notificacionBuilder.setGroup("comisapp_group_creditos") // Agruparlas puede forzar que se expandan juntas


        val idNotificacion = System.currentTimeMillis().toInt()
        manager.notify(idNotificacion, notificacionBuilder.build())
    }

    private fun getPendingIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun crearCanal() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val canal = NotificationChannel(
                CHANNEL_ID,
                "Notificaciones",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones de solicitudes de crédito"
                enableVibration(true)
            }
            manager.createNotificationChannel(canal)
        }
    }
}