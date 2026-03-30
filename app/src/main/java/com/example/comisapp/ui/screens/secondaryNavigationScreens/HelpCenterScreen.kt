package com.example.comisapp.ui.screens.secondaryNavigationScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.comisapp.data.repository.AyudaRepository
import com.example.comisapp.ui.screens.ViewModels.HelpCenterViewModel
//Importaciones para whatsapp
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpCenterScreen(
    onBackClick: () -> Unit,
    helpCenterViewModel: HelpCenterViewModel = viewModel(
        factory = HelpCenterViewModel.Factory(AyudaRepository())
    )
) {
    val ayudas by helpCenterViewModel.ayudas.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Centro de Ayuda",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = Color(0xFF0F172A)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                            contentDescription = "Regresar",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFF))
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item {

                Spacer(modifier = Modifier.height(40.dp))

                Icon(
                    imageVector = Icons.Default.HeadsetMic,
                    contentDescription = "Soporte",
                    tint = Color(0xFF8B5CF6),
                    modifier = Modifier.size(100.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "¿En qué podemos\nayudarte?",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF0F172A),
                    lineHeight = 36.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Explora nuestras guías o busca soluciones rápidas a tus dudas sobre créditos y servicios.",
                    fontSize = 16.sp,
                    color = Color(0xFF4B5563),
                    lineHeight = 24.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))
            }

            item {

                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF1F5F9).copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {

                        Text(
                            text = "Preguntas Frecuentes",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF0F172A)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Obtiene todo el texto del campo explicacion
                        val textoCompleto =
                            ayudas.firstOrNull()?.explicacion ?: ""

                        // Divide el texto en respuestas usando línea en blanco
                        val respuestas =
                            textoCompleto.split("\n\n")


                        val respuestaCredito =
                            respuestas.getOrNull(0) ?: ""


                        val respuestaAprobacion =
                            respuestas.getOrNull(1) ?: ""


                        val respuestaMetodoPago =
                            respuestas.getOrNull(2) ?: ""


                        FAQItem(
                            question = "¿Cómo solicito un nuevo crédito?",
                            answer = respuestaCredito
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        FAQItem(
                            question = "¿Cuál es el tiempo de aprobación?",
                            answer = respuestaAprobacion
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        FAQItem(
                            question = "¿Cómo cambio mi método de pago?",
                            answer = respuestaMetodoPago
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }

            item {

                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Black),
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "Soporte Atento\n24/7",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            lineHeight = 32.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Si no pudiste encontrar la solución a tus problemas puedes contactarte con el equipo técnico",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                val numero = "50432505304"
                                val mensaje = "Hola, necesito ayuda con CREDIFLOW:"
                                val url = "https://wa.me/$numero?text=${Uri.encode(mensaje)}"
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(size = 12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(height = 56.dp)
                        ) {
                            Text(
                                text = "CONTACTAR AHORA",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = "© 2026 Centro de Soporte Digital ComisApp. Todos los derechos reservados.",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }
        }
    }
}

@Composable
fun FAQItem(
    question: String,
    answer: String
) {

    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                expanded = !expanded
            }
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = question,
                    modifier = Modifier.weight(1f),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1E293B)
                )

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color(0xFF8B5CF6),
                    modifier = Modifier.size(24.dp)
                )
            }

            if (expanded) {

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = answer,
                    fontSize = 14.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 20.sp
                )
            }
        }
    }
}