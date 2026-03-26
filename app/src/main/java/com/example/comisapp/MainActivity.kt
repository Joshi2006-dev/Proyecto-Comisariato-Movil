package com.example.comisapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.comisapp.ui.screens.MainScreen
import com.example.comisapp.ui.screens.OnboardingScreen
import com.example.comisapp.ui.screens.auth.LoginScreen
import com.example.comisapp.ui.screens.auth.RecoveryScreen
import com.example.comisapp.ui.screens.secondaryNavigationScreens.AccountScreen
import com.example.comisapp.ui.screens.secondaryNavigationScreens.HelpCenterScreen
import com.example.comisapp.ui.screens.secondaryNavigationScreens.NotificationScreen
import com.example.comisapp.ui.screens.secondaryNavigationScreens.SuggestionScreen
import com.example.comisapp.ui.theme.ComisAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComisAppTheme {
                val nav = rememberNavController()

                NavHost(
                    navController = nav,
                    startDestination = "principal",
                    modifier = Modifier.fillMaxSize()
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
                            onForgotPasswordClick = {
                                nav.navigate("recovery")
                            },
                            onLoginSuccess = {
                                nav.navigate("principal")
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

                    composable("principal"){
                        MainScreen(navController = nav)
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

                    composable("account"){
                        AccountScreen(
                            onBackClick = {
                                nav.popBackStack()
                            }
                        )
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