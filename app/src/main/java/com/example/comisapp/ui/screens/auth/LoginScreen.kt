package com.example.comisapp.ui.screens.auth


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.*
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
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.comisapp.R
import com.example.comisapp.ui.screens.components.ButtonComponent
import com.example.comisapp.ui.screens.components.circle1Diameter
import com.example.comisapp.ui.screens.components.circle1RadiusDp
import com.example.comisapp.ui.screens.components.circle2Diameter
import com.example.comisapp.ui.screens.components.circle2RadiusDp
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.comisapp.data.repository.AuthRepository
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log

private fun guardarToken(email: String) {

    FirebaseMessaging.getInstance().token
        .addOnSuccessListener { token ->

            FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(email)
                .update(
                    mapOf(
                        "fcmToken" to token
                    )
                )

            Log.d("FCM", "TOKEN GUARDADO: $token")
        }
        .addOnFailureListener {
            Log.e("FCM", "ERROR OBTENIENDO TOKEN")
        }
}


private fun Modifier.glassBorder(): Modifier =
    this.border(
        width = 1.dp,
        color = CardBorderColor.copy(alpha = 0.20f),
        shape = RoundedCornerShape(20.dp)
    )

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {}


) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var screenSize by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current

    val repo = remember { AuthRepository() }
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
            .onGloballyPositioned { screenSize = it.size }
    ) {


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
                .padding(horizontal = 24.dp).offset(y=-60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(64.dp))



            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.offset(y = 40.dp)
            ){
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ){}
                Image(
                    painter = painterResource(id = R.drawable.login_lock_img),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(220.dp).rotate(22f).offset(y = -15.dp)
                )
            }

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
                text = "Iniciar Sesión",
                color = TextDark,
                fontSize = 34.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1.8).sp
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Introduce tus credenciales para acceder\na tu cuenta.",
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
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)) {

                    // Correo electrónico
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

                    Spacer(Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CONTRASEÑA",
                            color = LabelColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.9.sp
                        )
                        TextButton(
                            onClick = onForgotPasswordClick,
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = "¿OLVIDASTE TU CONTRASEÑA?",
                                color = TextPurple,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Normal,
                                letterSpacing = 1.1.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = {
                            Text("••••••••", color = IconColor, fontSize = 14.sp)
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Lock,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible)
                                        Icons.Filled.Visibility
                                    else
                                        Icons.Filled.VisibilityOff,
                                    contentDescription = "Toggle visibility",
                                    tint = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
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

                    Spacer(Modifier.height(28.dp))


                    error?.let {
                        Text(it, color = Color.Red, fontSize = 12.sp)
                        Spacer(Modifier.height(8.dp))
                    }
                    ButtonComponent(
                        text = if (isLoading) "ENTRANDO..." else "ENTRAR",
                        onClick = {
                            scope.launch {
                                isLoading = true
                                error = null
                                try {
                                    repo.login(email, password)
                                    guardarToken(email)
                                    onLoginSuccess()
                                } catch (e: Exception) {
                                    error = "Correo o contraseña incorrectos"
                                } finally {
                                    isLoading = false
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}