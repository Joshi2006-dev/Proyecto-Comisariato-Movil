package com.example.comisapp.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun GradientBackgroundComponent(){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .blur(radius = 45.dp)) {
        Box(
            modifier = Modifier
                .size(circle1Diameter)
                .offset(x = -circle1RadiusDp, y = 0.dp)
                .align(Alignment.CenterEnd)
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
                    color = Color(0xFF64748B).copy(alpha = 0.2f),
                    shape = androidx.compose.foundation.shape.CircleShape
                )
        )
    }

}