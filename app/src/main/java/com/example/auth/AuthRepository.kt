// AuthRepository.kt
package com.example.auth

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.content.Context
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


class AuthRepository() {
    private val api = RetrofitInstance.api



    fun saveToken(context: Context, token: String) {
        val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("auth_token", token).apply()
    }

    fun saveRefreshToken(context: Context, token: String) {
        val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("refresh_token", token).apply()
    }

    fun getToken(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("auth_token", null)
    }







    // Example usage of saving token during login
    fun login(email: String, password: String, context: Context, onResult: (AuthResponse?, String?) -> Unit) {
        val request = LoginRequest(email, password)
        api.login(request).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful) {
                    val authResponse = response.body()
                    authResponse?.let {
                        // Save the access token
                        saveToken(context, it.access_token ?: "")
                    }
                    onResult(authResponse, null)
                } else {
                    onResult(null, "Login failed: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                onResult(null, t.message ?: "Unknown error occurred")
            }
        })
    }

    fun register(request: RegisterRequest, onResult: (AuthResponse?, String?) -> Unit) {
        api.register(request).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        onResult(responseBody, null)
                    } else {
                        onResult(null, "Registration successful but response body is empty")
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    Log.d("RegisterRequest", "Request data: $request")
                    onResult(null, "Registration failed: $errorBody")
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                onResult(null, t.message ?: "Unknown error")
                Log.e("RegisterError", "Request failed: ${t.message}")
            }
        })
    }

    fun googleSignIn(idToken: String, onResult: (AuthResponse?, String?) -> Unit) {
        val request = GoogleSignInRequest(idToken)
        RetrofitInstance.api.googleSignIn(request).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful) {
                    onResult(response.body(), null)
                } else {
                    onResult(null, response.message())
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                onResult(null, t.message)
            }
        })
    }



    fun googleSignUp(idToken: String, onResult: (AuthResponse?, String?) -> Unit) {
        val request = GoogleSignUpRequest(idToken)
        api.googleSignUp(request).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                onResult(response.body(), if (response.isSuccessful) null else response.message())
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                onResult(null, t.message)
            }
        })
    }



    fun sendOtp(email: String, onResult: (String?, String?) -> Unit) {
        val request = ForgotPasswordRequest(email) // Email payload
        RetrofitInstance.api.sendOtp(request).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val message = response.body()?.message
                    Log.d("SendOtp", "OTP sent successfully: $message") // Debug log
                    onResult(message, null)
                } else {
                    Log.e("SendOtp", "Error: ${response.message()}")
                    onResult(null, response.message())
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Log.e("SendOtp", "Error sending OTP: ${t.message}") // Error log
                onResult(null, t.message)
            }
        })
    }

    suspend fun verifyOtp(otp: String): OtpResponse {
        val otpRequest = OtpRequest(otp) // Ensure this is constructed correctly
        return api.verifyOtp(otpRequest) // Pass the object here
    }


    suspend fun resetPassword(token: String, resetPasswordRequest: ResetPasswordRequest): Result<String> {
        return try {
            val response = api.resetPassword("Bearer $token", resetPasswordRequest)
            if (response.isSuccessful) {
                Result.success(response.body()?.message ?: "Password reset successful")
            } else {
                val error = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Failed to reset password: $error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun changePassword(token: String, request: ChangePasswordRequest): Result<String> {
        return try {
            val response = api.changePassword("Bearer $token", request)
            if (response.isSuccessful) {
                Result.success(response.body()?.message ?: "Password changed successfully")
            } else {
                val error = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Failed to change password: $error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    fun uploadCarImage(token: String, imageFile: File, onResult: (CarDetails?, String?) -> Unit) {
        val requestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestBody)

        RetrofitInstance.api.uploadCarImage("Bearer $token", imagePart).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val carDetails = response.body()?.car
                    onResult(carDetails, null) // Pass car details back to the caller
                } else {
                    onResult(null, "Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                onResult(null, t.message)
            }
        })
    }

    fun getUploadedCars(token: String, onResult: (List<Car>?, String?) -> Unit) {
        api.getUploadedCars("Bearer $token").enqueue(object : Callback<List<Car>> {
            override fun onResponse(call: Call<List<Car>>, response: Response<List<Car>>) {
                if (response.isSuccessful) {
                    onResult(response.body(), null)
                } else {
                    onResult(null, "Failed: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Car>>, t: Throwable) {
                onResult(null, t.message)
            }
        })
    }

    fun uploadImageToAI(token: String, imageFile: File, onResult: (String?, String?) -> Unit) {
        val requestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestBody)

        RetrofitInstance.api.uploadCarImage("Bearer $token", imagePart).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                Log.d("AuthRepository", "Response code: ${response.code()}")
                Log.d("AuthRepository", "Response body: ${response.body()}")
                if (response.isSuccessful) {
                    val aiResponseMessage = response.body()?.message
                    onResult(aiResponseMessage, null)
                } else {
                    onResult(null, "Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                onResult(null, t.message)
            }
        })
    }


}