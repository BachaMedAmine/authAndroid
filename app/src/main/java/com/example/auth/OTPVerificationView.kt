package com.example.auth

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun OTPVerificationView(
    otpText: String,
    onOtpChange: (String) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center // Centers the Row within the Box
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0 until 6) {
                OTPTextBox(
                    otpText = otpText,
                    onOtpChange = onOtpChange,
                    index = i
                )
            }
        }
    }

    // Hidden field for managing focus and keyboard activation
    BasicTextField(
        value = otpText,
        onValueChange = onOtpChange,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.NumberPassword
        ),
        modifier = Modifier
            .height(1.dp)
            .fillMaxWidth()
            .padding(1.dp) // Invisible
    )
}

@Composable
fun OTPTextBox(
    otpText: String,
    onOtpChange: (String) -> Unit,
    index: Int
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(45.dp)
            .border(1.dp, Color.Gray, MaterialTheme.shapes.medium)
    ) {
        Text(
            text = if (otpText.length > index) otpText[index].toString() else "",
            style = MaterialTheme.typography.headlineSmall
        )
    }
}