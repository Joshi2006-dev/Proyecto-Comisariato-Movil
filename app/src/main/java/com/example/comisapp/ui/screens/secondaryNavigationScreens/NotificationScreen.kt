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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    onBackClick: () -> Unit
) {

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

            item {
                Spacer(modifier = Modifier.height(20.dp))
                SectionTitle("RECIENTES")
            }

            item {
                NotificationCard(
                    title = "Solicitud Aprobada",
                    message = "Tu solicitud de Essential Leather Tote ha sido aprobada, ve a la sección de créditos para consultar más información.",
                    time = "hace 2 min",
                    approved = true
                )
            }

            item {
                NotificationCard(
                    title = "Solicitud Rechazada",
                    message = "Tu solicitud de iPhone 16 Pro Max ha sido rechazada: No cumples con los requisitos mínimos para optar a este crédito.",
                    time = "hace 1 h",
                    approved = false
                )
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
                SectionTitle("ANTERIOR")
            }

            item {
                NotificationCard(
                    title = "Solicitud Rechazada",
                    message = "Tu solicitud de Toyota Prado 2027 ha sido rechazada: No cumples con los requisitos mínimos para optar a este crédito.",
                    time = "Ayer",
                    approved = false
                )
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
    title: String,
    message: String,
    time: String,
    approved: Boolean
) {

    val purpleTheme = Color(0xFF673AB7)
    val purpleBackground = Color(0xFFF5F3FF)
    val borderColor = if (approved) Color(0xFFDDD6FE) else Color(0xFFF3F4F6)
    val iconBoxColor = if (approved) Color(0xFFEBE2FF) else Color(0xFFF1F3FC)
    val iconTint = if (approved) purpleTheme else Color(0xFF5A606C)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (approved) purpleBackground else Color.White
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
                    imageVector = if (approved) Icons.Default.CheckCircle else Icons.Default.Cancel,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = Color(0xFF111827)
                        )
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = time,
                            fontSize = 12.sp,
                            color = Color(0xFF9CA3AF)
                        )
                        if (approved) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(purpleTheme, CircleShape)
                            )
                        }
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