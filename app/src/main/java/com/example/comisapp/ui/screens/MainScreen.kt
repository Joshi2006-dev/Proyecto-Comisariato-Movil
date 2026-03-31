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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

import com.example.comisapp.data.repository.AuthRepository
import com.example.comisapp.data.repository.CategoryRepository
import com.example.comisapp.data.repository.CreditRepository
import com.example.comisapp.data.repository.ProductRepository

import com.example.comisapp.ui.navigation.BottomBarScreen
import com.example.comisapp.ui.navigation.CustomBottomBar

import com.example.comisapp.ui.screens.ViewModels.CatalogViewModel
import com.example.comisapp.ui.screens.ViewModels.CreditViewModel
import com.example.comisapp.ui.screens.ViewModels.HomeViewModel

import com.example.comisapp.ui.screens.primaryNavigationScreens.CatalogScreen
import com.example.comisapp.ui.screens.primaryNavigationScreens.CreditScreen
import com.example.comisapp.ui.screens.primaryNavigationScreens.HomeScreen
import com.example.comisapp.ui.screens.primaryNavigationScreens.SaveScreen

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

import kotlinx.coroutines.launch
import android.util.Log



private fun eliminarToken(email: String) {

    FirebaseMessaging.getInstance().token
        .addOnSuccessListener {

            FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(email)
                .update(
                    mapOf(
                        "fcmToken" to FieldValue.delete()
                    )
                )

            Log.d("FCM", "TOKEN ELIMINADO")

        }
}



@Composable
fun MainScreen(
    navController: NavHostController,
    authRepo: AuthRepository,
    initialPage: Int = 0
) {

    val screens = listOf(
        BottomBarScreen.Home,
        BottomBarScreen.Catalogo,
        BottomBarScreen.Creditos,
        BottomBarScreen.Guardados
    )

    val repoCredit = CreditRepository()
    val categoryRepo = CategoryRepository()
    val productRepo = ProductRepository()

    val factoryHome =
        HomeViewModel.Factory(
            categoryRepo,
            productRepo
        )

    val factoryCatalog =
        CatalogViewModel.Factory(
            categoryRepo,
            productRepo
        )

    val factoryCredit =
        CreditViewModel.Factory(
            repoCredit
        )

    val creditViewModel: CreditViewModel =
        viewModel(factory = factoryCredit)

    val catalogViewModel: CatalogViewModel =
        viewModel(factory = factoryCatalog)

    val homeViewModel: HomeViewModel =
        viewModel(factory = factoryHome)

    val auth by remember {
        mutableStateOf(authRepo)
    }

    val pagerState =
        rememberPagerState(
            initialPage = initialPage,
            pageCount = { screens.size }
        )

    val scope =
        rememberCoroutineScope()

    Scaffold(
        bottomBar = {

            CustomBottomBar(
                pagerState = pagerState,
                onTabSelected = { index ->

                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }

                }
            )

        }
    ) { innerPading ->

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = true
        ) { page ->

            when (page) {

                0 ->
                    HomeScreen(
                        viewModel = homeViewModel,

                        onNavigateToCuenta = {
                            navController.navigate("account")
                        },

                        onNotificationsClick = {
                            navController.navigate("notification")
                        },

                        onLogOut = {

                            val email =
                                FirebaseAuth
                                    .getInstance()
                                    .currentUser
                                    ?.email

                            if (email != null) {
                                eliminarToken(email)
                            }

                            auth.logout()

                            navController.navigate("login") {

                                popUpTo("principal") {
                                    inclusive = true
                                }

                            }

                        },

                        onGoDetail = {
                            navController.navigate(
                                "productDetail/$it"
                            )
                        }
                    )

                1 ->
                    CatalogScreen(
                        viewModel = catalogViewModel
                    )

                2 ->
                    CreditScreen(
                        onOpenDetail = {
                            navController.navigate(
                                "creditDetail/$it"
                            )
                        },
                        creditViewModel
                    )

                3 ->
                    SaveScreen()

            }

        }

    }

}