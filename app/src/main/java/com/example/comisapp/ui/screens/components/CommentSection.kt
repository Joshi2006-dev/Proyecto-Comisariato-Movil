package com.example.comisapp.ui.screens.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.comisapp.R
import com.example.comisapp.data.models.CommentModel
import com.example.comisapp.data.repository.ProductRepository

@Composable
fun ReviewsBottomSheetContent(
    productId: String,
    currentUserId: String,
    currentUserName: String,
    currentUserEmail: String,
    currentUserPhotoUrl: String,
    onClose: () -> Unit,
    canComment: Boolean = false,
    repository: ProductRepository = remember { ProductRepository() },
    vm: ReviewsViewModel = viewModel(
        key = "reviews_$productId",
        factory = ReviewsViewModel.Factory(productId, repository)
    )
) {


    val uiState by vm.uiState.collectAsState()
    val comments = vm.comments.collectAsLazyPagingItems()

    LaunchedEffect(uiState.sendSuccess) {
        if (uiState.sendSuccess) {
            comments.refresh()
            vm.clearSendSuccess()
        }
    }

    LaunchedEffect(comments.itemCount) {
        val loaded = (0 until comments.itemCount).mapNotNull { comments[it] }
        vm.updateRatingSummary(loaded)
    }

    ReviewsBottomSheetUi(
        productRating = uiState.averageRating,
        productReviewCount = uiState.totalComments,
        comments = comments,
        uiState = uiState,
        onReviewInputChange = vm::onReviewInputChange,
        onRatingChange = vm::onRatingChange,
        onSendReview = {
            vm.sendReview(
                userId = currentUserId,
                userName = currentUserName,
                userEmail = currentUserEmail,
                userPhotoUrl = currentUserPhotoUrl
            )
        },
        onClose = onClose,
        canComment = canComment
    )
}

@Composable
private fun ReviewsBottomSheetUi(
    productRating: Double,
    productReviewCount: Int,
    comments: LazyPagingItems<CommentModel>,
    uiState: ReviewsUiState,
    onReviewInputChange: (String) -> Unit,
    onRatingChange: (Int) -> Unit,
    onSendReview: () -> Unit,
    canComment: Boolean,
    onClose: () -> Unit
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.review))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.85f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Comentarios",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = BodyText,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        HorizontalDivider(color = DividerColor)

        RatingSummaryCard(rating = productRating, reviewCount = productReviewCount)

        if(comments.itemCount == 0){
            Column(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                LottieAnimation(
                    composition,
                    progress={progress},
                    modifier = Modifier
                        .size(200.dp)
                        .padding(bottom = 16.dp)
                        .alpha(0.5f),
                    contentScale = ContentScale.Fit,
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Aun no hay comentarios",
                    fontStyle = FontStyle.Normal,
                    fontWeight = FontWeight.Light,
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.offset( y= -(30.dp))
                )
            }
        }else{
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
            ) {
                items(
                    count = comments.itemCount,
                    key = comments.itemKey { it.id }
                ) { index ->
                    val comment = comments[index]
                    if (comment != null) {
                        ReviewItem(comment = comment)
                    }
                }
            }
        }



        AnimatedVisibility(
            visible = uiState.sendError != null,
            enter = fadeIn(), exit = fadeOut()
        ) {
            Text(
                text = uiState.sendError.orEmpty(),
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        HorizontalDivider(color = DividerColor)

        if(canComment){
            ReviewInputArea(
                reviewInput = uiState.reviewInput,
                selectedRating = uiState.selectedRating,
                isSending = uiState.isSending,
                onReviewInputChange = onReviewInputChange,
                onRatingChange = onRatingChange,
                onSendReview = onSendReview
            )
        }

    }
}


@Composable
private fun RatingSummaryCard(rating: Double, reviewCount: Int) {
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
            Column {
                Text(
                    "RESEÑAS",
                    fontSize = 10.sp, fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp, color = SubtleText
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        "$rating",
                        fontSize = 32.sp, fontWeight = FontWeight.Bold, color = BodyText
                    )
                    Text(
                        " / 5.0", fontSize = 16.sp, color = SubtleText,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    repeat(5) { i ->
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = if (i < rating.toInt()) BrandPurple else DividerColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    "Basado en $reviewCount opiniones",
                    fontSize = 12.sp, color = SubtleText
                )
            }
        }
    }
}

@Composable
private fun ReviewItem(comment: CommentModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = comment.fotoUrl.ifBlank { null },
                contentDescription = comment.usuario,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(SectionBg)
            )
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    comment.usuario,
                    fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = BodyText
                )
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    repeat(5) { i ->
                        Icon(
                            imageVector = if (i < comment.rate) Icons.Filled.Star else Icons.Outlined.StarOutline,
                            contentDescription = null,
                            tint = if (i < comment.rate) BrandPurple else DividerColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

        }
        Spacer(Modifier.height(8.dp))
        comment.fecha?.toDate()?.let { date ->
            val formatted = remember(date) {
                android.text.format.DateFormat.format("dd/MM/yyyy", date).toString()
            }
            Text(formatted, fontSize = 12.sp, color = SubtleText)
        }
        Text(comment.comentario, fontSize = 14.sp, color = BodyText, lineHeight = 20.sp)

    }
}

@Composable
private fun ReviewInputArea(
    reviewInput: String,
    selectedRating: Int,
    isSending: Boolean,
    onReviewInputChange: (String) -> Unit,
    onRatingChange: (Int) -> Unit,
    onSendReview: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .navigationBarsPadding()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Text("Califica tu experiencia: ", color = SubtleText, fontSize = 12.sp)
            repeat(5) { i ->
                val starIndex = i + 1
                Icon(
                    imageVector = if (starIndex <= selectedRating) Icons.Filled.Star else Icons.Outlined.StarOutline,
                    contentDescription = "Estrella $starIndex",
                    tint = if (starIndex <= selectedRating) BrandPurple else DividerColor,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onRatingChange(starIndex) }
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(50.dp))
                    .background(SectionBg)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                if (reviewInput.isEmpty()) {
                    Text("Añade una reseña", color = SubtleText, fontSize = 14.sp)
                }
                BasicTextField(
                    value = reviewInput,
                    onValueChange = onReviewInputChange,
                    textStyle = LocalTextStyle.current.copy(color = BodyText, fontSize = 14.sp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            val canSend = reviewInput.isNotBlank() && selectedRating > 0 && !isSending

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (canSend) Color(0xFF1C1C1E) else DividerColor)
                    .clickable(enabled = canSend) { onSendReview() },
                contentAlignment = Alignment.Center
            ) {
                if (isSending) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
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
}