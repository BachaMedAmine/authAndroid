package com.example.auth.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.auth.R
import com.example.auth.ui.theme.MontMediumFamily
import com.example.auth.ui.theme.MontRegularFamily


@Composable
fun CButton(
    onClick: () -> Unit = {},
    text: String,
) {
    // make this button also resuable
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFB4C424)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
    ) {

        Text(
            text = text,
            style = TextStyle(
                fontSize = 18.sp,
                fontFamily = MontRegularFamily,
                fontWeight = FontWeight(500),
                color = Color.Black
            )
        )

    }
}