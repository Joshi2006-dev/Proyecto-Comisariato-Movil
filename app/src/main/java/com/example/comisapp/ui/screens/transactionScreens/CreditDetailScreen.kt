package com.example.comisapp.ui.screens.transactionScreens

import LempiraHelper.toLempira
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
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
import androidx.compose.material.icons.filled.DeleteOutline
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
import com.example.comisapp.data.models.credits.ClaimModel
import com.example.comisapp.data.models.credits.CreditModel
import com.example.comisapp.data.models.credits.QuotaModel
import com.example.comisapp.data.models.settings.PlazoModel
import com.example.comisapp.ui.helpers.toFormattedDate
import com.example.comisapp.ui.screens.ViewModels.ClaimSubmitState
import com.example.comisapp.ui.screens.ViewModels.CreditDetailState
import com.example.comisapp.ui.screens.ViewModels.CreditDetailViewModel
import com.example.comisapp.ui.screens.ViewModels.EditCreditState
import com.example.comisapp.ui.screens.ViewModels.ProductDetailState
import com.example.comisapp.ui.screens.components.ButtonComponent
import com.example.comisapp.ui.screens.components.IconButtonComponent
import com.example.comisapp.ui.screens.components.PaymentBottomSheetContent
import com.example.comisapp.ui.theme.AppBg
import com.example.comisapp.ui.theme.CardBorderColor
import com.example.comisapp.ui.theme.TextPurple
import java.util.Locale
import java.util.Locale.getDefault
import kotlin.math.roundToInt
import com.example.comisapp.ui.screens.components.PaymentMode
import com.example.comisapp.ui.theme.RpbRed
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.HourglassEmpty
import com.example.comisapp.ui.screens.components.BodyText
import com.example.comisapp.ui.screens.components.ReviewsBottomSheetContent

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

    val quotaItems = viewModel.quotasFlow.collectAsLazyPagingItems()
    val claims = viewModel.claims.collectAsState()

    val editState   = viewModel.editCreditState
    val claimState  = viewModel.claimSubmitState

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope    = rememberCoroutineScope()

    val editSheetState   = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showEditSheet    by remember { mutableStateOf(false) }

    var showReviewsSheet by remember { mutableStateOf(false) }
    val reviewsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val claimSheetState  = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showClaimSheet   by remember { mutableStateOf(false) }

    var selectedClaim by remember { mutableStateOf<ClaimModel?>(null) }
    val claimDetailSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (selectedClaim != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedClaim = null },
            sheetState       = claimDetailSheetState,
            containerColor   = Color.White,
            shape            = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            dragHandle       = null
        ) {
            ClaimDetailSheetContent(
                claim   = selectedClaim!!,
                onClose = { selectedClaim = null }
            )
        }
    }

    LaunchedEffect(editState) {
        when (editState) {
            is EditCreditState.Success -> {
                editSheetState.hide()
                showEditSheet = false
                snackbarHostState.showSnackbar("Crédito actualizado")
                viewModel.resetEditState()
            }
            is EditCreditState.Error -> {
                snackbarHostState.showSnackbar(editState.message)
                viewModel.resetEditState()
            }
            else -> Unit
        }
    }

    LaunchedEffect(claimState) {
        when (claimState) {
            is ClaimSubmitState.Success -> {
                claimSheetState.hide()
                showClaimSheet = false
                snackbarHostState.showSnackbar("Reclamo enviado")
                viewModel.resetClaimState()
            }
            is ClaimSubmitState.Error -> {
                snackbarHostState.showSnackbar(claimState.message)
                viewModel.resetClaimState()
            }
            else -> Unit
        }
    }

    if (showEditSheet && uiState is CreditDetailState.Success) {
        ModalBottomSheet(
            onDismissRequest = { showEditSheet = false },
            sheetState       = editSheetState,
            containerColor   = Color.White,
            shape            = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            EditCreditSheetContent(
                credit   = uiState.credit,
                plazos   = viewModel.plazos,
                isLoading = editState is EditCreditState.Loading,
                onConfirm = { cantidad, plazoMeses ->
                    viewModel.editCredit(
                        cantidad      = cantidad,
                        plazoMeses    = plazoMeses,
                        precioCredito = uiState.credit.totalCredito / uiState.credit.cantidad
                    )
                }
            )
        }
    }

    if (showReviewsSheet  && uiState is CreditDetailState.Success) {
        ModalBottomSheet(
            onDismissRequest = { showReviewsSheet = false },
            sheetState = reviewsSheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            ReviewsBottomSheetContent(
                productId = uiState.credit.productoId,
                currentUserId = viewModel.currentUser?.empleadoId.orEmpty(),
                currentUserName = viewModel.currentUser?.nombre.orEmpty(),
                currentUserEmail = viewModel.currentUser?.correo.orEmpty(),
                currentUserPhotoUrl = viewModel.currentUser?.fotoUrl.orEmpty(),
                onClose = { showReviewsSheet = false },
                canComment = true
            )
        }
    }


    if (showClaimSheet) {
        ModalBottomSheet(
            onDismissRequest = { showClaimSheet = false },
            sheetState       = claimSheetState,
            containerColor   = Color.White,
            shape            = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            NewClaimSheetContent(
                isLoading = claimState is ClaimSubmitState.Loading,
                onConfirm = { asunto, descripcion, uri ->
                    viewModel.submitClaim(asunto, descripcion, uri)
                },
                onCancel  = {
                    coroutineScope.launch {
                        claimSheetState.hide()
                        showClaimSheet = false
                    }
                }
            )
        }
    }


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
                                    text = "Valor por cuota: ${uiState.credit.cuotaMensual.toLempira()}",
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


                                    ButtonComponent(
                                        text = "RESEÑAS",
                                        onClick = {showReviewsSheet = true  },
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
                                item { SeccionRealizarReclamo(
                                    onClick = { showClaimSheet = true }
                                ) }
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
                                if(claims.value.isNotEmpty())
                                {
                                    items(items = claims.value, key = { it.id }) { claim ->
                                        ReclamoArchivadoItem(
                                            claim    = claim,
                                            onClick = { selectedClaim = claim },
                                            onDelete = if (claim.estado.uppercase(getDefault()) != "REVISADO") {
                                                { viewModel.deleteClaim(claim.id) }
                                            } else null
                                        )
                                    }
                                }else{
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 32.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                "No hay reclamos registrados",
                                                fontSize = 14.sp,
                                                color = TextGray
                                            )
                                        }
                                    }

                                }
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
                Text(text = "CUOTA ${quota.numeroCuota}", fontSize = 12.sp, color = TextGray)
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(text = quota.monto.toLempira(), fontWeight = FontWeight.Bold, color = TextDark, fontSize = 16.sp)
        }
    }
}

@Composable
fun SeccionRealizarReclamo(
    onClick : () -> Unit = {}
) {
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
            onClick = { onClick() }
        )
    }
}

@Composable
fun ReclamoArchivadoItem(
    claim: ClaimModel,
    onDelete: (() -> Unit)? = null,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(
                enabled = true,
                onClick = { onClick() }
            ),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 0.5.dp,
            color = CardBorderColor.copy(alpha = 0.2f)
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = claim.asunto, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                Row() {
                    if (onDelete != null) {
                        Spacer(Modifier.width(8.dp))
                        IconButton(
                            onClick  = onDelete,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Eliminar reclamo",
                                tint = RpbRed,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Surface(
                        color = Color(0xFFF1F1F1),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = claim.estado.uppercase(getDefault()) , color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }

                }

            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                claim.descripcion,
                color = TextGray, fontSize = 14.sp,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.DateRange, contentDescription = null, tint = TextGray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(("Emision: " + claim.fechaEmision?.toFormattedDate()) , fontSize = 12.sp, color = TextGray)
                }
            }
        }
    }
}


@Composable
private fun EditCreditSheetContent(
    credit: CreditModel,
    plazos: List<PlazoModel>,
    isLoading: Boolean,
    onConfirm: (cantidad: Int, plazoMeses: Int) -> Unit
) {
    var quantity      by remember { mutableIntStateOf(credit.cantidad) }
    var selectedMonths by remember { mutableIntStateOf(credit.plazoMeses) }
    var menuExpanded  by remember { mutableStateOf(false) }
    var paymentMode   by remember { mutableStateOf(PaymentMode.INSTALLMENTS) }

    val precioUnitario = if (credit.cantidad > 0) credit.totalCredito / credit.cantidad else 0.0
    val totalPrice     = precioUnitario * quantity
    val monthlyPayment = if (selectedMonths > 0) totalPrice / selectedMonths else totalPrice

    PaymentBottomSheetContent(
        product               = null,
        paymentMode           = paymentMode,
        onPaymentModeChange   = { paymentMode = it },
        quantity              = quantity,
        onQuantityChange      = { quantity = it.coerceAtLeast(1) },
        plazos                = plazos,
        selectedMonths        = selectedMonths,
        onMonthsChange        = { selectedMonths = it },
        monthsMenuExpanded    = menuExpanded,
        onMonthsMenuExpandedChange = { menuExpanded = it },
        totalPrice            = totalPrice,
        monthlyPayment        = monthlyPayment,
        isLoading             = isLoading,
        isEditMode            = true,
        onConfirm             = { onConfirm(quantity, selectedMonths) }
    )
}

@Composable
private fun NewClaimSheetContent(
    isLoading: Boolean,
    onConfirm: (asunto: String, descripcion: String, imageUri: Uri) -> Unit,
    onCancel: () -> Unit
) {
    var asunto      by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var imageUri    by remember { mutableStateOf<Uri?>(null) }

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> imageUri = uri }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp)
    ) {
        Text("Subir un Reclamo", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
        Spacer(Modifier.height(4.dp))
        Text("Cuéntanos qué sucedió para ayudarte a resolverlo.", fontSize = 14.sp, color = TextGray)
        Spacer(Modifier.height(24.dp))

        // Asunto
        Text("ASUNTO", fontSize = 11.sp, fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp, color = TextGray)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value         = asunto,
            onValueChange = { asunto = it },
            placeholder   = { Text("Escribe el título de tu reclamo", color = TextGray) },
            singleLine    = true,
            modifier      = Modifier.fillMaxWidth(),
            shape         = RoundedCornerShape(12.dp),
            colors        = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedBorderColor   = AppPurple
            )
        )
        Spacer(Modifier.height(16.dp))

        Text("DESCRIPCIÓN", fontSize = 11.sp, fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp, color = TextGray)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value         = descripcion,
            onValueChange = { descripcion = it },
            placeholder   = { Text("Detalla los hechos de forma clara...", color = TextGray) },
            minLines      = 4,
            modifier      = Modifier.fillMaxWidth(),
            shape         = RoundedCornerShape(12.dp),
            colors        = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedBorderColor   = AppPurple
            )
        )
        Spacer(Modifier.height(16.dp))

        Text("SUBIR EVIDENCIA", fontSize = 11.sp, fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp, color = TextGray)
        Spacer(Modifier.height(8.dp))

        if (imageUri != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { imagePicker.launch("image/*") }
            ) {
                AsyncImage(
                    model          = imageUri,
                    contentDescription = "Evidencia",
                    contentScale   = ContentScale.Crop,
                    modifier       = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Toca para cambiar", color = Color.White,
                        fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                    .background(Color(0xFFF8F9FF))
                    .clickable { imagePicker.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.CloudUpload,
                        contentDescription = null,
                        tint = AppPurple,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("Arrastra tus archivos o haz clic aquí",
                        fontSize = 13.sp, color = TextGray)
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth().height(50.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AppPurple)
            }
        } else {
            ButtonComponent(
                text    = "ENVIAR RECLAMO",
                isEnabled = asunto.isNotBlank() && descripcion.isNotBlank() && imageUri != null,
                onClick = {
                    imageUri?.let { onConfirm(asunto, descripcion, it) }
                }
            )
        }

        Spacer(Modifier.height(12.dp))
        TextButton(
            onClick  = onCancel,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("CANCELAR", color = TextGray, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun ClaimDetailSheetContent(
    claim: ClaimModel,
    onClose: () -> Unit
) {
    val estadoColor = when (claim.estado.uppercase()) {
        "RESUELTO"   -> TextGreen
        "RECHAZADO"  -> RpbRed
        "EN PROCESO" -> AppPurple
        else         -> TextGray
    }
    val estadoIcon = when (claim.estado.uppercase()) {
        "RESUELTO"   -> Icons.Default.CheckCircle
        "RECHAZADO"  -> Icons.Default.Cancel
        "EN PROCESO" -> Icons.Default.Autorenew
        else         -> Icons.Default.HourglassEmpty
    }
    val estadoSubtitulo = when (claim.estado.uppercase()) {
        "REVISADO"   -> "Se ha realizado la inspección del reclamo."
        "RECHAZADO"  -> "El reclamo no pudo ser procesado."
        "EN REVISIÓN" -> "Tu reclamo está siendo revisado."
        else         -> "Tu reclamo está en espera de revisión."
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text(
                text = claim.asunto,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(end = 40.dp)
            )
            IconButton(
                onClick = onClose,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = TextGray)
            }
        }

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                "EMITIDO EL:",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = TextGray
            )
            Text(
                claim.fechaEmision?.toFormattedDate() ?: "—",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
        }

        Spacer(Modifier.height(20.dp))
        HorizontalDivider(color = Color(0xFFE0E0E0).copy(alpha = 0.5f))
        Spacer(Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {

            SectionLabel("ESTADO DEL RECLAMO")
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(estadoColor.copy(alpha = 0.08f))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Surface(
                    color = estadoColor,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.size(40.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = estadoIcon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Column {
                    Text(
                        claim.estado,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Text(
                        estadoSubtitulo,
                        fontSize = 12.sp,
                        color = TextGray,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            SectionLabel("MOTIVO/DESCRIPCIÓN")
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5))
                    .padding(14.dp)
                    .border(0.5.dp, Color(0xFFF5F5F5).copy(alpha = 0.2f))
            ,
            ) {
                Text(
                    claim.descripcion,
                    fontSize = 14.sp,
                    color = TextDark,
                    lineHeight = 20.sp
                )
            }

            if (!claim.respuesta.isNullOrBlank()) {
                Spacer(Modifier.height(20.dp))
                SectionLabel("RESPUESTA")
                Spacer(Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFEEF2FF))
                        .padding(14.dp)
                        .border(0.5.dp, Color(0xFFEEF2FF).copy(alpha = 0.2f))
                ) {
                    if (claim.fechaInicioRevision != null) {
                        Text(
                            claim.fechaInicioRevision.toFormattedDate(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextGray
                        )
                    }
                    claim.revisadoPor?.let {
                        Text(
                            "De: $it",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Light,
                            color = TextGray,
                            lineHeight = 20.sp
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        claim.respuesta,
                        fontSize = 14.sp,
                        color = TextDark,
                        lineHeight = 20.sp
                    )
                }
            }

            if (claim.evidenciaUrl.isNotBlank()) {
                Spacer(Modifier.height(20.dp))
                SectionLabel("EVIDENCIA ADJUNTADA")
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(claim.evidenciaUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Evidencia adjuntada",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .background(Color.Black.copy(alpha = 0.4f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AttachFile,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = claim.evidenciaUrl
                                    .substringAfterLast("%2F")
                                    .substringBefore("?")
                                    .uppercase(),
                                fontSize = 11.sp,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        color = TextGray
    )
}

