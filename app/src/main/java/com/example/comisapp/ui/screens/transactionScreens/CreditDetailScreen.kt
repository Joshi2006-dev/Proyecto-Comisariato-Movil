package com.example.comisapp.ui.screens.transactionScreens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Tag
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.Font
import com.example.comisapp.R
import com.example.comisapp.ui.screens.components.ButtonComponent
import com.example.comisapp.ui.theme.AppBg
import com.example.comisapp.ui.theme.CardBorderColor
import com.example.comisapp.ui.theme.TextPurple

// Colores del Diseño
val AppPurple = Color(0xFF7E3FF2)
val BackgroundColor = Color(0xFFF8F9FF)
val CardBackground = Color.White
val TextDark = Color(0xFF1A1A2E)
val TextGray = Color(0xFF7A7D91)
val GreenAccent = Color(0xFFECFDF5)
val TextGreen = Color(0xFF10B981)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleCreditoScreen(
    onBack: () -> Unit = {}
) {
    val tabs = listOf("Detalles", "Reclamos")
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detalle del Crédito", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = AppBg
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    border = BorderStroke(
                        width = 0.5.dp,
                        color = CardBorderColor.copy(alpha = 0.2f)
                    )
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Image(
                            painter = painterResource(R.drawable.testportada),
                            contentDescription = "Bolso Beige",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(280.dp)
                                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                            contentScale = ContentScale.Crop
                        )

                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp),
                            color = GreenAccent,
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = "APROBADO",
                                color = TextGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "MacBook Pro M3",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Text(
                            text = "Valor por cuota: L946.50",
                            color = TextPurple,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Progreso del pago", fontSize = 12.sp, color = TextGray, fontWeight = FontWeight.Medium)
                            Text("4 de 6 cuotas", fontSize = 12.sp, color = TextDark, fontWeight = FontWeight.Medium)
                        }

                        LinearProgressIndicator(
                            progress = { 4f / 6f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            color = AppPurple,
                            trackColor = Color(0xFFE0E0FF),
                        )
                        Text(
                            text = "66% completado",
                            fontSize = 10.sp,
                            color = TextGray,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )
                    }
                }
            }

            stickyHeader {
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = BackgroundColor,
                    contentColor = AppPurple,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            height = 2.dp,
                            color = AppPurple,
                        )
                    },
                    divider = {
                        HorizontalDivider(
                            thickness = 0.5.dp,
                            color = CardBorderColor.copy(alpha = 0.2f)
                        )
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    text = title,
                                    color = if (selectedTabIndex == index) AppPurple else TextGray,
                                    fontWeight =  if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                            },
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }
            }

            when (selectedTabIndex) {
                0 -> {
                    item {
                        Text(
                            "Últimas cuotas liquidadas",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark,
                            modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 12.dp).fillMaxWidth()
                        )
                    }
                    items(listOf("4/6", "3/6", "2/6", "1/6")) { cuotaNum ->
                        CuotaItem(cuotaNum)
                    }
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ){
                            ButtonComponent(
                                text = "VER HISTORIAL COMPLETO",
                                onClick = {  }
                            )
                        }

                    }
                }
                1 -> {
                    item { SeccionRealizarReclamo() }
                    item {
                        Text(
                            "HISTORIAL DE RECLAMOS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = TextGray,
                            modifier = Modifier.padding(start = 20.dp, top = 24.dp, bottom = 8.dp).fillMaxWidth()
                        )
                    }
                    item { ReclamoArchivadoItem() }
                }
            }
        }
    }
}

@Composable
fun CuotaItem(numCuota: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(13.dp),
        border = BorderStroke(
            width = 0.5.dp,
            color = CardBorderColor.copy(alpha = 0.2f)
        )
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                color = TextPurple.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Pagado",
                    tint = AppPurple,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = "15 Oct 2023", fontWeight = FontWeight.Bold, color = TextDark)
                Text(text = "CUOTA $numCuota", fontSize = 12.sp, color = TextGray)
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "L 946.50", fontWeight = FontWeight.Bold, color = TextDark, fontSize = 16.sp)
        }
    }
}

@Composable
fun SeccionRealizarReclamo() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "¿Algo no va bien?",
            fontSize = 30.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextDark,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "Estamos aquí para ayudarte a resolver cualquier inconveniente con tu crédito.",
            fontSize = 16.sp,
            color = TextGray,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
        Spacer(modifier = Modifier.height(28.dp))
        ButtonComponent(
            text = "REALIZAR RECLAMO",
            onClick = {  }
        )
    }
}

@Composable
fun ReclamoArchivadoItem() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Producto dañado", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                Surface(
                    color = Color(0xFFF1F1F1),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("RESUELTO", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "La cartera presenta algunas descosturas y podría romperce facilmente.",
                color = TextGray, fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                // Iconos temporales
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.DateRange, contentDescription = null, tint = TextGray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("05 Sep, 2023", fontSize = 12.sp, color = TextGray)
                }
                Spacer(modifier = Modifier.width(20.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Tag, contentDescription = null, tint = TextGray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("ID: #77412", fontSize = 12.sp, color = TextGray)
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DetallePreview() {
    DetalleCreditoScreen()
}