import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.auth.AuthViewModel
import com.example.auth.R
import com.example.auth.components.CButton
import com.example.auth.components.CTextField
import com.example.auth.ui.theme.MontBoldFamily
import com.example.auth.ui.theme.MontRegularFamily
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.common.api.ApiException
import androidx.activity.compose.rememberLauncherForActivityResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(
    navController: NavHostController,
    viewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current

    // Configure Google Sign-In client
    val googleSignInClient = remember {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("776445557487-v8tn5ljgb5d3t6823tc83i87nkjv6b6q.apps.googleusercontent.com") // Replace with your client ID
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, options)
    }

    // Google Sign-In launcher
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        handleGoogleSignInResult(task,
            onSuccess = { idToken ->
                viewModel.googleSignUp(idToken)
                // Update login state and navigate to the main screen
                navController.navigate("WelcomeScreen") {
                    popUpTo("SignupScreen") { inclusive = true } // Prevents back navigation to login
                }
            },
            onFailure = { error ->
                Log.e("GoogleSignIn", error)
            }
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        var name by remember { mutableStateOf("") }
        var emailAddress by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }
        var isChecked by remember { mutableStateOf(false) }

        // Validation error messages
        var fullNameError by remember { mutableStateOf<String?>(null) }
        var emailError by remember { mutableStateOf<String?>(null) }
        var passwordError by remember { mutableStateOf<String?>(null) }
        var confirmPasswordError by remember { mutableStateOf<String?>(null) }
        var termsError by remember { mutableStateOf<String?>(null) }

        // Observe the registration state
        val registerState by viewModel.registerState

        fun validateInputs(): Boolean {
            var isValid = true

            fullNameError = if (name.isBlank()) {
                isValid = false
                "Full Name is required"
            } else null

            emailError = if (emailAddress.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
                isValid = false
                "Valid Email Address is required"
            } else null

            passwordError = if (password.isBlank() || password.length < 6) {
                isValid = false
                "Password must be at least 6 characters"
            } else null

            confirmPasswordError = if (confirmPassword != password) {
                isValid = false
                "Passwords do not match"
            } else null

            termsError = if (!isChecked) {
                isValid = false
                "You must accept the terms and conditions"
            } else null

            return isValid
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .height(90.dp)
                        .width(90.dp)
                )

                Text(
                    text = "Welcome 👋",
                    style = TextStyle(
                        fontSize = 28.sp,
                        fontFamily = MontBoldFamily,
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    modifier = Modifier.padding(bottom = 14.dp)
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

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    "Sign up to get started",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontFamily = MontRegularFamily,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CTextField(hint = "Full Name", value = name, onValueChange = { name = it })
                    fullNameError?.let {
                        Text(it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.align(Alignment.Start))
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    CTextField(hint = "Email Address", value = emailAddress, onValueChange = { emailAddress = it })
                    emailError?.let {
                        Text(it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.align(Alignment.Start))
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    CTextField(hint = "Password", value = password, onValueChange = { password = it }, isPassword = true)
                    passwordError?.let {
                        Text(it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.align(Alignment.Start))
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    CTextField(hint = "Confirm Password", value = confirmPassword, onValueChange = { confirmPassword = it }, isPassword = true)
                    confirmPasswordError?.let {
                        Text(it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.align(Alignment.Start))
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(
                                id = if (isChecked) R.drawable.ic_check_box else R.drawable.ic_check_box_outline_blank
                            ),
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { isChecked = !isChecked },
                            tint = if (isChecked) Color(0xFFB4C424) else Color.Gray
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "I accept ", fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)

                        ClickableText(
                            text = AnnotatedString("terms and conditions"),
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.freeprivacypolicy.com/live/e9a63b42-a84d-4785-8499-aeab786caff9"))
                                context.startActivity(intent)
                            },
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFFB4C424),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    termsError?.let {
                        Text(it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.align(Alignment.Start))
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    CButton(
                        text = "Signup",
                        onClick = {
                            if (validateInputs()) {
                                viewModel.register(emailAddress, password, confirmPassword, name)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    registerState?.let { message ->
                        Text(
                            text = message,
                            color = if (message.contains("failed", ignoreCase = true)) Color.Red else Color.Green,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        if (message == "Registration successful") {
                            navController.navigate("WelcomeScreen")
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))

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
                            onClick = {
                                googleSignInClient.signOut().addOnCompleteListener {
                                    // Force Google to show account picker by clearing the previous session
                                    googleSignInLauncher.launch(googleSignInClient.signInIntent)
                                    Log.d("GoogleSignIn", "Sign-in intent launched after sign-out.")
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.background),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                        ){
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
                            onClick = { /* Handle Facebook signup */ },
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
                            text = "Already have an account? ",
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                fontFamily = MontRegularFamily
                            )
                        )

                        Text(
                            text = "Login",
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = Color(0xFFB4C424),
                                fontFamily = MontRegularFamily,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.clickable {
                                navController.navigate("login")
                            }
                        )
                    }
                }
            }
        }
    }
}

// Function to handle the result of Google Sign-In
private fun handleGoogleSignInResult(
    task: Task<GoogleSignInAccount>,
    onSuccess: (String) -> Unit,
    onFailure: (String) -> Unit
) {
    try {
        val account = task.getResult(ApiException::class.java)
        val idToken = account?.idToken
        if (idToken != null) {
            // Pass idToken to your backend or authentication process
            onSuccess(idToken)
        } else {
            onFailure("Failed to retrieve ID token")
        }
    } catch (e: ApiException) {
        Log.e("GoogleSignIn", "Google sign-in failed: ${e.statusCode}", e)
        onFailure("Google sign-in failed: ${e.statusCode}")
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 640)
@Composable
fun SignupScreenPreview() {
    SignupScreen(rememberNavController())
}