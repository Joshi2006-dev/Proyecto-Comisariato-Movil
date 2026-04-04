package com.example.comisapp.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.comisapp.data.repository.AuthRepository
import com.example.comisapp.ui.screens.components.ButtonComponent
import com.example.comisapp.ui.screens.components.circle1Diameter
import com.example.comisapp.ui.screens.components.circle1RadiusDp
import com.example.comisapp.ui.screens.components.circle2Diameter
import com.example.comisapp.ui.screens.components.circle2RadiusDp
import com.example.comisapp.ui.theme.*
import kotlinx.coroutines.launch

private fun Modifier.glassBorder(): Modifier =
    this.border(
        width = 1.dp,
        color = CardBorderColor.copy(alpha = 0.20f),
        shape = RoundedCornerShape(20.dp)
    )

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit = {},
    onBackToLogin: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val repo = remember { AuthRepository() }
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
    ) {

        Box(modifier = Modifier.fillMaxSize().blur(radius = 90.dp)) {
            Box(
                modifier = Modifier
                    .size(circle1Diameter)
                    .offset(x = -circle1RadiusDp, y = 0.dp)
                    .align(Alignment.CenterStart)
                    .background(color = Color(0xFF7C3AED).copy(alpha = 0.15f), shape = androidx.compose.foundation.shape.CircleShape)
            )
            Box(
                modifier = Modifier
                    .size(circle2Diameter)
                    .offset(x = circle2RadiusDp, y = circle2RadiusDp)
                    .align(Alignment.BottomEnd)
                    .background(color = Color(0xFF64748B).copy(alpha = 0.15f), shape = androidx.compose.foundation.shape.CircleShape)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = TextDark, fontWeight = FontWeight.ExtraBold)) { append("Comis") }
                    withStyle(SpanStyle(color = TextPurple, fontWeight = FontWeight.ExtraBold)) { append("App") }
                },
                fontSize = 20.sp
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = "Crear Cuenta",
                color = TextDark,
                fontSize = 34.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1.8).sp
            )

            Text(
                text = "Regístrate para comenzar a gestionar\ntus comisiones.",
                color = TextGray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(Modifier.height(40.dp))


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(CardBg.copy(alpha = 0.3f))
                    .glassBorder()
            ) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)) {

                    Text("CORREO ELECTRÓNICO", color = LabelColor, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.9.sp)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { Text("tu-correo@autorizado.com", color = IconColor, fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = FieldBg, unfocusedContainerColor = FieldBg,
                            focusedBorderColor = FieldFocus, unfocusedBorderColor = FieldBorder
                        )
                    )

                    Spacer(Modifier.height(20.dp))

                    Text("CONTRASEÑA NUEVA", color = LabelColor, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.9.sp)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { Text("••••••••", color = IconColor, fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp)) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = FieldBg, unfocusedContainerColor = FieldBg,
                            focusedBorderColor = FieldFocus, unfocusedBorderColor = FieldBorder
                        )
                    )

                    Spacer(Modifier.height(28.dp))

                    if (error != null) {
                        Text(error!!, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))
                    }

                    ButtonComponent(
                        text = if (isLoading) "REGISTRANDO..." else "REGISTRARSE",
                        onClick = {
                            scope.launch {
                                isLoading = true
                                error = null
                                try {
                                    repo.register(email, password)
                                    onRegisterSuccess()
                                } catch (e: Exception) {
                                    error = e.message ?: "Error al registrarse"
                                } finally {
                                    isLoading = false
                                }
                            }
                        }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            TextButton(onClick = onBackToLogin) {
                Text("¿YA TIENES CUENTA? INICIA SESIÓN", color = TextPurple, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}