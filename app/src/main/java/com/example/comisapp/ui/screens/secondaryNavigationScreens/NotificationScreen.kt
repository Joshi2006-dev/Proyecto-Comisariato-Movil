package com.example.comisapp.ui.screens.secondaryNavigationScreens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.comisapp.data.repository.NotificationRepository
import com.example.comisapp.ui.screens.ViewModels.NotificationViewModel
import java.util.Calendar
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Close
import com.example.comisapp.data.models.NotificationModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    onBackClick: () -> Unit,
    viewModel: NotificationViewModel = viewModel(
        factory = NotificationViewModel.Factory(NotificationRepository())
    )
) {
    val notificaciones by viewModel.notificaciones.collectAsState()

    val hoy = Calendar.getInstance().apply{
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set (Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time

    val recientes= notificaciones.filter { notif->
        val fecha = notif.fechaEnvioNotificacion?.toDate()
        fecha != null && fecha.after(hoy)
    }
    val anteriores = notificaciones.filter { notif->
        val fecha = notif.fechaEnvioNotificacion?.toDate()
        fecha== null || !fecha.after(hoy)
    }


    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text(
                        text = "Notificaciones",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },

                navigationIcon = {

                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }

                },
                actions = {
                    // Botón marcar todas como leídas
                    if (notificaciones.any { !it.leido }) {
                        TextButton(onClick = { viewModel.marcarTodasComoLeidas() }) {
                            Text(
                                text = "Leer todas",
                                fontSize = 12.sp,
                                color = Color(0xFF673AB7)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }

    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F6FA))
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            item {
                SupportCard()
            }

            if(notificaciones.isEmpty()){
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.NotificationsNone,
                                contentDescription = null,
                                tint = Color(0xFF9CA3AF),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No hay notificaciones",
                                color = Color(0xFF9CA3AF),
                                fontSize = 15.sp
                            )
                        }

                    }
                }
            }

            // Sección RECIENTES
            if (recientes.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    SectionTitle("RECIENTES")
                }
                items(recientes) { notif ->
                    NotificationCard(
                        notif = notif,
                        onTap = { viewModel.marcarComoLeida(notif.notificacionId) }
                    )
                }
            }

            // Sección ANTERIOR
            if (anteriores.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    SectionTitle("ANTERIOR")
                }
                items(anteriores) { notif ->
                    NotificationCard(
                        notif = notif,
                        onTap = { viewModel.marcarComoLeida(notif.notificacionId) }
                    )
                }
            }
        }
    }
}


@Composable
fun SupportCard() {

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0D1B2A)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = "SOPORTE TÉCNICO",
                color = Color.LightGray,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "¿Necesitas ayuda con tus solicitudes?",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                )
            ) {

                Text(
                    text = "Contactar Soporte",
                    color = Color.Black,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp
                )

            }

        }

    }

}

@Composable
fun SectionTitle(title: String) {

    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        color = Color.Gray,
        modifier = Modifier.padding(vertical = 8.dp)
    )

}

@Composable
fun NotificationCard(
    notif: NotificationModel,
    onTap: () -> Unit
) {
    val estado = notif.tipo

    val title = notif.titulo ?: ""
    val message = notif.descripcion ?: ""
    val time = notif.fechaEnvioNotificacion
        ?.toDate()
        ?.let {
            java.text.SimpleDateFormat(
                "hh:mm a",
                java.util.Locale.getDefault()
            ).format(it)
        }
        ?: ""

    val purpleTheme = Color(0xFF673AB7)
    val purpleBackground = Color(0xFFF5F3FF)
//    val borderColor = if (approved) Color(0xFFDDD6FE) else Color(0xFFF3F4F6)
//    val iconBoxColor = if (approved) Color(0xFFEBE2FF) else Color(0xFFF1F3FC)
//    val iconTint = if (approved) purpleTheme else Color(0xFF5A606C)

//    val purpleTheme = Color(0xFF673AB7)

    val borderColor = when (estado) {
        "credito_aprobado" -> Color(0xFFDDD6FE)
        "credito_rechazado" -> Color(0xFFE5E7EB)
        else -> Color(0xFFE5E7EB)
    }

    val iconBoxColor = when (estado) {
        "credito_aprobado" -> Color(0xFFEBE2FF)
        "credito_rechazado" -> Color(0xFFF3F4F6)
        else -> Color(0xFFF3F4F6)
    }

    val iconTint = when (estado) {
        "credito_aprobado" -> purpleTheme
        "credito_rechazado" -> Color(0xFF9CA3AF)
        else -> Color(0xFF9CA3AF)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (estado == "credito_leido") purpleBackground else Color.White
        ),
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        color = iconBoxColor,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (estado) {
                        "credito_aprobado" -> Icons.Default.CheckCircle
                        "credito_rechazado" -> Icons.Default.Close
                        else -> Icons.Default.AccessTime
                    },
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // TITULO
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = Color(0xFF111827)
                        ),
                        modifier = Modifier.weight(1f),
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // HORA
                    Text(
                        text = time,
                        fontSize = 12.sp,
                        color = Color(0xFF9CA3AF),
                        maxLines = 1
                    )

                    if (!notif.leido) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(purpleTheme, CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF4B5563),
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                )
            }
        }
    }
}