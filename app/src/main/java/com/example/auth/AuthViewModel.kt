package com.example.auth

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.ui.platform.LocalContext
import java.io.File


class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    var carDetailsState by mutableStateOf<CarDetails?>(null)
        private set



    var carsState by mutableStateOf<List<Car>?>(null)
    var navigationState by mutableStateOf<String?>(null)
    var uploadState by mutableStateOf<String?>(null)

    private val _sendOtpState = MutableStateFlow<Result<String>?>(null)
    val sendOtpState: StateFlow<Result<String>?> = _sendOtpState

    private val _resetPasswordState = MutableStateFlow<String?>(null)

    val resetPasswordState: StateFlow<String?> = _resetPasswordState

    private val _otpVerificationState = MutableStateFlow<Result<String>?>(null)
    val otpVerificationState: StateFlow<Result<String>?> = _otpVerificationState



    // State variables for UI updates
    var loginState = mutableStateOf<String?>(null)
    var registerState = mutableStateOf<String?>(null)
    var forgotPasswordState = mutableStateOf<String?>(null)


    var emailID by mutableStateOf("")
    var emailForReset = mutableStateOf("")
    var changePasswordResultState by mutableStateOf<String?>(null)
        private set

    // StateFlow for change password result
    private val _changePasswordResult = MutableStateFlow<Result<String>?>(null)
    val changePasswordResult: StateFlow<Result<String>?> = _changePasswordResult
    // Get token from SharedPreferences
    fun getToken(context: Context): String? {
        return repository.getToken(context)
    }

    // Login function with Context parameter for saving token in SharedPreferences
    fun login(context: Context, email: String, password: String) {
        viewModelScope.launch {
            repository.login(email, password, context) { response, error ->
                if (response != null) {
                    loginState.value = "Login successful"
                } else {
                    loginState.value = "Login failed: $error"
                }
            }
        }
    }

    // Register function
    fun register(email: String, password: String, confirmPassword: String, name: String) {
        viewModelScope.launch {
            val request = RegisterRequest(email, password, confirmPassword, name)
            repository.register(request) { response, error ->
                registerState.value = if (response != null) {
                    "Registration successful"
                } else {
                    "Registration failed: $error"
                }
            }
        }
    }

    // Change Password function with Context parameter for retrieving token from SharedPreferences
    suspend fun changePassword(context: Context, oldPassword: String, newPassword: String) {
        val token = repository.getToken(context)
        if (token.isNullOrEmpty()) {
            Log.e("AuthViewModel", "Token is missing")
            Toast.makeText(context, "Authorization token is missing. Please log in again.", Toast.LENGTH_SHORT).show()
            return
        }

        val request = ChangePasswordRequest(oldPassword, newPassword)
        val result = repository.changePassword(token, request)

        result.onSuccess {
            Log.d("AuthViewModel", "Password changed successfully: $it")
            changePasswordResultState = "Password changed successfully"
        }.onFailure {
            Log.e("AuthViewModel", "Failed to change password: ${it.message}")
            changePasswordResultState = "Failed to change password: ${it.message}"
        }
    }

    // Google Login function
    fun googleLogin(idToken: String) {
        viewModelScope.launch {
            repository.googleSignIn(idToken) { response, error ->
                loginState.value = if (response != null) {
                    "Login successful"
                } else {
                    "Login failed: $error"
                }
            }
        }
    }

    // Google Sign-Up function
    fun googleSignUp(idToken: String) {
        viewModelScope.launch {
            repository.googleSignUp(idToken) { response, error ->
                registerState.value = if (response != null) {
                    "Registration successful"
                } else {
                    "Registration failed: $error"
                }
            }
        }
    }
    fun verifyOtp(context: Context, otp: String) {
        viewModelScope.launch {
            try {
                val response = repository.verifyOtp(otp) // Correctly construct the request
                repository.saveToken(context, response.accessToken) // Save token
                _otpVerificationState.value = Result.success("OTP Verified Successfully")
            } catch (e: Exception) {
                _otpVerificationState.value = Result.failure(e)
            }
        }
    }

    // Send OTP function
    fun sendOtp(email: String) {
        viewModelScope.launch {
            repository.sendOtp(email) { response, error ->
                if (response != null) {
                    _sendOtpState.value = Result.success(response)
                    Log.d("SendOtp", "OTP sent successfully: $response")
                } else {
                    _sendOtpState.value = Result.failure(Exception(error))
                    Log.e("SendOtp", "Failed to send OTP: $error")
                }
            }
        }
    }

    // Reset Password function
    fun resetPassword(context: Context, newPassword: String, confirmPassword: String) {
        viewModelScope.launch {
            if (newPassword != confirmPassword) {
                _resetPasswordState.value = "Passwords do not match"
                return@launch
            }

            try {
                val token = repository.getToken(context) ?: ""
                if (token.isEmpty()) {
                    _resetPasswordState.value = "Authorization token is missing"
                    return@launch
                }

                val request = ResetPasswordRequest(newPassword = newPassword, confirmPassword = confirmPassword)
                val result = repository.resetPassword(token, request)

                result.onSuccess {
                    _resetPasswordState.value = "Password reset successfully"
                }.onFailure {
                    _resetPasswordState.value = "Failed to reset password: ${it.message}"
                }
            } catch (e: Exception) {
                _resetPasswordState.value = "An error occurred: ${e.message}"
            }
        }
    }




    fun changeUserPassword(context: Context, oldPassword: String, newPassword: String) {
        viewModelScope.launch {
            // Retrieve the token from SharedPreferences
            val token = repository.getToken(context) ?: ""

            // Check if the token is missing
            if (token.isEmpty()) {
                Log.e("AuthViewModel", "Token is missing")

            }

            // Log the token for debugging (optional)
            Log.d("AuthViewModel", "Using token: Bearer $token")

            // Prepare the change password request
            val request = ChangePasswordRequest(oldPassword, newPassword)

            // Call the change password API with the token
            val result = repository.changePassword(token, request)
            // Handle the result (success or failure)
            result.onSuccess {
                Log.d("AuthViewModel", "Password changed successfully")
                // Update UI or state based on successful password change
            }.onFailure {
                Log.e("AuthViewModel", "Failed to change password: ${it.message}")
                // Update UI or state based on failure
            }
        }
    }


    fun uploadCarImage(context: Context, imageFile: File) {
        viewModelScope.launch {
            val token = repository.getToken(context)
            if (token.isNullOrEmpty()) {
                uploadState = "Token is missing. Please log in again."
                return@launch
            }

            repository.uploadCarImage(token, imageFile) { carDetails, error ->
                if (carDetails != null) {
                    carDetailsState = carDetails
                    uploadState = "Upload successful! Car: ${carDetails.make} ${carDetails.carModel}, Year: ${carDetails.year}"
                } else {
                    uploadState = "Failed to upload: $error"
                }
            }
        }
    }


    fun processImageWithAI(
        context: Context,
        imageFile: File,
        onResult: (String?, String?) -> Unit
    ) {
        val token = repository.getToken(context)
        if (token.isNullOrEmpty()) {
            onResult(null, "Authorization token is missing. Please log in again.")
            return
        }

        viewModelScope.launch {
            repository.uploadImageToAI(token, imageFile) { response, error ->
                if (response != null) {
                    onResult(response, null)
                } else {
                    onResult(null, error)
                }
            }
        }
    }

}