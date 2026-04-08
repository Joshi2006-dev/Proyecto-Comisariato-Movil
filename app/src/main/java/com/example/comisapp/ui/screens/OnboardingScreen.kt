package com.example.comisapp.ui.screens


import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.reverseDifference
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.comisapp.ui.theme.CardBorderColor
import com.example.comisapp.R
import com.example.comisapp.ui.screens.components.AnimatedText
import com.example.comisapp.ui.screens.components.ButtonComponent
import com.example.comisapp.ui.screens.components.CardWithShadowAndImage
import com.example.comisapp.ui.screens.components.FloatingComponent
import com.example.comisapp.ui.screens.components.GradientBackgroundComponent

import com.example.comisapp.ui.screens.components.circle1Diameter
import com.example.comisapp.ui.screens.components.circle1RadiusDp
import com.example.comisapp.ui.screens.components.circle2Diameter
import com.example.comisapp.ui.screens.components.circle2RadiusDp
import com.example.comisapp.ui.theme.TextDark
import com.example.comisapp.ui.theme.TextPurple
import kotlinx.coroutines.launch


fun Modifier.glassBorder(): Modifier =
    this.border(
        width = 1.dp,
        color = CardBorderColor.copy(alpha = 0.20f),
        shape = RoundedCornerShape(20.dp)
    )

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit = {}
) {
    val pageCount = 3
    val pagerState = rememberPagerState(pageCount = { pageCount })
    val scope= rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        GradientBackgroundComponent()
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
        ) { page ->
            ContenidoDePagina(page,
                onFinish = onFinish,
                onNextClick = {
                    scope.launch {
                        val nextPage = pagerState.currentPage + 1
                        if (nextPage < pageCount) {
                            pagerState.animateScrollToPage(nextPage)
                        }
                    }
                })
        }

        Row(
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 35.dp)
                .height(50.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(pageCount) { iteration ->
                val isSelected = remember {
                    derivedStateOf { pagerState.currentPage == iteration }
                }.value


                val width by animateDpAsState(
                    targetValue = if (isSelected) 24.dp else 8.dp,
                    label = "width"
                )

                val color by animateColorAsState(
                    targetValue = if (pagerState.currentPage == iteration)
                        TextPurple
                    else
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    label = "color"
                )

                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(width = width, height = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ContenidoDePagina(index: Int, onNextClick: () -> Unit = {}, onFinish: () -> Unit) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (index) {
            0 -> {
                FirstScreen(
                    onNextClick
                )
            }
            1 -> {
                SecondScreen(
                    onNextClick
                )
            }
            2 -> {
                ThirdScreen(
                    onFinish
                )
                }
            }
        }
    }




@Composable
private fun FirstScreen(
    onNextClick: () -> Unit
){
    val gradientBrush = remember {Brush.linearGradient(
        colors = listOf(
            Color(0xFFE2DFFF),
            Color(0xFF7C3AED),
            Color(0xFF2B066B),

        ),
        start = Offset(0f, -0.4f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )}

    var visibilidad by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = Unit) {
        visibilidad = true
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ){
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(2.dp)
                ).padding(top = 220.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 150.dp).offset( x = 150.dp )
                ,
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    painter =  painterResource(id = R.drawable.ic_shop_onboarding_bg),
                    contentDescription = "Logo",
                    modifier = Modifier.size(520.dp)
                        .rotate(25f)
                        .graphicsLayer(alpha = 0.25f)
                        .drawWithContent{
                            drawContent()
                            drawRect(
                                brush = gradientBrush,
                                blendMode = BlendMode.SrcIn
                            )
                        },
                    tint = Color.Unspecified,
                )
            }

        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .padding(top = 170.dp, bottom = 70.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            AnimatedText(text = "Gestiona", isBold = true, visible = visibilidad, delay = 50)
            AnimatedText(text = "Tus Compras", isBold = false, visible = visibilidad, delay = 200)
            AnimatedText(text = "A Tu", isBold = true, visible = visibilidad, delay = 350)
            AnimatedText(text = "Manera", isBold = false, visible = visibilidad, delay = 500)
            Spacer(modifier = Modifier.weight(1f))
            ButtonComponent(text = "SIGUIENTE", onClick =  onNextClick)

        }

    }
}


@Composable
private fun SecondScreen(
    onNextClick: () -> Unit
){
    var visibilidad by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = Unit) {
        visibilidad = true
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
    ){
        Spacer(Modifier.weight(1f))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(60.dp),
            contentAlignment = Alignment.TopCenter
        ){
            FloatingComponent(durationMillis = 2600, offsetY = 12.dp) {
                CardWithShadowAndImage(
                    imageRes = R.drawable.vehiculo_card_onb,
                    rotationDegrees = 0f,
                    modifier = Modifier.size(height = 260.dp, width = 200.dp)
                        .align(Alignment.TopCenter)
                        .offset(y = 20.dp )
                )
            }

            FloatingComponent(durationMillis = 2900, offsetY = 8.dp) {
                CardWithShadowAndImage(
                    imageRes = R.drawable.hogar_card_onb,
                    rotationDegrees = 18f,
                    modifier = Modifier.size(height = 260.dp, width = 200.dp)
                        .align(Alignment.TopCenter)
                        .offset(y = 210.dp, x = 50.dp )
                )
            }

            FloatingComponent(durationMillis = 2300, offsetY = 10.dp) {
                CardWithShadowAndImage(
                    imageRes = R.drawable.electronica_card_onb,
                    rotationDegrees = -15f,
                    modifier = Modifier.size(height = 260.dp, width = 200.dp)
                        .align(Alignment.TopCenter)
                        .offset(y = 135.dp, x = -40.dp )
                )
            }

        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 40.dp)
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedText(text = "Descubre ", isBold = true, visible = visibilidad, delay = 50, fontSize = 24)
                AnimatedText(text = "Nuevas Opciones", isBold = false, visible = visibilidad, delay = 200, fontSize = 24)

            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedText(text = "En ", isBold = false, visible = visibilidad, delay = 350, fontSize = 24)
                AnimatedText(text = "Tu Comisariato", isBold = true, visible = visibilidad, delay = 400, fontSize = 24)
            }


            Text(
                text = "Todo tipo de productos que desees disponibles al alcance de un solo clic.",
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = -0.8.sp,
                lineHeight = 20.sp,
                color = Color.Black.copy(alpha = 0.5f),
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 15.dp)
            )

            ButtonComponent(text = "SIGUIENTE", onClick = onNextClick)

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ThirdScreen(
    onFinish: () -> Unit
){
    var visibilidad by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = Unit) {
        visibilidad = true
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ){

        Column(
            modifier = Modifier.fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 70.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.weight(1f))
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = TextDark, fontWeight = FontWeight.ExtraBold, letterSpacing = 0.sp)) {
                        append("Credi")
                    }
                    withStyle(SpanStyle(color = TextPurple, fontWeight = FontWeight.ExtraBold, letterSpacing = 0.sp)) {
                        append("Flow")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 40.sp
            )
            Spacer(modifier = Modifier.height(20.dp))

            Box(
            modifier = Modifier
                .fillMaxWidth().padding(vertical = 30.dp),
            contentAlignment = Alignment.TopCenter
            ){
                Box(
                    modifier = Modifier.align(Alignment.TopEnd)
                        .rotate(-15f)
                        .offset(x = 30.dp, y = -(10).dp)
                ){
                    FloatingComponent(durationMillis = 2300, offsetY = 10.dp) {
                        Image(
                            painter = painterResource(id = R.drawable.bag_img_onb),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .size(160.dp)

                        )
                    }
                }


                Image(
                    painter = painterResource(id = R.drawable.case_img_onb),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(300.dp)
                )

                Box(
                    modifier = Modifier.align(Alignment.BottomStart)
                        .offset(x = -(10).dp)
                ){
                    FloatingComponent(durationMillis = 2450, offsetY = -(10).dp) {
                        Image(
                            painter = painterResource(id = R.drawable.carrito_img_onb),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .size(110.dp)

                        )
                    }
                }

            }
            Spacer(Modifier.weight(0.3f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedText(text = "Todo Lo Que ", isBold = false, visible = visibilidad, delay = 50, fontSize = 22)
                AnimatedText(text = "Desees ", isBold = true, visible = visibilidad, delay = 200, fontSize = 22)
                AnimatedText(text = "Al Alcance ", isBold = false, visible = visibilidad, delay = 350, fontSize = 22)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedText(text = "De Tus ", isBold = false, visible = visibilidad, delay = 500, fontSize = 22)
                AnimatedText(text = "Manos ", isBold = true, visible = visibilidad, delay = 650, fontSize = 22)
            }
            Spacer(modifier = Modifier.height(25.dp))
            ButtonComponent(text = "FINALIZAR", onClick = onFinish)

        }
    }
}