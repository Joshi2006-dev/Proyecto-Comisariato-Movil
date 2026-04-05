package com.example.comisapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.comisapp.ui.screens.MainScreen
import com.example.comisapp.ui.screens.OnboardingScreen
import com.example.comisapp.ui.screens.auth.LoginScreen
import com.example.comisapp.ui.screens.auth.RecoveryScreen
import com.example.comisapp.ui.screens.secondaryNavigationScreens.AccountScreen
import com.example.comisapp.ui.screens.secondaryNavigationScreens.HelpCenterScreen
import com.example.comisapp.ui.screens.secondaryNavigationScreens.NotificationScreen
import com.example.comisapp.ui.screens.secondaryNavigationScreens.SuggestionScreen
import com.example.comisapp.ui.screens.transactionScreens.DetalleCreditoScreen
import com.example.comisapp.ui.theme.ComisAppTheme
import com.example.comisapp.data.repository.AuthRepository
import com.example.comisapp.data.repository.CreditRepository
import com.example.comisapp.data.repository.ProductRepository
import com.example.comisapp.data.repository.SettingRepository
import com.example.comisapp.data.repository.UserRepository
import com.example.comisapp.ui.screens.ProductDetailScreen
import com.example.comisapp.ui.screens.ViewModels.ProductDetailViewModel
import com.example.comisapp.ui.screens.auth.AuthViewModel
import com.example.comisapp.ui.screens.auth.ChangePasswordScreen
import com.example.comisapp.data.repository.SuggestionRepository
import com.example.comisapp.ui.screens.ViewModels.CreditDetailViewModel
import com.example.comisapp.ui.screens.auth.RegisterScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.jvm.java
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.example.comisapp.data.repository.EmployeeRepository
import com.example.comisapp.ui.screens.ViewModels.AccountViewModel
import kotlinx.coroutines.launch
import androidx.fragment.app.FragmentActivity


class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ── Canal de notificaciones ───────────────────────────────────
        // Usa el mismo channelId "notificaciones" que ya tienes en
        // MyFirebaseMessagingService
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)
            val canal = NotificationChannel(
                "notificaciones_credito",
                "Notificaciones",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones de solicitudes de crédito"
                enableVibration(true)
            }
            manager.createNotificationChannel(canal)
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            ComisAppTheme {
                val nav = rememberNavController()
                val repo = remember { AuthRepository() }
                val scope = rememberCoroutineScope()
                val productRepo = remember { ProductRepository() }
                val userRepo = remember { UserRepository() }
                val creditRepo = remember { CreditRepository() }
                val settingRepo = remember { SettingRepository() }
                val employeeRepo = remember { EmployeeRepository() }
                val suggestionRepo = remember { SuggestionRepository() }


                // Este bloque se encarga de sacar al usuario si pasa a INACTIVO
                val userEmail = FirebaseAuth.getInstance().currentUser?.email

                if (userEmail != null) {
                    LaunchedEffect(userEmail) {
                        val docRef = FirebaseFirestore.getInstance()
                            .collection("usuarios")
                            .document(userEmail)

                        docRef.addSnapshotListener { snapshot, e ->
                            if (e != null) return@addSnapshotListener

                            if (snapshot != null && snapshot.exists()) {
                                val estado = snapshot.getString("estado")
                                if (estado == "INACTIVO") {
                                    scope.launch {
                                        repo.logout()
                                        nav.navigate("login") {

                                            popUpTo(0) { inclusive = true }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                NavHost(
                    navController = nav,
                    startDestination = if (repo.islogged()) "principal" else "login",
                    modifier = Modifier.fillMaxSize(),
                    enterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(300)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(300)
                        )
                    },
                    popEnterTransition = {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(300)
                        )
                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(300)
                        )
                    }
                ){
                    composable("onboarding"){
                        OnboardingScreen(
                            onFinish = {
                                nav.navigate("login"){
                                    popUpTo("onboarding"){
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }

                    composable("login"){
                        LoginScreen(
                            navController = nav,
                            onForgotPasswordClick = {
                                nav.navigate("recovery")
                            },
                            onLoginSuccess = {
                                nav.navigate("principal"){
                                    popUpTo("login"){
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }
                    composable("register") {
                        RegisterScreen(
                            onRegisterSuccess = {
                                nav.navigate("login") {
                                    popUpTo("register") { inclusive = true }
                                }
                            },
                            onBackToLogin = {
                                nav.popBackStack()
                            }
                        )
                    }

                    composable("recovery"){
                        RecoveryScreen(
                            onBack = {
                                nav.popBackStack()
                            }
                        )
                    }

                    composable(
                        route = "principal?page={page}",
                        arguments = listOf(
                            navArgument("page"){
                                type = NavType.IntType
                                defaultValue = 0
                            }
                        )
                    ){  backStackEntry ->
                        val initialPage = backStackEntry.arguments?.getInt("page") ?: 0
                        MainScreen(navController = nav, authRepo = repo, initialPage = initialPage)
                    }

                    composable("notification"){
                        NotificationScreen(
                            onBackClick = {
                                nav.popBackStack()
                            }
                        )
                    }

                    composable("helpCenter"){
                        HelpCenterScreen(
                            onBackClick = {
                                nav.popBackStack()
                            }
                        )
                    }

                    composable("sugerencias"){
                        SuggestionScreen(
                            onBackClick = {
                                nav.popBackStack()
                            }
                        )
                    }

                    composable("creditDetail/{id}"){
                            backStackEntry ->
                        val id = backStackEntry.arguments?.getString("id")
                        val factoryDetail = remember{
                            CreditDetailViewModel.Factory(creditRepo,settingRepo,userRepo,id ?: "")
                        }
                        val creditViewModel: CreditDetailViewModel = viewModel(factory = factoryDetail)
                        if (id != null) {
                            DetalleCreditoScreen(
                                onBack = {
                                    nav.popBackStack()
                                },
                                viewModel = creditViewModel
                            )
                        }


                    }


                    composable("account"){
                        val factoryAccount = remember{
                            AccountViewModel.Factory(employeeRepo,userRepo)
                        }
                        val accountViewModel: AccountViewModel = viewModel(factory = factoryAccount)
                        AccountScreen(
                            onBackClick = {
                                nav.popBackStack()
                            },
                            nav = nav,
                            accountViewModel
                        )
                    }

                    composable("changePassword"){
                        val authViewModel : AuthViewModel = viewModel(factory = AuthViewModel.Factory(repo))
                        ChangePasswordScreen(
                            viewModel = authViewModel,
                            onSucces = {
                                nav.popBackStack()
                            },
                            onBackClick = {
                                nav.popBackStack()
                            }
                        )
                    }

                    composable("productDetail/{id}"){
                        backStackEntry ->
                        val id = backStackEntry.arguments?.getString("id")
                        val factoryDetail = remember{
                            ProductDetailViewModel.Factory(
                                productRepo,
                                creditRepo,
                                userRepo,
                                settingRepo,
                                id ?: "")
                        }
                        val productDetailViewModel: ProductDetailViewModel = viewModel(factory = factoryDetail)
                        if (id != null) {
                            ProductDetailScreen(
                                onBack = {
                                    nav.popBackStack()
                                },
                                onNavigateToCredits = {
                                    nav.popBackStack()
                                    nav.navigate("principal?page=2"){
                                        popUpTo("principal"){
                                            inclusive = true
                                        }
                                    }
                                },
                                viewModel = productDetailViewModel
                            )
                        }


                    }




                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ComisAppTheme {
        Greeting("Android")
    }
}