package com.example.auth

import SignupScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.auth.ui.theme.AuthTheme
//import androidx.navigation.compose.getBackStackEntry


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: AuthViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
            AuthTheme {
                NavigationView(viewModel)
            }
        }
    }
}

@Composable
fun NavigationView(viewModel: AuthViewModel) {
    val navController = rememberNavController()
    val otpText = remember { mutableStateOf("") }
    var showOtpSheet by remember { mutableStateOf(false) }
    var showResetSheet by remember { mutableStateOf(false) }
    val isAuthenticated = remember { mutableStateOf(false) }

    LaunchedEffect(isAuthenticated.value) {
        if (isAuthenticated.value) {
            navController.navigate("profile") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (isAuthenticated.value) "profile" else "login"
    ) {
        composable("login") { LoginScreen(navController) }
        composable("signup") { SignupScreen(navController) }
        composable("profile") { ProfileView(navController, viewModel) }
        composable("edit_profile") {
            EditProfileScreen(onBackPressed = { navController.popBackStack() })
        }
        composable(
            "changePassword/{token}",
            arguments = listOf(navArgument("token") { defaultValue = "" })
        ) { entry ->
            val token = entry.arguments?.getString("token") ?: ""

            ChangePasswordScreen(
                onBackPressed = { navController.popBackStack() },
                token = token,
                viewModel = viewModel
            )
        }
        composable("forgotPassword") {
            ForgotPassword(
                onBackPressed = { navController.popBackStack() },
                navController = navController,
                showBottomSheet = true,
                onDismissBottomSheet = { navController.popBackStack() }
            )
        }
        composable("resetPassword") {
            LaunchedEffect(Unit) {
                showResetSheet = true
            }
        }
        composable("photosScreen") { PhotosView() }
        composable("welcomeScreen") { WelcomeScreen(navController) }
        composable("otpView") {
            LaunchedEffect(Unit) {
                showOtpSheet = true
            }
        }
        composable("uploadCarImage") { UploadCarImageScreen(navController) }
        composable("carsList") { CarsListScreen(navController) }
    }

    if (showResetSheet) {
        PasswordResetView(
            otpText = otpText.value,
            onDismiss = { showResetSheet = false },
            showBottomSheet = showResetSheet,
            navController = navController,
            onPasswordResetSuccess = {
                showResetSheet = false
                showOtpSheet = false // Ensure OTP sheet is cleared
                navController.navigate("login") {
                    popUpTo("login") { inclusive = true } // Clear navigation stack
                }
            }
        )
    }

    if (showOtpSheet) {
        OTPView(
            otpText = otpText.value,
            onOtpChange = { otpText.value = it },
            onBackPressed = { showOtpSheet = false },
            navController = navController,
            showBottomSheet = showOtpSheet,
            onDismissBottomSheet = { showOtpSheet = false },
            onOtpSuccess = {
                showOtpSheet = false // Close OTP sheet after success
                navController.navigate("resetPassword") {
                    popUpTo("otpView") { inclusive = true } // Clear OTP view
                }
            }
        )
    }
}