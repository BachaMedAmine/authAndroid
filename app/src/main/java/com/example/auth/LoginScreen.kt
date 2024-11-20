package com.example.auth

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.auth.components.CButton
import com.example.auth.components.CTextField
import com.example.auth.ui.theme.MontBoldFamily
import com.example.auth.ui.theme.MontRegularFamily
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import androidx.activity.compose.rememberLauncherForActivityResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    var showForgotPasswordSheet by remember { mutableStateOf(false) }
    var emailAddress by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Observer for login state
    val loginState by viewModel.loginState

    // Google Sign-In configuration
    val googleSignInClient = remember {
        GoogleSignIn.getClient(
            context,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id)) // Add your client ID here
                .requestEmail()
                .build()
        )
    }

    // Activity launcher for Google Sign-In intent
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        handleSignInResult(task, viewModel)
    }

    // Function to initiate Google Sign-In
    fun signInWithGoogle() {
        googleSignInLauncher.launch(googleSignInClient.signInIntent)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(30.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "Carz Logo",
                        modifier = Modifier
                            .height(90.dp)
                            .width(90.dp)
                    )
                }

                Text(
                    text = "Welcome Back 👋",
                    style = TextStyle(
                        fontSize = 28.sp,
                        fontFamily = MontBoldFamily,
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    modifier = Modifier
                        .padding(bottom = 14.dp)
                )

                Row(horizontalArrangement = Arrangement.Center) {
                    Text(
                        text = "to ",
                        style = TextStyle(
                            fontSize = 28.sp,
                            fontFamily = MontBoldFamily,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )

                    Text(
                        text = "OVA",
                        style = TextStyle(
                            fontSize = 28.sp,
                            fontFamily = MontBoldFamily,
                            color = Color(0xFFB4C424)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        "Log in to your account using email or social",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = MontRegularFamily,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        "networks",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontFamily = MontRegularFamily,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    CTextField(
                        hint = "Email Address",
                        value = emailAddress,
                        onValueChange = { emailAddress = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    CTextField(
                        hint = "Password",
                        value = password,
                        onValueChange = { password = it },
                        isPassword = true
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = { showForgotPasswordSheet = true },
                            modifier = Modifier.height(48.dp),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.background),
                        ) {
                            Text(
                                text = "Forgot Password?",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    color = Color(0xFFB4C424),
                                    fontFamily = MontRegularFamily,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        if (showForgotPasswordSheet) {
                            ForgotPassword(
                                onBackPressed = { showForgotPasswordSheet = false },
                                navController = navController,
                                showBottomSheet = true,
                                onDismissBottomSheet = { showForgotPasswordSheet = false }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Updated Login Button to trigger viewModel.login
                    CButton(
                        text = "Login",
                        onClick = {
                            viewModel.login(context, emailAddress, password)
                        }
                    )

                    Spacer(modifier = Modifier.height(36.dp))

                    // Display login state result
                    loginState?.let { message ->
                        Text(
                            text = message,
                            color = if (message.contains("failed", ignoreCase = true)) Color.Red else Color.Green,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        // Navigate to WelcomeScreen on successful login
                        if (message == "Login successful") {
                            navController.navigate("WelcomeScreen")
                        }
                    }

                    Spacer(modifier = Modifier.height(36.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Divider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                        Text(
                            text = "Or continue with social account",
                            style = TextStyle(fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        Divider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = { signInWithGoogle() },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.background),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_google),
                                contentDescription = null,
                                tint = Color.Unspecified
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Google",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }

                        Button(
                            onClick = { /* Handle Facebook login */ },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.background),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_facebook),
                                contentDescription = null,
                                tint = Color.Unspecified
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Facebook",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(60.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Didn't have an account? ",
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                fontFamily = MontRegularFamily
                            )
                        )

                        Text(
                            text = "Signup",
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = Color(0xFFB4C424),
                                fontFamily = MontRegularFamily,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.clickable {
                                navController.navigate("signup")
                            }
                        )
                    }
                }
            }
        }
    }
}

// Handle Google Sign-In result
private fun handleSignInResult(task: Task<GoogleSignInAccount>, viewModel: AuthViewModel) {
    try {
        val account = task.getResult(ApiException::class.java)
        val idToken = account?.idToken
        if (idToken != null) {
            // Send ID token to backend for verification
            viewModel.googleLogin(idToken)
        }
    } catch (e: ApiException) {
        // Handle sign-in error
    }
}