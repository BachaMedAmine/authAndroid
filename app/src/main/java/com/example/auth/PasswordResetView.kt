package com.example.auth

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.auth.components.CTextField
import com.example.auth.ui.theme.AuthTheme
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.auth.AuthViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordResetView(
    otpText: String,
    onDismiss: () -> Unit,
    showBottomSheet: Boolean,
    navController: NavController,
    onPasswordResetSuccess: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val resetPasswordState by viewModel.resetPasswordState.collectAsState(initial = null)
    val context = LocalContext.current

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Reset Password",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(20.dp))

                CTextField(
                    hint = "Password",
                    value = password,
                    onValueChange = { password = it }
                )

                Spacer(modifier = Modifier.height(10.dp))

                CTextField(
                    hint = "Confirm Password",
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it }
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.resetPassword(
                                context = context,
                                newPassword = password,
                                confirmPassword = confirmPassword
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB4C424)),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                ) {
                    Text("Reset")
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Observe state changes for password reset
                LaunchedEffect(resetPasswordState) {
                    resetPasswordState?.let { state ->
                        when (state) {
                            "Password reset successfully" -> {
                                Toast.makeText(context, state, Toast.LENGTH_SHORT).show()
                                coroutineScope.launch {
                                    sheetState.hide()
                                    navController.navigate("login") {
                                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                    }
                                }
                            }
                            else -> {
                                Toast.makeText(context, state ?: "An error occurred", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }
}
fun resetPassword(
    otpText: String,
    password: String,
    confirmPassword: String,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    if (password != confirmPassword) {
        onError("Passwords do not match")
        return
    }

    // Launch a coroutine to simulate an asynchronous network call
    GlobalScope.launch {
        // Simulate a 1.5-second delay for the network call
        delay(1500)

        // Simulated logic: success or failure
        val success = true // Replace this with actual logic

        // Call the callback on the main thread
        if (success) {
            onSuccess()
        } else {
            onError("Password reset failed")
        }
    }
}