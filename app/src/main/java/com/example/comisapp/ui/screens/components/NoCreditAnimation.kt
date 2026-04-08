package com.example.comisapp.ui.screens.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.comisapp.R

@Composable
fun NoCreditAnimation(
    modifier: Modifier = Modifier
){
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.empty))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = 1
    )


    Column(
        modifier = modifier.fillMaxWidth().fillMaxHeight().offset( y= -(30.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LottieAnimation(
            composition,
            progress={progress},
            modifier = Modifier
                .size(400.dp)
                .padding(bottom = 16.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "No se encontraron creditos",
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.Light,
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.offset( y= -(10.dp))
        )
    }
}