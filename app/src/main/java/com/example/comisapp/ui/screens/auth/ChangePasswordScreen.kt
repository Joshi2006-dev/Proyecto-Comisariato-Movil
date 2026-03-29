package com.example.comisapp.ui.screens.auth


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


private fun Modifier.glassBorder(): Modifier =
    this.border(
        width = 1.dp,
        color = CardBorderColor.copy(alpha = 0.20f),
        shape = RoundedCornerShape(20.dp)
    )

@Composable
fun ChangePasswordScreen(
    onSucces: () -> Unit = {},
    viewModel: AuthViewModel,
    onBackClick: () -> Unit = {}

) {

    var currentPassword by remember { mutableStateOf("") }
    var currentPasswordVisible by remember { mutableStateOf(false) }

    var newPassword by remember { mutableStateOf("") }
    var newPasswordVisible by remember { mutableStateOf(false) }


    var screenSize by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current

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
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Spacer(Modifier.height(2.dp))

            Text(
                text = "Cambiar Contraseña",
                color = TextDark,
                fontSize = 34.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1.8).sp
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

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CONTRASEÑA ACTUAL",
                            color = LabelColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.9.sp
                        )

                    }

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
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
                            IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
                                Icon(
                                    imageVector = if (currentPasswordVisible)
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
                        visualTransformation = if (currentPasswordVisible)
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

                    Spacer(Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CONTRASEÑA NUEVA",
                            color = LabelColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.9.sp
                        )

                    }

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
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
                            IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                                Icon(
                                    imageVector = if (newPasswordVisible)
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
                        visualTransformation = if (newPasswordVisible)
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


                    if(viewModel.statusMsg.isNotEmpty()){
                        Text(viewModel.statusMsg,
                            color = Color.Red,
                            fontSize = 12.sp)
                    }
                    ButtonComponent(
                        text = if (viewModel.isLoading) "CAMBIANDO..." else "CAMBIAR CONTRASEÑA",
                        onClick = {
                            viewModel.changePassword(currentPassword, newPassword)
                        }
                    )
                    Column(
                        modifier = Modifier.wrapContentSize().padding(5.dp),
                    ) {
                        TextButton(
                            onClick = {onBackClick()},
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
                                    text = "REGRESAR",
                                    color = TextPurple,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.1.sp
                                )

                            }

                        }
                    }
                }

            }
        }

        LaunchedEffect(viewModel.isSuccess) {
            if (viewModel.isSuccess) {
                onSucces()
            }
        }
    }
}