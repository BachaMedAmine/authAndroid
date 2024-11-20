package com.example.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun ChangePasswordScreen(
    viewModel: AuthViewModel,
    onBackPressed: () -> Unit,


    token: String
) {
    val context = LocalContext.current
    var oldPassword by remember { mutableStateOf(TextFieldValue("")) }
    var newPassword by remember { mutableStateOf(TextFieldValue("")) }
    var confirmPassword by remember { mutableStateOf(TextFieldValue("")) }
    val changePasswordResult = viewModel.changePasswordResult.collectAsState(initial = null)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(onClick = onBackPressed) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Gray
                    )
                }
            }

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(100.dp)
                    .padding(top = 20.dp)
            )

            Text(
                text = "Change Password",
                fontSize = 28.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 10.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            CustomPasswordField(
                value = oldPassword,
                onValueChange = { oldPassword = it },
                label = "Old Password",
                leadingIcon = ImageVector.vectorResource(id = R.drawable.ic_lock)
            )

            Spacer(modifier = Modifier.height(20.dp))

            CustomPasswordField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = "Password",
                leadingIcon = ImageVector.vectorResource(id = R.drawable.ic_lock)
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomPasswordField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirm Password",
                leadingIcon = ImageVector.vectorResource(id = R.drawable.ic_lock_shield)
            )

            Spacer(modifier = Modifier.height(25.dp))

            Button(
                onClick = {
                    if (newPassword.text == confirmPassword.text) {
                        // Call the function directly with the required parameters
                        viewModel.changeUserPassword(
                            context = context,
                            oldPassword = oldPassword.text,
                            newPassword = newPassword.text
                        )
                    } else {
                        // Handle the case where passwords do not match
                        Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB4C424))
            ) {
                Text(text = "Change Password")
            }

            changePasswordResult.value?.let { result ->
                when {
                    result.isSuccess -> {
                        Toast.makeText(context, "Password changed successfully", Toast.LENGTH_SHORT).show()
                    }
                    result.isFailure -> {
                        Toast.makeText(context, result.exceptionOrNull()?.message ?: "Failed to change password", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}

@Composable
fun CustomPasswordField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    label: String,
    leadingIcon: ImageVector
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(imageVector = leadingIcon, contentDescription = null) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        )
    )
}