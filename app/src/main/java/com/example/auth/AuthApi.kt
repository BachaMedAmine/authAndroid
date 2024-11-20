package com.example.auth

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.auth.RetrofitInstance.api
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

data class LoginRequest(val email: String, val password: String)
data class ForgotPasswordRequest(val email: String)
data class VerifyOtpRequest(val email: String, val otp: String, val newPassword: String)
data class ApiResponse(val message: String,val car: CarDetails? = null) {

}
class ImageViewModel : ViewModel() {
    var selectedImageUri: Uri? = null
}

data class ResetPasswordRequest(
    val newPassword: String,
    val confirmPassword: String
)

data class OtpRequest(val otp: String)
data class OtpResponse(val message: String, val accessToken: String)



data class RegisterRequest(
    val email: String,
    val password: String,
    val confirmPassword: String,
    val name: String
)

data class AuthResponse(
    val email: String? = null,
    val password: String? = null,
    val name: String? = null,
    val role: String? = null,
    val _id: String? = null,
    val refreshTokens: List<String>? = null,
    val token: String? = null, // Add token here

        val access_token: String,
        val refresh_token: String,
    val car: CarDetails? = null
)

data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)

data class CarDetails(
    val make: String,
    val carModel: String,
    val year: Int,
    val mileage: Int,
    val owner: String,
    val _id: String,
    val __v: Int // Added for completeness based on your response JSON
)

data class Car(
    val make: String,
    val carModel: String,
    val year: Int,
    val mileage: Int,
    val owner: String,
    val _id: String
)


interface AuthApi {
    @POST("auth/login")
    fun login(@Body request: LoginRequest): Call<AuthResponse>

    @POST("auth/signup")
    fun register(@Body request: RegisterRequest): Call<AuthResponse>

    @POST("auth/forgot-password")
    fun sendOtp(@Body request: ForgotPasswordRequest): Call<ApiResponse>


    @PUT("auth/verify-otp")
    suspend fun verifyOtp(@Body otpRequest: OtpRequest): OtpResponse


    @POST("auth/reset-password")
    suspend fun resetPassword(
        @Header("Authorization") token: String,
        @Body resetPasswordRequest: ResetPasswordRequest
    ): Response<ApiResponse>





    @PUT("/auth/change-password")
    suspend fun changePassword(
        @Header("Authorization") token: String,
        @Body request: ChangePasswordRequest
    ): Response<ApiResponse>


    @POST("auth/google/token")
    fun googleSignIn(@Body request: GoogleSignInRequest): Call<AuthResponse>

    @POST("/auth/google-signup")
    fun googleSignUp(@Body request: GoogleSignUpRequest): Call<AuthResponse>


    @POST("cars/upload-image")
    @Multipart
    fun uploadCarImage(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part
    ): Call<ApiResponse>


    @GET("cars")
    fun getUploadedCars(
        @Header("Authorization") token: String
    ): Call<List<Car>>
}