package com.example.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OTPView(
    otpText: String,
    onOtpChange: (String) -> Unit,
    onBackPressed: () -> Unit,
    navController: NavHostController,
    showBottomSheet: Boolean,
    onDismissBottomSheet: () -> Unit,
    onOtpSuccess: () -> Unit, // Add this parameter
    viewModel: AuthViewModel = viewModel()
) {
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var emailID by remember { mutableStateOf("") }

    val otpVerificationState by viewModel.otpVerificationState.collectAsState(initial = null)

    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(viewModel.otpVerificationState.collectAsState().value) {
        val state = viewModel.otpVerificationState.value
        state?.onSuccess {
            onOtpSuccess() // Trigger OTP success logic
        }?.onFailure {
            Toast.makeText(context, "Failed to verify OTP: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismissBottomSheet,
            sheetState = sheetState
        ) {
            Row(horizontalArrangement = Arrangement.Start) {
                IconButton(onClick = onBackPressed) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Gray
                    )
                }
            }
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Enter OTP",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 5.dp)
                )

                Text(
                    text = "A 6-digit code has been sent to your Email ID.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                OTPVerificationView(otpText = otpText, onOtpChange = onOtpChange)

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                // Pass the OTP to verify
                                viewModel.verifyOtp(context, otpText)
                                viewModel.otpVerificationState.collect { state ->
                                    state?.onSuccess {
                                        Toast.makeText(context, "OTP Verified Successfully", Toast.LENGTH_SHORT).show()
                                        navController.navigate("resetPassword") // Navigate on success
                                    }?.onFailure {
                                        Toast.makeText(context, "Failed to verify OTP: ${it.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Verification failed: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFB4C424)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Verify",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Error message
                errorMessage?.let {
                    Text(it, color = Color.Red, fontSize = 12.sp)
                }
            }
        }
    }
}