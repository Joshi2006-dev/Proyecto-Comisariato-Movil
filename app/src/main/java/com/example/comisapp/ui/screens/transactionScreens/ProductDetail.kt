package com.example.comisapp.ui.screens

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
                Text(product.precioContado.toString(), fontSize = 18.sp, fontWeight = FontWeight.Medium, color = BodyText)
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
            HorizontalDivider(color = DividerColor)

            // Description
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)) {
                Text("Descripción", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BodyText)
                Spacer(Modifier.height(8.dp))
                Text(product.descripcion?:"", fontSize = 14.sp, color = SubtleText, lineHeight = 22.sp)
            }

            HorizontalDivider(color = DividerColor)
            Spacer(Modifier.height(8.dp))

            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)) {
                Text(
                    "POLÍTICA DE GARANTÍA", fontSize = 12.sp,
                    fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = BodyText
                )
                Spacer(Modifier.height(10.dp))
                Text("Comisariato Cucayagua garantiza la calidad de sus " +
                        "productos por un periodo de ${product.garantiaMeses} mes(es) " +
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

            HorizontalDivider(color = DividerColor)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
//                        showReviewsSheet = true
                    }     // ← opens reviews sheet
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Reseñas", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BodyText)
            }

            Spacer(Modifier.height(8.dp))
        }
    }


}
//
//@Composable
//private fun ReviewsBottomSheetContent(
//    product: ProductDetail,
//    reviewInput: String,
//    onReviewInputChange: (String) -> Unit,
//    onClose: () -> Unit,
//    onSendReview: () -> Unit
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .fillMaxHeight(0.85f)
//    ) {
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp, vertical = 14.dp)
//        ) {
//            IconButton(onClick = onClose, modifier = Modifier.align(Alignment.CenterStart)) {
//                Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = BodyText)
//            }
//            Text(
//                text = "Reseñas",
//                fontSize = 18.sp,
//                fontWeight = FontWeight.Bold,
//                color = BodyText,
//                modifier = Modifier.align(Alignment.Center)
//            )
//        }
//
//        HorizontalDivider(color = DividerColor)
//
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp, vertical = 14.dp)
//                .clip(RoundedCornerShape(14.dp))
//                .background(InfoBlueBg)
//                .padding(horizontal = 16.dp, vertical = 14.dp)
//        ) {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.SpaceBetween
//            ) {
//                Column {
//                    Text(
//                        "RESEÑAS", fontSize = 10.sp,
//                        fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = SubtleText
//                    )
//                    Spacer(Modifier.height(4.dp))
//                    Row(verticalAlignment = Alignment.Bottom) {
//                        Text(
//                            "${product.rating}",
//                            fontSize = 32.sp, fontWeight = FontWeight.Bold, color = BodyText
//                        )
//                        Text(
//                            " / 5.0", fontSize = 16.sp, color = SubtleText,
//                            modifier = Modifier.padding(bottom = 4.dp)
//                        )
//                    }
//                }
//
//                // Right: stars + count
//                Column(horizontalAlignment = Alignment.End) {
//                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
//                        repeat(5) { i ->
//                            Icon(
//                                imageVector = Icons.Filled.Star,
//                                contentDescription = null,
//                                tint = if (i < product.rating.toInt()) BrandPurple else DividerColor,
//                                modifier = Modifier.size(20.dp)
//                            )
//                        }
//                    }
//                    Spacer(Modifier.height(4.dp))
//                    Text(
//                        "Basado en ${product.reviewCount} opiniones",
//                        fontSize = 12.sp, color = SubtleText
//                    )
//                }
//            }
//        }
//
//        // ── Review list ─────────────────────────────────────────────────────
//        LazyColumn(
//            modifier = Modifier
//                .weight(1f)
//                .fillMaxWidth(),
//            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
//            verticalArrangement = Arrangement.spacedBy(0.dp)
//        ) {
//            items(product.reviews) { review ->
//                ReviewItem(review = review)
//                HorizontalDivider(color = DividerColor)
//            }
//        }
//
//        // ── Add review input ────────────────────────────────────────────────
//        HorizontalDivider(color = DividerColor)
//
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp, vertical = 12.dp)
//                .navigationBarsPadding(),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.spacedBy(10.dp)
//        ) {
//            Box(
//                modifier = Modifier
//                    .weight(1f)
//                    .clip(RoundedCornerShape(50.dp))
//                    .background(SectionBg)
//                    .padding(horizontal = 16.dp, vertical = 4.dp)
//            ) {
//                BasicTextFieldHint(
//                    value = reviewInput,
//                    onValueChange = onReviewInputChange,
//                    hint = "Añade una reseña"
//                )
//            }
//
//            // Send button
//            Box(
//                modifier = Modifier
//                    .size(44.dp)
//                    .clip(CircleShape)
//                    .background(Color(0xFF1C1C1E))
//                    .clickable(enabled = reviewInput.isNotBlank()) { onSendReview() },
//                contentAlignment = Alignment.Center
//            ) {
//                Icon(
//                    imageVector = Icons.AutoMirrored.Filled.Send,
//                    contentDescription = "Enviar",
//                    tint = Color.White,
//                    modifier = Modifier.size(20.dp)
//                )
//            }
//        }
//    }
//}

//@Composable
//private fun ReviewItem(review: Review) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 14.dp)
//    ) {
//        // Author row
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            AsyncImage(
//                model = review.authorAvatarUrl,
//                contentDescription = review.authorName,
//                contentScale = ContentScale.Crop,
//                modifier = Modifier
//                    .size(44.dp)
//                    .clip(CircleShape)
//                    .background(SectionBg)
//            )
//            Spacer(Modifier.width(10.dp))
//            Column(modifier = Modifier.weight(1f)) {
//                Text(
//                    review.authorName,
//                    fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = BodyText
//                )
//                // Stars
//                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
//                    repeat(5) { i ->
//                        Icon(
//                            imageVector = if (i < review.rating) Icons.Filled.Star else Icons.Outlined.StarOutline,
//                            contentDescription = null,
//                            tint = if (i < review.rating) BrandPurple else DividerColor,
//                            modifier = Modifier.size(16.dp)
//                        )
//                    }
//                }
//            }
//            Text(review.timeAgo, fontSize = 12.sp, color = SubtleText)
//        }
//
//        Spacer(Modifier.height(8.dp))
//        Text(review.comment, fontSize = 14.sp, color = BodyText, lineHeight = 20.sp)
//    }
//}

@Composable
private fun BasicTextFieldHint(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String
) {
    Box(contentAlignment = Alignment.CenterStart) {
        if (value.isEmpty()) {
            Text(hint, fontSize = 14.sp, color = SubtleText)
        }
        androidx.compose.foundation.text.BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = androidx.compose.ui.text.TextStyle(
                fontSize = 14.sp,
                color = BodyText
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
        )
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
