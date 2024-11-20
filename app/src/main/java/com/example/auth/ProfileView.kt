package com.example.auth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.auth.ui.theme.AuthTheme

import android.content.Context
import androidx.compose.ui.platform.LocalContext


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileView(navController: NavController,viewModel: AuthViewModel) {
    var showEditProfile by remember { mutableStateOf(false) }
    var showChangePassword by remember { mutableStateOf(false) }
    val context = LocalContext.current


    Scaffold(

        topBar = {
            TopAppBar(
                title = { Text("My Profile", fontSize = 20.sp, fontWeight = FontWeight.Bold) }
            )
        },
        bottomBar = {
            BottomNavBar()
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "PROFILE VIEW",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(40.dp))

                ProfileButton(text = "Edit Profile", onClick = { navController.navigate("edit_profile")  })
                ProfileButton(text = "Change Password",
                    onClick = {
                        val token = viewModel.getToken(context) ?: ""
                        navController.navigate("changePassword/$token")
                    })
                ProfileButton(text = "Logout", onClick = { navController.navigate("login") })

                Spacer(modifier = Modifier.height(40.dp))

                Divider()

                Spacer(modifier = Modifier.height(16.dp))



                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ProfileButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB4C424)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .height(48.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(text = text, fontSize = 16.sp)
    }
}




@Composable
fun BottomNavBar() {
    BottomAppBar(
        contentPadding = PaddingValues(horizontal = 32.dp),
        containerColor = Color.Transparent
    ) {
        BottomNavItem(iconName = R.drawable.ic_home)
        Spacer(modifier = Modifier.weight(1f))
        BottomNavItem(iconName = R.drawable.ic_heart)
        Spacer(modifier = Modifier.weight(1f))
        BottomNavItem(iconName = R.drawable.ic_message)
        Spacer(modifier = Modifier.weight(1f))
        BottomNavItem(iconName = R.drawable.ic_person)
    }
}

@Composable
fun BottomNavItem(iconName: Int) {
    Icon(
        painter = painterResource(id = iconName),
        contentDescription = null,
        modifier = Modifier.size(24.dp)
    )
}


