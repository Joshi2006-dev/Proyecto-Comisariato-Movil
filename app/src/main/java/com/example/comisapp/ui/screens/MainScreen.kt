package com.example.comisapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.example.comisapp.ui.navigation.BottomBarScreen
import com.example.comisapp.ui.navigation.CustomBottomBar
import com.example.comisapp.ui.screens.primaryNavigationScreens.HomeScreen
import kotlinx.coroutines.launch

@Composable
fun MainScreen(navController: NavHostController) {
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
                .fillMaxSize(),
            userScrollEnabled = true
        ) { page ->
            when (page) {
                0 ->
                    HomeScreen(

                        onNavigateToCentroAyuda = {
                            navController.navigate("helpCenter")
                        },
                        onNavigateToSugerencias = {
                            navController.navigate("sugerencias")
                        },

                        onNotificationsClick = {
                            navController.navigate("notification")
                        }
                    ) {
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