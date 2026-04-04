package com.example.comisapp.ui.screens.secondaryNavigationScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lightbulb
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
import com.example.comisapp.data.repository.SuggestionRepository
import com.example.comisapp.data.repository.UserRepository
import com.example.comisapp.ui.screens.ViewModels.SuggestionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuggestionScreen(
    onBackClick: () -> Unit,
    suggestionViewModel: SuggestionViewModel = viewModel(
        factory = SuggestionViewModel.Factory(SuggestionRepository(), UserRepository())
    )
) {
    val sheetState = rememberModalBottomSheetState()
    var showSheet by remember { mutableStateOf(false) }

    val sugerencias by suggestionViewModel.sugerencias.collectAsState()
    val isLoading by suggestionViewModel.isLoading.collectAsState()
    val enviado by suggestionViewModel.enviado.collectAsState()

    var asunto by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    LaunchedEffect(enviado) {
        if (enviado) {
            showSheet = false
            asunto = "" // pa que se limpellen los campos
            descripcion = ""
            suggestionViewModel.resetEnviado()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Sugerencias",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF111827)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                            contentDescription = "Regresar",
                            modifier = Modifier.size(16.dp),
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showSheet = true },
                containerColor = Color.Black,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 16.dp, end = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar Sugerencia",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF9FAFB))
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(40.dp))
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = Color(0xFF8B5CF6),
                    modifier = Modifier.size(90.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "¿Tienes alguna\nidea?",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF0F172A),
                    lineHeight = 34.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Comentanos como podemos mejorar\nla calidad de nuestros servicios.",
                    fontSize = 15.sp,
                    color = Color(0xFF6B7280),
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            items(sugerencias) { sugerencia ->
                val fecha = sugerencia.fechaRegistro?.toDate()?.let {
                    java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(it)
                } ?: ""

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(text = fecha, fontSize = 12.sp, color = Color(0xFF9CA3AF))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = sugerencia.asunto,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = sugerencia.descripcion,
                            fontSize = 14.sp,
                            color = Color(0xFF4B5563),
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }

        if (showSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSheet = false },
                sheetState = sheetState,
                containerColor = Color.White,
                dragHandle = { BottomSheetDefaults.DragHandle(color = Color(0xFFE5E7EB)) }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Nueva Sugerencia",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "Tu voz nos ayuda a construir una plataforma mejor para todos.",
                        fontSize = 14.sp,
                        color = Color(0xFF6B7280),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
                    )

                    SuggestionLabel("ASUNTO:")
                    CustomInputField(
                        value = asunto,
                        onValueChange = { asunto = it },
                        placeholder = "Escribe un título para tu sugerencia...",
                        lines = 1
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    SuggestionLabel("CONTENIDO DE LA SUGERENCIA")
                    CustomInputField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        placeholder = "Escribe aquí tu idea, mejora o comentario...",
                        lines = 5
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = {
                            val usuarioId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
                            suggestionViewModel.enviarSugerencia(usuarioId, asunto, descripcion)
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("ENVIAR SUGERENCIA", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }

                    TextButton(onClick = { showSheet = false }) {
                        Text("CANCELAR", color = Color(0xFF4B5563), fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
fun SuggestionLabel(text: String) {
    Text(
        text = text,
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        fontSize = 11.sp,
        fontWeight = FontWeight.ExtraBold,
        color = Color(0xFF374151)
    )
}

@Composable
fun CustomInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    lines: Int
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, fontSize = 14.sp, color = Color(0xFF9CA3AF)) },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFEFF6FF),
            unfocusedContainerColor = Color(0xFFEFF6FF),
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent
        ),
        minLines = lines
    )
}