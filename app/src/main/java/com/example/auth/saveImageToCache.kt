package com.example.auth

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import java.io.File

fun saveImageToCache(context: Context, uri: Uri): File? {
    return try {
        // Create a file in the cache directory
        val cacheFile = File(context.cacheDir, "compressed_image.jpg")

        // Open input stream for the selected URI
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            // Decode the image to a Bitmap
            val bitmap = BitmapFactory.decodeStream(inputStream)

            // Compress and save the Bitmap to the cache file
            cacheFile.outputStream().use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream) // Adjust compression as needed
            }
        }

        Log.d("saveImageToCache", "File saved to cache: ${cacheFile.absolutePath}")
        cacheFile // Return the saved file
    } catch (e: Exception) {
        Log.e("saveImageToCache", "Error saving image to cache: ${e.message}", e)
        null
    }
}