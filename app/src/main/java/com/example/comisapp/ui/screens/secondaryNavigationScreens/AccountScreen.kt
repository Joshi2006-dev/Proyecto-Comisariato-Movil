package com.example.comisapp.ui.screens.secondaryNavigationScreens

import LempiraHelper.toLempira
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.BusinessCenter
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.PhoneInTalk
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.comisapp.R
import com.example.comisapp.ui.screens.ViewModels.AccountViewModel
import com.example.comisapp.ui.screens.ViewModels.EmployeeDetailState
import com.example.comisapp.ui.theme.TextPurple


val TextDarkBlue = Color(0xFF1A2B48)
val LightGrayBg = Color(0xFFF8FAFC)
val CustomPurple = Color(0xFF8B5CF6)
val ActiveGreen = Color(0xFF22C55E)
val BorderGray = Color(0xFFE2E8F0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    onBackClick: () -> Unit,
    nav: NavController,
    viewModel: AccountViewModel
) {
    var progress by remember { mutableStateOf(0f) }
    val uiState = viewModel.uiState
    val limiteUsado = viewModel.limiteUsado
    Scaffold(
        containerColor = LightGrayBg,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Cuenta",
                        fontWeight = FontWeight.Bold,
                        color = TextDarkBlue
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Regresar",
                            modifier = Modifier.size(20.dp),
                            tint = TextDarkBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        if (uiState is EmployeeDetailState.Success) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BorderGray, RoundedCornerShape(12.dp)),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Avatar con Yoshi similar y borde exacto
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .background(Color(0xFFF1F5F9), CircleShape)
                                .border(4.dp, Color(0xFFE2E8F0).copy(alpha = 0.5f), CircleShape) // Borde doble sutil
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = uiState.employee.fotoUrl,
                                contentDescription = "Foto de perfil",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "${uiState.employee.nombres}  ${uiState.employee.apellidos}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 22.sp,
                            color = TextDarkBlue,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = uiState.employee.cargo,
                            fontSize = 15.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Surface(
                            color = Color(0xFFF0FDF4),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = uiState.employee.estado,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                                color = ActiveGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        HorizontalDivider(thickness = 0.5.dp, color = BorderGray)
                        Spacer(modifier = Modifier.height(20.dp))

                        InfoRow(icon = Icons.Outlined.Badge, label = "DNI", value = uiState.employee.dni)
                        Spacer(modifier = Modifier.height(16.dp))
                        InfoRow(icon = Icons.Outlined.BusinessCenter, label = "DEPARTAMENTO", value = uiState.employee.departamento)
                        Spacer(modifier = Modifier.height(16.dp))
                        InfoRow(icon = Icons.Outlined.CalendarMonth, label = "SE UNIÓ", value = uiState.employee.fechaIngreso)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().height(120.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Box(modifier = Modifier.align(Alignment.CenterEnd)){
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_credit_limit_card),
                                contentDescription = "Icono de badge",
                                tint = TextPurple.copy(alpha = 0.45f),
                                modifier = Modifier
                                    .size(140.dp)
                                    .rotate(-25f)
                                    .offset(x = 20.dp)

                            )
                        }
                        Column(modifier = Modifier.padding(24.dp).align(Alignment.TopStart)) {
                            Text(
                                text = "SALARIO MENSUAL",
                                color = Color.LightGray,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = uiState.employee.salario.toLempira(),
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))


                if (limiteUsado != null) {
                    progress = ((limiteUsado / uiState.employee.limiteCredito) * 10.0).toInt() / 10.0f
                }

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BorderGray, RoundedCornerShape(12.dp)),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Box(modifier = Modifier.align(Alignment.CenterEnd)){
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.ic_salary_card),
                                contentDescription = "Icono de badge",
                                tint = Color.Black.copy(alpha = 0.05f),
                                modifier = Modifier
                                    .size(160.dp)
                                    .rotate(-25f)
                                    .offset(x = 20.dp)

                            )
                        }
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Límite de Crédito",
                                fontWeight = FontWeight.Bold,
                                color = TextDarkBlue,
                                fontSize = 16.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Text(
                                    text = limiteUsado?.toLempira()?:"",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 20.sp,
                                    color = TextDarkBlue
                                )
                                Text(
                                    text = "${(progress * 100)}% en Uso",
                                    color = CustomPurple,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(CircleShape), // Puntas redondeadas exactas
                                color = CustomPurple,
                                trackColor = Color(0xFFF1F5F9) // Fondo de la barra exacto
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "de ${uiState.employee.limiteCredito.toLempira()} Lps usables",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }

                }

                Spacer(modifier = Modifier.height(24.dp))

                Column {
                    Text(
                        text = "INFORMACIÓN DE CONTACTO",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderGray, RoundedCornerShape(16.dp)),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            // Fila de teléfono con icono circular
                            ContactRow(
                                icon = Icons.Outlined.PhoneInTalk,
                                label = "TELÉFONO",
                                value = uiState.employee.telefono
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // Fila de email con icono circular
                            ContactRow(
                                icon = Icons.Outlined.Email,
                                label = "CORREO",
                                value = uiState.employee.correo
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Column {
                    Text(
                        text = "OPCIONES DE CUENTA",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, BorderGray, RoundedCornerShape(16.dp)),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column() {
                            SettingsRow(
                                icon = ImageVector.vectorResource(R.drawable.ic_ideas),
                                label = "Sugerencias",
                                onClick = {
                                    nav.navigate("sugerencias")
                                }
                            )

                            HorizontalDivider(thickness = 0.5.dp, color = BorderGray)

                            SettingsRow(
                                icon = ImageVector.vectorResource(R.drawable.ic_support),
                                label = "Centro de Ayuda",
                                onClick = {
                                    nav.navigate("helpCenter")
                                }
                            )

                            HorizontalDivider(thickness = 0.5.dp, color = BorderGray)

                            SettingsRow(
                                icon = ImageVector.vectorResource(R.drawable.ic_support),
                                label = "Cambiar Contraseña",
                                onClick = {
                                    nav.navigate("changePassword")
                                }
                            )

                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

            }
        }


    }
}
@Composable
fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Gray, // Gris de icono exacto
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = label.uppercase(),
                fontSize = 10.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextDarkBlue
            )
        }
    }
}


@Composable
fun ContactRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(Color(0xFFF5F3FF), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = CustomPurple,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = label,
                fontSize = 11.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextDarkBlue
            )
        }
    }
}


@Composable
fun SettingsRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.clickable(enabled = true, onClick = {onClick()}).fillMaxWidth().padding(horizontal = 20.dp).height(80.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = label,
                fontSize = 16.sp,
                color = Color.Black,
                fontWeight = FontWeight.Normal
            )
        }
    }
}