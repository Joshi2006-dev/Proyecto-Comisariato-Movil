package com.example.comisapp.ui.screens.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.animation.core.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun AnimatedText(
    text: String,
    isBold: Boolean,
    visible: Boolean,
    fontSize: Int = 38,
    delay: Int
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { 40 },
            animationSpec = tween(durationMillis = 700, delayMillis = delay)
        ) + fadeIn(
            animationSpec = tween(durationMillis = 700, delayMillis = delay)
        )
    ) {
        Text(
            text = text,
            fontSize = fontSize.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            letterSpacing = if (isBold) (-1.8).sp else 0.sp,
            color = Color.Black
        )
    }


}

@Composable
fun FloatingComponent(
    modifier: Modifier = Modifier,
    durationMillis: Int = 2000,
    offsetY: Dp = 15.dp,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    val density = LocalDensity.current

    val offsetPx = with(density) { offsetY.toPx() }

    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = offsetPx,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offsetY"
    )

    Box(
        modifier = modifier.offset { IntOffset(x = 0, y = animatedOffset.roundToInt()) },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}