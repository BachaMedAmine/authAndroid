package com.example.auth

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import java.io.File
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter


@Composable
fun UploadCarImageScreen(navController: NavController, viewModel: AuthViewModel = viewModel()) {
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedImageUri = uri
        Log.d("UploadCarImageScreen", "Selected URI: $uri")

        // Save and compress the image immediately
        uri?.let {
            val imageFile = saveImageToCache(context, it)
            if (imageFile != null) {
                Log.d(
                    "UploadCarImageScreen",
                    "Image file exists: ${imageFile.exists()}, Path: ${imageFile.absolutePath}, Size: ${imageFile.length()} bytes"
                )
                viewModel.uploadCarImage(context, imageFile)
            } else {
                Toast.makeText(context, "Failed to save image to cache.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        // Button to open gallery and select an image
        Button(onClick = { launcher.launch("image/*") }) {
            Text("Select Image")
        }

        // Display the selected image
        selectedImageUri?.let { uri ->
            Image(
                painter = rememberAsyncImagePainter(model = uri),
                contentDescription = "Selected Image",
                modifier = Modifier
                    .size(200.dp)
                    .padding(16.dp)
            )

            // Upload button to process the image
            Button(onClick = {
                val imageFile = try {
                    File(context.cacheDir, "selected_image.jpg").apply {
                        context.contentResolver.openInputStream(uri)?.use { inputStream ->
                            outputStream().use { inputStream.copyTo(it) }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("UploadCarImageScreen", "Error creating file: ${e.message}", e)
                    null
                }

                if (imageFile != null) {
                    Log.d(
                        "UploadCarImageScreen",
                        "Image file exists: ${imageFile.exists()}, Path: ${imageFile.absolutePath}"
                    )
                    Log.d("UploadCarImageScreen", "File size: ${imageFile.length()} bytes")
                    // Upload the image
                    viewModel.processImageWithAI(context, imageFile) { response, error ->
                        if (response != null) {
                            Log.d("UploadCarImageScreen", "AI processed successfully: $response")

                            Toast.makeText(context, "Image processed successfully!", Toast.LENGTH_LONG).show()
                            // Navigate to CarsListScreen
                            navController.navigate("carsList")
                        } else {
                            Log.e("UploadCarImageScreen", "AI processing failed: $error")

                            Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Failed to create file from URI", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Upload Image")
            }
        }
    }
}