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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Tag
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.comisapp.R
import com.example.comisapp.data.models.credits.QuotaModel
import com.example.comisapp.ui.helpers.toFormattedDate
import com.example.comisapp.ui.screens.ViewModels.CreditDetailState
import com.example.comisapp.ui.screens.ViewModels.CreditDetailViewModel
import com.example.comisapp.ui.screens.ViewModels.ProductDetailState
import com.example.comisapp.ui.screens.components.ButtonComponent
import com.example.comisapp.ui.screens.components.IconButtonComponent
import com.example.comisapp.ui.theme.AppBg
import com.example.comisapp.ui.theme.CardBorderColor
import com.example.comisapp.ui.theme.TextPurple
import java.util.Locale
import java.util.Locale.getDefault
import kotlin.math.roundToInt

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
    onBack: () -> Unit = {},
    viewModel: CreditDetailViewModel
) {

    val uiState = viewModel.uiState
    val tabs = listOf("Detalles", "Reclamos")
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    var quotaItems = viewModel.quotasFlow.collectAsLazyPagingItems()

    when(uiState){
        is  CreditDetailState.Loading-> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is CreditDetailState.Error -> {
            Text(uiState.message)
        }
        is CreditDetailState.Success -> {
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
                val progressPercent = ((uiState.credit.cuotasPagadas.toFloat() / uiState.credit.plazoMeses.toFloat()) * 100).roundToInt()

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
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(uiState.credit.imagenProductoURL)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Imagen de ${uiState.credit.nombreProducto}",
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
                                        text = uiState.credit.estado.uppercase(getDefault()),
                                        color = TextGreen,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = uiState.credit.nombreProducto,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark
                                )
                                Text(
                                    text = "Valor por cuota: ${uiState.credit.cuotaMensual}",
                                    color = TextPurple,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )

                                if(uiState.credit.estado == "Aprobado" || uiState.credit.estado == "Liquidado"){
                                    Spacer(modifier = Modifier.height(16.dp))

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text("Progreso del pago", fontSize = 12.sp, color = TextGray, fontWeight = FontWeight.Medium)
                                        Text("${uiState.credit.cuotasPagadas} de ${uiState.credit.plazoMeses}", fontSize = 12.sp, color = TextDark, fontWeight = FontWeight.Medium)
                                    }

                                    LinearProgressIndicator(
                                        progress = { uiState.credit.cuotasPagadas.toFloat() / uiState.credit.plazoMeses.toFloat() },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(10.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                        color = AppPurple,
                                        trackColor = Color(0xFFE0E0FF),
                                    )
                                    Text(
                                        text = "${progressPercent}% completado",
                                        fontSize = 10.sp,
                                        color = TextGray,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.End
                                    )
                                }

                            }
                        }
                    }

                    if(uiState.credit.estado == "Aprobado" || uiState.credit.estado == "Liquidado"){
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
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
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
                                        modifier = Modifier
                                            .padding(start = 16.dp, top = 24.dp, bottom = 12.dp)
                                            .fillMaxWidth()
                                    )
                                }

                                items(
                                    count = quotaItems.itemCount,
                                    key = quotaItems.itemKey { it.id }
                                ) {
                                    cuotaNum ->
                                    val quota = quotaItems[cuotaNum]
                                    quota?.let {
                                        CuotaItem(quota,("${(quotaItems.itemCount)-(cuotaNum)}/${uiState.credit.plazoMeses}"))
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
                                        modifier = Modifier
                                            .padding(start = 20.dp, top = 24.dp, bottom = 8.dp)
                                            .fillMaxWidth()
                                    )
                                }
                                item { ReclamoArchivadoItem() }
                            }
                        }
                    }
                    else if(uiState.credit.estado=="Pendiente"){
                        item{
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally

                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFF5F5F5))
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = Color(0xFF757575),
                                        modifier = Modifier
                                            .size(18.dp)
                                            .padding(top = 1.dp)
                                    )
                                    Text(
                                        text = "Tu solicitud está siendo revisada por nuestro equipo. " +
                                                "Recibirás una respuesta en los siguientes 7 días hábiles " +
                                                "después de la fecha de emisión.",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color(0xFF474747),
                                            lineHeight = 18.sp
                                        )
                                    )
                                }

                                Spacer(modifier=Modifier.height(20.dp))


                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                ){
                                    ButtonComponent(
                                        text = "CANCELAR CREDITO",
                                        onClick = {  },
                                        modifier = Modifier.weight(4f)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    IconButtonComponent(
                                        icon = Icons.Default.Edit,
                                        onClick = {  },
                                        modifier = Modifier.weight(1f)

                                    )
                                }


                            }

                        }


                    }


                }
            }
        }
    }

}

@Composable
fun CuotaItem(
    quota: QuotaModel,
    numCuota: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
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
                Text(text = "${quota.fechaRegistro?.toFormattedDate()}", fontWeight = FontWeight.Bold, color = TextDark)
                Text(text = "CUOTA $numCuota", fontSize = 12.sp, color = TextGray)
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "L ${quota.monto}", fontWeight = FontWeight.Bold, color = TextDark, fontSize = 16.sp)
        }
    }
}

@Composable
fun SeccionRealizarReclamo() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp, vertical = 32.dp),
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
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

