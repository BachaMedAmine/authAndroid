package com.example.auth

import android.annotation.SuppressLint
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Scale
import com.example.auth.ui.theme.MontBoldFamily
import com.example.auth.ui.theme.MontMediumFamily
import com.example.auth.ui.theme.MontRegularFamily
import kotlinx.coroutines.launch
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.widget.Toast
import androidx.lifecycle.viewmodel.compose.viewModel


@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun WelcomeScreen(navController: NavController, viewModel: AuthViewModel = viewModel()) {
    var offset by remember { mutableStateOf(0.dp) }
    var imageIndex by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    val context = LocalContext.current

    // Créer un lanceur pour ouvrir la caméra
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            Toast.makeText(context, "Image Captured!", Toast.LENGTH_SHORT).show()
            // Vous pouvez manipuler l'image ici si besoin
        } else {
            Toast.makeText(context, "Failed to capture image", Toast.LENGTH_SHORT).show()
        }
    }

    val imageResources = listOf(
        R.drawable.image1, // Remplacez par le nom de votre première image
        R.drawable.image2, // Remplacez par le nom de votre deuxième image
        R.drawable.image3  // Remplacez par le nom de votre troisième image
    )

    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            imageIndex = (imageIndex + 1) % imageResources.size
        }
        val navigationState = viewModel.navigationState

        if (navigationState == "CarsList") {
            navController.navigate("carsList")
        } else if (navigationState == "UploadCarImage") {
            navController.navigate("uploadCarImage")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ) {
        Image(
            painter = painterResource(id = imageResources[imageIndex]),
            contentDescription = "Background Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .fillMaxSize()
                .padding(40.dp)
        ) {
            Text(
                text = "Hello There",
                fontSize = 28.sp,
                fontFamily = MontBoldFamily,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Slide to Open the camera and Capture your car",
                fontSize = 12.sp,
                color = Color.White,
                fontFamily = MontRegularFamily,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Box(
                modifier = Modifier
                    .width(150.dp)
                    .height(50.dp)
                    .align(Alignment.Start)
                    .offset(x = offsetX.value.dp)
                    .clickable {
                        coroutineScope.launch {
                            offsetX.animateTo(
                                targetValue = 100f,
                                animationSpec = tween(durationMillis = 1000)
                            )
                            navController.navigate("uploadCarImage") // Navigate to PhotosView
                        }
                    }
                    .background(
                        color = Color(0xFFB4C424),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Get Started >",
                    color = Color.White,
                    fontSize = 18.sp,
                )
            }
        }
    }
}

