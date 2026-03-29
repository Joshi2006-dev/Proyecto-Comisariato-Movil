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
import coil.compose.AsyncImage
import com.example.comisapp.ui.screens.components.ButtonComponent
import com.example.comisapp.ui.screens.components.FavoriteButton
import com.example.comisapp.ui.theme.TextPurple
import kotlinx.coroutines.launch

// ─── Color palette ────────────────────────────────────────────────────────────
private val BrandPurple  = TextPurple
private val SectionBg    = Color(0xFFF5F5F5)
private val InfoBlueBg   = Color(0xFFEEF2FF)
private val InfoBlueIcon = Color(0xFF6366F1)
private val DividerColor = Color(0xFFE0E0E0)
private val BodyText     = Color(0xFF212121)
private val SubtleText   = Color(0xFF757575)
private val StarYellow   = Color(0xFFFFC107)

// ─── Data models ──────────────────────────────────────────────────────────────
data class Specification(val label: String, val value: String)

data class Review(
    val authorName: String,
    val authorAvatarUrl: String,
    val rating: Int,       // 1-5
    val comment: String,
    val timeAgo: String    // "Hace 2 semanas"
)

data class ProductDetail(
    val brandName: String,
    val productName: String,
    val price: Double,
    val priceDisplay: String,
    val mainImageUrl: String,
    val thumbnailUrls: List<String>,
    val description: String,
    val specifications: List<Specification>,
    val guaranteeText: String,
    val coverageItems: List<String>,
    val exclusionItems: List<String>,
    val requirementItems: List<String>,
    val rating: Float,
    val reviewCount: Int,
    val reviews: List<Review> = emptyList(),
    val financingMonths: List<Int> = listOf(3, 6, 12, 18, 24)
)

// ─── Payment mode enum ────────────────────────────────────────────────────────
private enum class PaymentMode { SINGLE, INSTALLMENTS }

// ─── Screen ───────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    product: ProductDetail,
    onBack: () -> Unit = {}
) {
    var isFavorite by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    val galleryImages = remember(product) {
        (listOf(product.mainImageUrl) + product.thumbnailUrls).take(3)
    }
    var selectedImageUrl by remember(product) { mutableStateOf(galleryImages.first()) }

    // ── Payment sheet ──────────────────────────────────────────────────────────
    val paymentSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showPaymentSheet by remember { mutableStateOf(false) }
    var paymentMode by remember { mutableStateOf(PaymentMode.SINGLE) }
    var quantity by remember { mutableIntStateOf(1) }
    var selectedMonths by remember { mutableIntStateOf(product.financingMonths.firstOrNull() ?: 6) }
    var monthsMenuExpanded by remember { mutableStateOf(false) }
    val totalPrice = product.price * quantity
    val monthlyPayment = if (selectedMonths > 0) totalPrice / selectedMonths else totalPrice

    // ── Reviews sheet ──────────────────────────────────────────────────────────
    val reviewsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showReviewsSheet by remember { mutableStateOf(false) }
    var reviewInput by remember { mutableStateOf("") }

    // ── Payment bottom sheet ───────────────────────────────────────────────────
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
                onConfirm = {
                    coroutineScope.launch {
                        paymentSheetState.hide()
                        showPaymentSheet = false
                    }
                }
            )
        }
    }

    // ── Reviews bottom sheet ───────────────────────────────────────────────────
    if (showReviewsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showReviewsSheet = false },
            sheetState = reviewsSheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            dragHandle = null                      // we draw our own header with X
        ) {
            ReviewsBottomSheetContent(
                product = product,
                reviewInput = reviewInput,
                onReviewInputChange = { reviewInput = it },
                onClose = {
                    coroutineScope.launch {
                        reviewsSheetState.hide()
                        showReviewsSheet = false
                    }
                },
                onSendReview = {
                    // TODO: handle send
                    reviewInput = ""
                }
            )
        }
    }

    // ── Main scaffold ──────────────────────────────────────────────────────────
    Scaffold(
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

            // Main image
            AsyncImage(
                model = selectedImageUrl,
                contentDescription = product.productName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(Color(0xFFF0EDE8))
            )

            // Thumbnails
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

            // Brand / name / price
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                Text(
                    product.brandName.uppercase(),
                    color = BrandPurple, fontSize = 12.sp,
                    fontWeight = FontWeight.Bold, letterSpacing = 1.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(product.productName, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = BodyText)
                Spacer(Modifier.height(4.dp))
                Text(product.priceDisplay, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = BodyText)
            }

            Spacer(Modifier.height(16.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    ButtonComponent(
                        text = "SOLICITAR PRODUCTO",
                        onClick = { showPaymentSheet = true }
                    )
                }
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF1C1C1E)),
                    contentAlignment = Alignment.Center
                ) {
                    FavoriteButton(isFavorite = isFavorite, onToggle = { isFavorite = it })
                }
            }

            Spacer(Modifier.height(20.dp))
            HorizontalDivider(color = DividerColor)

            // Description
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text("Descripción", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BodyText)
                Spacer(Modifier.height(8.dp))
                Text(product.description, fontSize = 14.sp, color = SubtleText, lineHeight = 22.sp)
            }

            HorizontalDivider(color = DividerColor)
            Spacer(Modifier.height(8.dp))

            // Specifications
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SectionBg)
                    .padding(16.dp)
            ) {
                Text(
                    "ESPECIFICACIONES", fontSize = 12.sp,
                    fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = BodyText
                )
                Spacer(Modifier.height(12.dp))
                product.specifications.forEachIndexed { index, spec ->
                    if (index > 0) HorizontalDivider(color = DividerColor, modifier = Modifier.padding(vertical = 8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(spec.label, fontSize = 14.sp, color = SubtleText)
                        Text(spec.value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = BodyText)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = DividerColor)

            // Guarantee
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text(
                    "POLÍTICA DE GARANTÍA", fontSize = 12.sp,
                    fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = BodyText
                )
                Spacer(Modifier.height(10.dp))
                Text(product.guaranteeText, fontSize = 14.sp, color = SubtleText, lineHeight = 22.sp)
                Spacer(Modifier.height(10.dp))
                BulletSection("Cobertura:", product.coverageItems)
                Spacer(Modifier.height(6.dp))
                BulletSection("Exclusiones:", product.exclusionItems)
                Spacer(Modifier.height(6.dp))
                BulletSection("Requisito:", product.requirementItems)
                Spacer(Modifier.height(14.dp))
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

            // ── Reviews summary row (clickable → opens sheet) ──────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showReviewsSheet = true }     // ← opens reviews sheet
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Reseñas", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BodyText)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    repeat(5) { i ->
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = if (i < product.rating.toInt()) StarYellow else DividerColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(Modifier.width(4.dp))
                    Text(
                        "${product.rating} / 5.0 (${product.reviewCount})",
                        fontSize = 13.sp, color = SubtleText
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

// ─── Reviews Bottom Sheet ─────────────────────────────────────────────────────
@Composable
private fun ReviewsBottomSheetContent(
    product: ProductDetail,
    reviewInput: String,
    onReviewInputChange: (String) -> Unit,
    onClose: () -> Unit,
    onSendReview: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f)        // takes 85% of screen height
    ) {
        // ── Header ─────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            IconButton(onClick = onClose, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = BodyText)
            }
            Text(
                text = "Reseñas",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = BodyText,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        HorizontalDivider(color = DividerColor)

        // ── Summary card ────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(InfoBlueBg)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: label + big number
                Column {
                    Text(
                        "RESEÑAS", fontSize = 10.sp,
                        fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = SubtleText
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            "${product.rating}",
                            fontSize = 32.sp, fontWeight = FontWeight.Bold, color = BodyText
                        )
                        Text(
                            " / 5.0", fontSize = 16.sp, color = SubtleText,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }

                // Right: stars + count
                Column(horizontalAlignment = Alignment.End) {
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        repeat(5) { i ->
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = if (i < product.rating.toInt()) BrandPurple else DividerColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Basado en ${product.reviewCount} opiniones",
                        fontSize = 12.sp, color = SubtleText
                    )
                }
            }
        }

        // ── Review list ─────────────────────────────────────────────────────
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            items(product.reviews) { review ->
                ReviewItem(review = review)
                HorizontalDivider(color = DividerColor)
            }
        }

        // ── Add review input ────────────────────────────────────────────────
        HorizontalDivider(color = DividerColor)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(50.dp))
                    .background(SectionBg)
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                BasicTextFieldHint(
                    value = reviewInput,
                    onValueChange = onReviewInputChange,
                    hint = "Añade una reseña"
                )
            }

            // Send button
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1C1C1E))
                    .clickable(enabled = reviewInput.isNotBlank()) { onSendReview() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Enviar",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ─── Single review card ───────────────────────────────────────────────────────
@Composable
private fun ReviewItem(review: Review) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp)
    ) {
        // Author row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = review.authorAvatarUrl,
                contentDescription = review.authorName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(SectionBg)
            )
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    review.authorName,
                    fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = BodyText
                )
                // Stars
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    repeat(5) { i ->
                        Icon(
                            imageVector = if (i < review.rating) Icons.Filled.Star else Icons.Outlined.StarOutline,
                            contentDescription = null,
                            tint = if (i < review.rating) BrandPurple else DividerColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            Text(review.timeAgo, fontSize = 12.sp, color = SubtleText)
        }

        Spacer(Modifier.height(8.dp))
        Text(review.comment, fontSize = 14.sp, color = BodyText, lineHeight = 20.sp)
    }
}

// ─── Hint text field (no Material wrapper overhead) ───────────────────────────
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

// ─── Payment Bottom Sheet ─────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PaymentBottomSheetContent(
    product: ProductDetail,
    paymentMode: PaymentMode,
    onPaymentModeChange: (PaymentMode) -> Unit,
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    selectedMonths: Int,
    onMonthsChange: (Int) -> Unit,
    monthsMenuExpanded: Boolean,
    onMonthsMenuExpandedChange: (Boolean) -> Unit,
    totalPrice: Double,
    monthlyPayment: Double,
    onConfirm: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Método de Pago", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = BodyText)
        Spacer(Modifier.height(20.dp))

        PaymentModeToggle(selected = paymentMode, onSelect = onPaymentModeChange)
        Spacer(Modifier.height(20.dp))

        if (paymentMode == PaymentMode.INSTALLMENTS) {
            Text(
                "PLAZO DE FINANCIACIÓN", fontSize = 11.sp,
                fontWeight = FontWeight.Bold, letterSpacing = 1.sp,
                color = SubtleText, modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, DividerColor, RoundedCornerShape(10.dp))
                    .clickable { onMonthsMenuExpandedChange(true) }
                    .padding(horizontal = 14.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("$selectedMonths meses", fontSize = 14.sp, color = BodyText)
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = SubtleText)
                }
                DropdownMenu(
                    expanded = monthsMenuExpanded,
                    onDismissRequest = { onMonthsMenuExpandedChange(false) }
                ) {
                    product.financingMonths.forEach { months ->
                        DropdownMenuItem(
                            text = { Text("$months meses") },
                            onClick = { onMonthsChange(months); onMonthsMenuExpandedChange(false) }
                        )
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        Text(
            "CANTIDAD", fontSize = 11.sp, fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp, color = SubtleText, modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF1C1C1E))
                    .clickable { onQuantityChange(quantity - 1) },
                contentAlignment = Alignment.Center
            ) {
                Text("−", fontSize = 22.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }

            Text(
                quantity.toString(), fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold, color = BodyText,
                modifier = Modifier.weight(1f), textAlign = TextAlign.Center
            )

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF1C1C1E))
                    .clickable { onQuantityChange(quantity + 1) },
                contentAlignment = Alignment.Center
            ) {
                Text("+", fontSize = 22.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(SectionBg)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    if (paymentMode == PaymentMode.INSTALLMENTS) "Deducción Mensual" else "Total a pagar",
                    fontSize = 13.sp, color = SubtleText
                )
                Spacer(Modifier.height(4.dp))
                val amount = if (paymentMode == PaymentMode.INSTALLMENTS) monthlyPayment else totalPrice
                val amountStr = "L%.2f".format(amount).replace(".", ",")
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold, color = BodyText)) {
                            append(amountStr)
                        }
                        append("  ")
                        withStyle(SpanStyle(fontSize = 13.sp, color = SubtleText, fontWeight = FontWeight.Normal)) {
                            append("LPS")
                        }
                    }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(InfoBlueBg)
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                Icons.Default.Info, contentDescription = null,
                tint = InfoBlueIcon, modifier = Modifier.size(18.dp).padding(top = 1.dp)
            )
            Text(
                text = if (paymentMode == PaymentMode.INSTALLMENTS)
                    "Durante los próximos $selectedMonths meses se deducirá el valor de la cuota del pago. " +
                            "La solicitud será procesada en 7 días hábiles. Por favor, esté atento a cualquier situación."
                else
                    "El valor del producto será deducido de su próximo pago. La solicitud será procesada " +
                            "en 7 días hábiles. Por favor, esté atento a cualquier situación.",
                fontSize = 12.sp, color = Color(0xFF3730A3), lineHeight = 18.sp
            )
        }

        Spacer(Modifier.height(24.dp))
        ButtonComponent(text = "CONFIRMAR SOLICITUD", onClick = onConfirm)
    }
}

// ─── Payment mode toggle ──────────────────────────────────────────────────────
@Composable
private fun PaymentModeToggle(selected: PaymentMode, onSelect: (PaymentMode) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50.dp))
            .background(SectionBg)
            .padding(4.dp)
    ) {
        Row {
            PaymentModeTab("Pago Único", selected == PaymentMode.SINGLE, Modifier.weight(1f)) {
                onSelect(PaymentMode.SINGLE)
            }
            PaymentModeTab("Cuotas", selected == PaymentMode.INSTALLMENTS, Modifier.weight(1f)) {
                onSelect(PaymentMode.INSTALLMENTS)
            }
        }
    }
}

@Composable
private fun PaymentModeTab(
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else Color.Transparent,
        animationSpec = tween(200), label = "tab_bg"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) BrandPurple else SubtleText,
        animationSpec = tween(200), label = "tab_text"
    )
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label, fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = textColor
        )
    }
}

// ─── Helpers ──────────────────────────────────────────────────────────────────
@Composable
private fun BulletSection(title: String, items: List<String>) {
    Column {
        items.forEach { item ->
            Row(modifier = Modifier.padding(vertical = 2.dp)) {
                Text("• ", fontSize = 14.sp, color = SubtleText)
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(title) }
                        append(" $item")
                    },
                    fontSize = 14.sp, color = SubtleText, lineHeight = 22.sp
                )
            }
        }
    }
}

// ─── Sample data ──────────────────────────────────────────────────────────────
val sampleReviews = listOf(
    Review(
        authorName = "Marco Aurelio",
        authorAvatarUrl = "https://i.pravatar.cc/150?img=11",
        rating = 5,
        comment = "Esta muy bonita la cartera, es de buena calidad y esta nice.",
        timeAgo = "Hace 2 semanas"
    ),
    Review(
        authorName = "Marco Aurelio",
        authorAvatarUrl = "https://i.pravatar.cc/150?img=12",
        rating = 4,
        comment = "Esta muy bonita la cartera, es de buena calidad y esta nice.",
        timeAgo = "Hace 2 semanas"
    ),
    Review(
        authorName = "Marco Aurelio",
        authorAvatarUrl = "https://i.pravatar.cc/150?img=13",
        rating = 5,
        comment = "Esta muy bonita la cartera, es de buena calidad y esta nice.",
        timeAgo = "Hace 2 semanas"
    )
)

val sampleProduct = ProductDetail(
    brandName = "Studio Atelier",
    productName = "Essential Leather Tote",
    price = 5697.00,
    priceDisplay = "5,697.00 L.",
    mainImageUrl = "https://images.unsplash.com/photo-1548036328-c9fa89d128fa?w=800",
    thumbnailUrls = listOf(
        "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=800",
        "https://images.unsplash.com/photo-1584917865442-de89df76afd3?w=800"
    ),
    description = "La Lámpara Architex no es solo un objeto funcional, es una declaración de principios. " +
            "Inspirada en la arquitectura brutalista y el minimalismo contemporáneo, su estructura de " +
            "líneas puras elimina todo lo innecesario para centrarse en la esencia de la luz.\n\n" +
            "Cada unión ha sido mecanizada con precisión milimétrica, permitiendo un movimiento fluido " +
            "del brazo articulado. El cabezal cuenta con tecnología LED de espectro completo, diseñada " +
            "para reducir la fatiga ocular mientras mantiene una reproducción cromática superior al 95 CRI.",
    specifications = listOf(
        Specification("Altura", "145 cm"),
        Specification("Peso", "4.2 kg"),
        Specification("Lúmenes", "800 lm")
    ),
    guaranteeText = "Comisariato Cucayagua garantiza la calidad de sus productos por un período de 30 " +
            "días contra defectos de fábrica.",
    coverageItems = listOf("Fallas técnicas y materiales bajo uso normal."),
    exclusionItems = listOf(
        "Daños por maltrato, accidentes, humedad, variaciones de voltaje o modificaciones no autorizadas."
    ),
    requirementItems = listOf("Conservar el comprobante de compra digital o físico."),
    rating = 4.8f,
    reviewCount = 124,
    reviews = sampleReviews,
    financingMonths = listOf(3, 6, 12, 18, 24)
)