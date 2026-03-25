package com.example.comisapp.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.comisapp.ui.theme.CardBorderColor

@Composable
fun CustomBottomBar(
    pagerState: PagerState,
    onTabSelected: (Int) -> Unit
) {
    val items = listOf(
        BottomBarScreen.Home,
        BottomBarScreen.Catalogo,
        BottomBarScreen.Creditos,
        BottomBarScreen.Guardados
    )

    val activeIndex by remember { derivedStateOf { pagerState.currentPage } }

    val animatedOffset = animateDpAsState(
        targetValue = (93.75).dp * activeIndex,
        animationSpec = tween(durationMillis = 300),
        label = "tab_offset"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .border(width = 0.5.dp, CardBorderColor.copy(alpha = 0.20f)),
        color = Color.White,

    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .offset(x = animatedOffset.value, y = 8.dp)
                    .size(68.dp)
                    .background(
                        color = Color.Black,
                        shape = RoundedCornerShape(5.dp)
                    )
            )

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, item ->
                    val isSelected = pagerState.currentPage == index

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { onTabSelected(index) }
                            .padding(8.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = item.icon),
                            contentDescription = null,
                            tint = if (isSelected) Color.White else Color(0xFF64748B),
                            modifier = Modifier.size(26.dp)
                        )

                        AnimatedVisibility(
                            visible = !isSelected,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Text(
                                text = item.title,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B),
                                letterSpacing = 1.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}