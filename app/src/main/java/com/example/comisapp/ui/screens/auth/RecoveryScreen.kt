package com.example.comisapp.ui.screens.auth

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.comisapp.R
import com.example.comisapp.data.repository.AuthRepository
import com.example.comisapp.ui.screens.components.ButtonComponent
import com.example.comisapp.ui.theme.BgColor
import com.example.comisapp.ui.theme.ButtonBg
import com.example.comisapp.ui.theme.CardBg
import com.example.comisapp.ui.theme.CardBorderColor
import com.example.comisapp.ui.theme.FieldBg
import com.example.comisapp.ui.theme.FieldBorder
import com.example.comisapp.ui.theme.FieldFocus
import com.example.comisapp.ui.theme.IconColor
import com.example.comisapp.ui.theme.LabelColor
import com.example.comisapp.ui.theme.LogoBg
import com.example.comisapp.ui.theme.TextDark
import com.example.comisapp.ui.theme.TextGray
import com.example.comisapp.ui.theme.TextPurple
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.S)
private fun Modifier.blurBackground(radiusDp: Float): Modifier =
    this.graphicsLayer {
        renderEffect = android.graphics.RenderEffect
            .createBlurEffect(radiusDp, radiusDp, android.graphics.Shader.TileMode.CLAMP)
            .asComposeRenderEffect()
    }

private fun Modifier.glassBorder(): Modifier =
    this.border(
        width = 1.dp,
        color = CardBorderColor.copy(alpha = 0.20f),
        shape = RoundedCornerShape(20.dp)
    )

@Composable
fun RecoveryScreen(
    onBack: () -> Unit = {},
    repo: AuthRepository = remember { AuthRepository() }
) {
    val scope = rememberCoroutineScope()
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errMsg by remember { mutableStateOf("") }
    var showSuccess by remember { mutableStateOf(false) }
    var screenSize by remember { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
            .onGloballyPositioned { screenSize = it.size }
    ) {

        val circle1RadiusDp = 180.dp
        val circle1Diameter = circle1RadiusDp * 2
        val circle2RadiusDp = 160.dp
        val circle2Diameter = circle2RadiusDp * 2
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(radius = 90.dp)
        ){
            Box(
                modifier = Modifier
                    .size(circle1Diameter)
                    .offset(x = -circle1RadiusDp, y = 0.dp)
                    .align(Alignment.CenterStart)
                    .background(
                        color = Color(0xFF7C3AED).copy(alpha = 0.15f),
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
            )

            Box(
                modifier = Modifier
                    .size(circle2Diameter)
                    .offset(x = circle2RadiusDp, y = circle2RadiusDp)
                    .align(Alignment.BottomEnd)
                    .background(
                        color = Color(0xFF64748B).copy(alpha = 0.15f),
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
            )
        }





        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp).offset(y=(-30).dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(64.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = TextDark, fontWeight = FontWeight.ExtraBold, letterSpacing = 0.sp)) {
                        append("Comis")
                    }
                    withStyle(SpanStyle(color = TextPurple, fontWeight = FontWeight.ExtraBold, letterSpacing = 0.sp)) {
                        append("App")
                    }
                },
                fontSize = 20.sp
            )

            Spacer(Modifier.height(2.dp))

            Text(
                text = "Recuperar Contraseña",
                color = TextDark,
                fontSize = 34.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                lineHeight = 45.sp,
                letterSpacing = (-1.8).sp
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Ingresa tu correo para recibir un enlace de \nrecuperación.",
                color = TextGray,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(30.dp))


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(CardBg.copy(alpha = 0.3f))
                    .glassBorder()
            ) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {

                    Text(
                        text = "CORREO ELECTRÓNICO",
                        color = LabelColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.9.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = {
                            Text("nombre@ejemplo.com", color = IconColor, fontSize = 14.sp)
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Email,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextDark,
                            unfocusedTextColor = TextDark,
                            focusedContainerColor = FieldBg,
                            unfocusedContainerColor = FieldBg,
                            focusedBorderColor = FieldFocus,
                            unfocusedBorderColor = FieldBorder,
                            cursorColor = FieldFocus
                        )
                    )
                    if (errMsg.isNotEmpty()) {
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = errMsg,
                            color = Color(0xFFEF4444),
                            fontSize = 12.sp,
                            letterSpacing = 0.sp
                        )
                    }

                    Spacer(Modifier.height(20.dp))



                    ButtonComponent(text = if (isLoading) "ENVIANDO..." else "ENVIAR ENLACE",
                        onClick = {if (email.isBlank()) {
                            errMsg = "Ingresa tu correo electrónico"
                            return@ButtonComponent
                        }
                            scope.launch {
                                isLoading = true
                                errMsg = ""
                                try {
                                    repo.forgotPassword(email)
                                    showSuccess = true
                                } catch (e: Exception) {
                                    errMsg = "No se pudo enviar el correo. Verifica el email."
                                } finally {
                                    isLoading = false
                                }
                            }},
                        isEnabled = !isLoading
                    )
                }

            }
            Spacer(Modifier.height(10.dp))
            Column(
                modifier = Modifier.wrapContentSize().padding(5.dp),
            ) {
                TextButton(
                    onClick = onBack,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.ArrowBackIosNew,
                            contentDescription = null,
                            tint = TextPurple,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(5.dp))
                        Text(
                            text = "VOLVER AL INICIO DE SESIÓN",
                            color = TextPurple,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.1.sp
                        )

                    }

                }
            }


        }

        if (showSuccess) {
            AlertDialog(
                onDismissRequest = { onBack() },
                title = {
                    Text(
                        text = "Correo enviado",
                        color = TextDark,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = "Revisa tu bandeja de entrada y sigue las instrucciones para restablecer tu contraseña.",
                        color = TextGray,
                        fontSize = 14.sp
                    )
                },
                confirmButton = {
                    TextButton(onClick = { onBack() }) {
                        Text("Aceptar", color = TextPurple, fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = CardBg,
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}