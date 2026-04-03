package com.example.comisapp.ui.screens.secondaryNavigationScreens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.comisapp.data.models.NotificationModel
import com.example.comisapp.data.repository.NotificationRepository
import com.example.comisapp.ui.screens.ViewModels.NotificationViewModel
import java.util.Calendar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    onBackClick: () -> Unit,
    viewModel: NotificationViewModel = viewModel(
        factory = NotificationViewModel.Factory(NotificationRepository())
    )
) {
    val notificaciones by viewModel.notificaciones.collectAsState()
    val isLoading      by viewModel.isLoading.collectAsState()
    val hayMas         by viewModel.hayMas.collectAsState()
    val noLeidas       by viewModel.noLeidasCount.collectAsState()

    val listState = rememberLazyListState()

    var cargandoScroll by remember { mutableStateOf(false) }

    LaunchedEffect(listState) {

        snapshotFlow {
            listState.layoutInfo
        }.collect { layoutInfo ->

            val totalItems = layoutInfo.totalItemsCount

            val lastVisibleIndex =
                layoutInfo.visibleItemsInfo.lastOrNull()?.index
                    ?: return@collect

            if (
                totalItems > 0 &&
                lastVisibleIndex >= totalItems - 3 &&
                !isLoading &&
                hayMas &&
                !cargandoScroll
            ) {

                cargandoScroll = true
                viewModel.cargarMas()
                cargandoScroll = false
            }
        }
    }

    val hoy = remember {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
    }

    val recientes = remember(notificaciones) {
        notificaciones.filter {
            it.fechaEnvioNotificacion?.toDate()?.after(hoy) == true
        }
    }
    val anteriores = remember(notificaciones) {
        notificaciones.filter {
            it.fechaEnvioNotificacion?.toDate()?.after(hoy) != true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Notificaciones", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    if (noLeidas > 0) {
                        TextButton(onClick = { viewModel.marcarTodasComoLeidas() }) {
                            Text("Leer todas", fontSize = 12.sp, color = Color(0xFF673AB7))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F6FA))
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {

//            item {
//                Spacer(Modifier.height(12.dp))
//                SupportCard()
//            }

            if (!isLoading && notificaciones.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.NotificationsNone,
                                contentDescription = null,
                                tint = Color(0xFF9CA3AF),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "No hay notificaciones",
                                color = Color(0xFF9CA3AF),
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }


            if (recientes.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(20.dp))
                    SectionTitle("RECIENTES")
                }
                items(
                    items = recientes,
                    key   = { it.notificacionId }
                ) { notif ->
                    NotificationCard(
                        notif = notif,
                        onTap = { viewModel.marcarComoLeida(notif.notificacionId) }
                    )
                }
            }


            if (anteriores.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(20.dp))
                    SectionTitle("ANTERIOR")
                }
                items(
                    items = anteriores,
                    key   = { it.notificacionId }
                ) { notif ->
                    NotificationCard(
                        notif = notif,
                        onTap = { viewModel.marcarComoLeida(notif.notificacionId) }
                    )
                }
            }


            item {
                Spacer(Modifier.height(16.dp))
                when {
                    isLoading -> {
                        Box(
                            Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color       = Color(0xFF673AB7),
                                modifier    = Modifier.size(28.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    }
                    !hayMas && notificaciones.isNotEmpty() -> {
                        Box(
                            Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No hay más notificaciones",
                                fontSize = 12.sp,
                                color    = Color(0xFF9CA3AF)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

//@Composable
//fun SupportCard() {
//    Card(
//        shape  = RoundedCornerShape(16.dp),
//        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1B2A)),
//        modifier = Modifier.fillMaxWidth()
//    ) {
//        Column(modifier = Modifier.padding(16.dp)) {
//            Text("SOPORTE TÉCNICO", color = Color.LightGray, fontSize = 12.sp)
//            Spacer(Modifier.height(8.dp))
//            Text(
//                "¿Necesitas ayuda con tus solicitudes?",
//                color      = Color.White,
//                fontSize   = 16.sp,
//                fontWeight = FontWeight.Bold
//            )
//            Spacer(Modifier.height(12.dp))
//            Button(
//                onClick = { },
//                colors  = ButtonDefaults.buttonColors(containerColor = Color.White)
//            ) {
//                Text(
//                    "Contactar Soporte",
//                    color      = Color.Black,
//                    fontWeight = FontWeight.ExtraBold,
//                    fontSize   = 15.sp
//                )
//            }
//        }
//    }
//}

@Composable
fun SectionTitle(title: String) {
    Text(
        text       = title,
        fontWeight = FontWeight.Bold,
        fontSize   = 12.sp,
        color      = Color.Gray,
        modifier   = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun NotificationCard(
    notif: NotificationModel,
    onTap: () -> Unit
) {
    val estado      = notif.tipo
    val purpleTheme = Color(0xFF673AB7)


    val mensajeMostrado = when (estado) {
        "credito_aprobado", "credito_aprobada" -> "Su solicitud de crédito fue aprobada"
        "credito_rechazado", "credito_rechazada" -> "Su solicitud de crédito fue rechazada"
        "credito_pendiente" -> "Su solicitud de crédito fue recibida y está en revisión"
        // --- Casos de Reclamos ---
        "reclamo_aprobado" -> notif.descripcion // El mensaje viene del index.js
        "reclamo_rechazado" -> notif.descripcion
        "reclamo_pendiente" -> notif.descripcion
        else -> notif.descripcion
    }


    val tituloPorTipo = when (estado) {
        "credito_aprobado", "credito_aprobada" -> "Solicitud aprobada"
        "credito_rechazado", "credito_rechazada" -> "Solicitud rechazada"
        "credito_pendiente" -> "Solicitud en revisión"
        // --- Títulos de Reclamos ---
        "reclamo_aprobado" -> "Reclamo Aprobado"
        "reclamo_rechazado" -> "Reclamo Rechazado"
        "reclamo_pendiente" -> "Reclamo en revisión"
        else -> notif.titulo
    }

    // 3. Color del Borde (Morado para aprobados, Rojo para rechazados)
    val borderColor = when (estado) {
        "credito_aprobado", "credito_aprobada", "reclamo_aprobado" -> Color(0xFFDDD6FE)
        "credito_rechazado", "credito_rechazada", "reclamo_rechazado" -> Color(0xFFFFCDD2)
        else -> Color(0xFFE5E7EB)
    }

    // 4. Color de fondo del Icono
    val iconBoxColor = when (estado) {
        "credito_aprobado", "credito_aprobada", "reclamo_aprobado" -> Color(0xFFEBE2FF)
        "credito_rechazado", "credito_rechazada", "reclamo_rechazado" -> Color(0xFFFFEBEE)
        else -> Color(0xFFF3F4F6)
    }

    // 5. Color del Icono
    val iconTint = when (estado) {
        "credito_aprobado", "credito_aprobada", "reclamo_aprobado" -> purpleTheme
        "credito_rechazado", "credito_rechazada", "reclamo_rechazado" -> Color(0xFFE53935)
        else -> Color(0xFF9CA3AF)
    }

    // 6. Selección del Icono (Check, Equis o Reloj)
    val iconoMostrado = when (estado) {
        "credito_aprobado", "credito_aprobada", "reclamo_aprobado" -> Icons.Default.CheckCircle
        "credito_rechazado", "credito_rechazada", "reclamo_rechazado" -> Icons.Default.Close
        else -> Icons.Default.AccessTime
    }

    // ... resto del código del formato de tiempo ...
    val time = remember(notif.fechaEnvioNotificacion) {
        val date = notif.fechaEnvioNotificacion?.toDate() ?: return@remember ""
        val now = java.util.Calendar.getInstance()
        val notifDate = java.util.Calendar.getInstance().apply { time = date }
        val formatTime = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.getDefault())
        val formatDate = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())

        when {
            now.get(java.util.Calendar.YEAR) == notifDate.get(java.util.Calendar.YEAR) &&
                    now.get(java.util.Calendar.DAY_OF_YEAR) == notifDate.get(java.util.Calendar.DAY_OF_YEAR) -> formatTime.format(date)
            now.get(java.util.Calendar.YEAR) == notifDate.get(java.util.Calendar.YEAR) &&
                    now.get(java.util.Calendar.DAY_OF_YEAR) - notifDate.get(java.util.Calendar.DAY_OF_YEAR) == 1 -> "Ayer, ${formatTime.format(date)}"
            else -> formatDate.format(date)
        }
    }

    Card(
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (!notif.leido) Color(0xFFF5F3FF) else Color.White
        ),
        border   = BorderStroke(1.dp, borderColor),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        onClick   = onTap
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(iconBoxColor, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconoMostrado,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = tituloPorTipo,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = Color(0xFF111827)
                        ),
                        modifier = Modifier.weight(1f),
                        maxLines = 1
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(time, fontSize = 12.sp, color = Color(0xFF9CA3AF))
                    if (!notif.leido) {
                        Spacer(Modifier.width(6.dp))
                        Box(modifier = Modifier.size(6.dp).background(purpleTheme, CircleShape))
                    }
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text = mensajeMostrado,
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