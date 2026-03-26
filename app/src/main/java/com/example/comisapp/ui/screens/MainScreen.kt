package com.example.comisapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.comisapp.ui.navigation.BottomBarScreen
import com.example.comisapp.ui.navigation.CustomBottomBar
import com.example.comisapp.ui.screens.primaryNavigationScreens.HomeScreen
import com.example.comisapp.ui.theme.CardBorderColor
import com.example.comisapp.ui.theme.TextPurple
import kotlinx.coroutines.launch

@Composable
fun MainScreen() {
    val screens = listOf(
        BottomBarScreen.Home,
        BottomBarScreen.Catalogo,
        BottomBarScreen.Creditos,
        BottomBarScreen.Guardados
    )

    val pagerState = rememberPagerState(pageCount = { screens.size })
    val scope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            CustomBottomBar(
                pagerState = pagerState,
                onTabSelected = {
                    index ->
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            userScrollEnabled = true
        ) { page ->
            when (page) {
                0 ->
                    HomeScreen() {
                        contentPadding ->
                        Box(modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                        )
                    }
                1 ->
                    Text("Esta es el Catalogoso", modifier = Modifier.fillMaxWidth())
                2 ->
                    Text("Esta es el Creditoso", modifier = Modifier.fillMaxWidth())

                3 ->
                    Text("Esta es el Guardoso", modifier = Modifier.fillMaxWidth())
            }
        }
    }
}