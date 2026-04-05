package com.example.comisapp.ui.screens

import LempiraHelper.toLempira
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.comisapp.data.models.ProductModel
import com.example.comisapp.data.models.settings.PlazoModel
import com.example.comisapp.ui.screens.ViewModels.CreditRequestState
import com.example.comisapp.ui.screens.ViewModels.ProductDetailState
import com.example.comisapp.ui.screens.ViewModels.ProductDetailViewModel
import com.example.comisapp.ui.screens.components.ButtonComponent
import com.example.comisapp.ui.screens.components.FavoriteButton
import com.example.comisapp.ui.screens.components.PaymentBottomSheetContent
import com.example.comisapp.ui.theme.TextPurple
import com.example.comisapp.ui.screens.components.PaymentMode
import com.example.comisapp.ui.screens.components.ReviewsBottomSheetContent
import kotlinx.coroutines.launch
import kotlin.collections.first

// ─── Color palette ────────────────────────────────────────────────────────────
private val BrandPurple  = TextPurple
private val SectionBg    = Color(0xFFF5F5F5)
private val InfoBlueBg   = Color(0xFFEEF2FF)
private val InfoBlueIcon = Color(0xFF6366F1)
private val DividerColor = Color(0xFFE0E0E0)
private val BodyText     = Color(0xFF212121)
private val SubtleText   = Color(0xFF757575)
private val StarYellow   = Color(0xFFFFC107)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    viewModel: ProductDetailViewModel,
    onBack: () -> Unit = {},
    onNavigateToCredits:() -> Unit = {},
) {
    val state = viewModel.uiState

    when(state){
        is  ProductDetailState.Loading-> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is ProductDetailState.Error -> {
            Text(state.message)
        }
        is ProductDetailState.Success -> {
            MainContent(product = state.product, viewModel, onBack, onNavigateToCredits)
        }
    }

}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainContent(
   product: ProductModel,
   viewModel: ProductDetailViewModel,
   onBack: () -> Unit = {},
   onNavigateToCredits: () -> Unit = {}
){
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val plazos = viewModel.plazos
    val garantia = viewModel.garantia

    val galleryImages = remember(product){
        listOfNotNull(
            product.imagenFrontalUrl?.takeIf { it.isNotBlank() },
            product.imagenLateralUrl?.takeIf { it.isNotBlank() },
            product.imagenTraseraUrl?.takeIf { it.isNotBlank() }
        )

    }

    var selectedImageUrl by remember(product) {
        mutableStateOf(galleryImages.firstOrNull().orEmpty())
    }

    val paymentSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showPaymentSheet by remember { mutableStateOf(false) }

    var showReviewsSheet by remember { mutableStateOf(false) }
    val reviewsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var paymentMode by remember { mutableStateOf(PaymentMode.SINGLE) }
    var quantity by remember { mutableIntStateOf(1) }
    var selectedMonths by remember {
        mutableIntStateOf(0)
    }


    LaunchedEffect(plazos) {
        if(plazos.isNotEmpty() && selectedMonths == 0){
            selectedMonths = plazos.minByOrNull { it.plazoMeses }!!.plazoMeses
        }
    }
    var monthsMenuExpanded by remember { mutableStateOf(false) }

    val precio = if (paymentMode== PaymentMode.SINGLE){
        product.precioContado?:0.0
    }else{
        product.precioCredito?:0.0
    }
    val totalPrice = precio * quantity
    val monthlyPayment = if (paymentMode== PaymentMode.SINGLE){
        totalPrice
    }else{
        totalPrice / selectedMonths
    }

    val creditState = viewModel.creditRequestState
    LaunchedEffect(creditState) {
        when (creditState) {
            is CreditRequestState.Success -> {
                paymentSheetState.hide()
                showPaymentSheet = false
                val result = snackbarHostState.showSnackbar(
                    message ="Solicitud enviada con éxito",
                    actionLabel = "Ver créditos",
                    duration = SnackbarDuration.Long
                )
                if (result === SnackbarResult.ActionPerformed) {
                    onNavigateToCredits()
                }
                viewModel.resetCreditState()
            }
            is CreditRequestState.Error -> {
                snackbarHostState.showSnackbar(creditState.message)
                viewModel.resetCreditState()
            }
            else -> Unit
        }
    }

    if (showReviewsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showReviewsSheet = false },
            sheetState = reviewsSheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            ReviewsBottomSheetContent(
                productId = product.id,
                currentUserId = viewModel.currentUser?.empleadoId.orEmpty(),
                currentUserName = viewModel.currentUser?.nombre.orEmpty(),
                currentUserEmail = viewModel.currentUser?.correo.orEmpty(),
                currentUserPhotoUrl = viewModel.currentUser?.fotoUrl.orEmpty(),
                onClose = { showReviewsSheet = false }
            )
        }
    }

        if (showPaymentSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPaymentSheet = false },
            sheetState = paymentSheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            PaymentBottomSheetContent(
                product = product,
                paymentMode = paymentMode,
                onPaymentModeChange = { paymentMode = it },
                quantity = quantity,
                onQuantityChange = { quantity = it.coerceAtLeast(1) },
                selectedMonths = selectedMonths,
                onMonthsChange = { selectedMonths = it },
                monthsMenuExpanded = monthsMenuExpanded,
                onMonthsMenuExpandedChange = { monthsMenuExpanded = it },
                totalPrice = totalPrice,
                monthlyPayment = monthlyPayment,
                isLoading = creditState is CreditRequestState.Loading,
                plazos = plazos,
                onConfirm = {
                    viewModel.requestCredit(
                        product=product,
                        quantity = quantity,
                        plazoMeses = if (paymentMode == PaymentMode.SINGLE) 1 else selectedMonths,
                        isSingle = paymentMode == PaymentMode.SINGLE
                        )
                }
            )
        }



    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Detalle del producto",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = BodyText,
                    navigationIconContentColor = BodyText
                )
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {

            AsyncImage(
                model = selectedImageUrl,
                contentDescription = product.nombre,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(Color(0xFFF0EDE8))
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                galleryImages.forEach { url ->
                    val isSelected = url == selectedImageUrl
                    val borderColor by animateColorAsState(
                        targetValue = if (isSelected) BrandPurple else DividerColor,
                        animationSpec = tween(200), label = "thumb_border"
                    )
                    AsyncImage(
                        model = url,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .border(2.dp, borderColor, RoundedCornerShape(8.dp))
                            .background(Color(0xFFF0EDE8))
                            .clickable { selectedImageUrl = url }
                    )
                }
            }


            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)) {
                Text(
                    product.categoria?:"".uppercase(),
                    color = BrandPurple, fontSize = 12.sp,
                    fontWeight = FontWeight.Bold, letterSpacing = 1.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(product.nombre?:"", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = BodyText)
                Spacer(Modifier.height(4.dp))
                Text(product.precioContado?.toLempira()?:"", fontSize = 18.sp, fontWeight = FontWeight.Medium, color = BodyText)
            }

            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    ButtonComponent(
                        text = "SOLICITAR PRODUCTO",
                        onClick = {
                            showPaymentSheet = true
                        }
                    )
                }
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    FavoriteButton(
                        isFavorite = viewModel.isFavorite,
                        onToggle = { viewModel.toggleFavorite()}
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
            HorizontalDivider(color = DividerColor.copy(alpha = 0.3f))
            Column(modifier = Modifier.fillMaxWidth()) {
                val rating = product.rate ?: 0.0
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showReviewsSheet = true }
                        .padding(horizontal = 16.dp, vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Text(
                            text = "Reseñas",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = BodyText
                        )
                    }
                    if(rating != 0.0){
                        Row(verticalAlignment = Alignment.CenterVertically) {

                            Row(modifier = Modifier.padding(end = 8.dp)) {
                                repeat(5) { index ->
                                    Icon(
                                        imageVector = if (index < rating.toInt()) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                        contentDescription = null,
                                        tint = if (index < rating.toInt()) TextPurple else DividerColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Text(
                                text = rating.toString(),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = BodyText
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = SubtleText,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }else{
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "No hay reseñas para este producto",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                fontStyle = FontStyle.Italic,
                                color = Color.Gray
                            )
                        }
                    }


                }
                HorizontalDivider(color = DividerColor.copy(alpha = 0.3f))
            }

            // Description
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)) {
                Text("Descripción", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BodyText)
                Spacer(Modifier.height(8.dp))
                Text(product.descripcion?:"", fontSize = 14.sp, color = SubtleText, lineHeight = 22.sp)
            }

            HorizontalDivider(color = DividerColor.copy(alpha = 0.3f))
            Spacer(Modifier.height(8.dp))

            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)) {
                Text(
                    "POLÍTICA DE GARANTÍA", fontSize = 12.sp,
                    fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = BodyText
                )
                Spacer(Modifier.height(10.dp))
                Text("CrediFlow garantiza la calidad de sus " +
                        "productos por un periodo de " +
                        if(product.garantiaMeses != null) " ${product.garantiaMeses} mes(es) " else "${product.garantiaDias} días "+
                        "contra defectos de fábrica",
                    fontSize = 14.sp, color = SubtleText, lineHeight = 22.sp)
                Spacer(Modifier.height(10.dp))

                if(garantia?.cobertura?.isNotBlank() == true){
                    BulletSection("Cobertura:", garantia.cobertura)
                    Spacer(Modifier.height(6.dp))
                }
                if(garantia?.exclusiones?.isNotBlank() == true){
                    BulletSection("Exclusiones:", garantia.exclusiones)
                    Spacer(Modifier.height(6.dp))
                }
                if(garantia?.requisito?.isNotBlank() == true){
                    BulletSection("Requisito:", garantia.requisito)
                    Spacer(Modifier.height(6.dp))
                }
                Text("¿Cómo solicitar tu garantía?", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = BodyText)
                Spacer(Modifier.height(6.dp))
                Text(
                    "Para agilizar tu proceso, ingresa a nuestra App y dirígete al apartado de " +
                            "\"Garantías o Reclamos\" que se encuentra en el detalle de tu crédito. " +
                            "Desde allí podrás enviar tu solicitud, adjuntar fotos de la falla y " +
                            "recibir una respuesta técnica en un plazo de 7 días hábiles.",
                    fontSize = 14.sp, color = SubtleText, lineHeight = 22.sp
                )
            }

            Spacer(Modifier.height(8.dp))
        }
    }


}
@Composable
private fun BulletSection(title: String, text: String) {
    Column {
            Row(modifier = Modifier.padding(vertical = 2.dp)) {
                Text("• ", fontSize = 14.sp, color = SubtleText)
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(title) }
                        append(" $text")
                    },
                    fontSize = 14.sp, color = SubtleText, lineHeight = 22.sp
                )
            }
    }
}
