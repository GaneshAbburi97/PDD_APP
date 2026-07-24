package com.example.tmdapp.data.remote

import com.example.tmdapp.data.model.*
import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface TmdApiService {
    @POST("api/auth/google")
    suspend fun googleLogin(@Body request: GoogleLoginRequest): AuthResponse

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @GET("api/auth/profile")
    suspend fun getProfile(): User

    @PUT("api/auth/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): User

    @DELETE("api/auth/profile")
    suspend fun deleteProfile()

    @GET("api/pain")
    suspend fun getPainRecords(): List<PainRecord>

    @POST("api/pain")
    suspend fun addPainRecord(@Body record: PainRecord): PainRecord

    @GET("api/sleep")
    suspend fun getSleepRecords(): List<SleepRecord>

    @POST("api/sleep")
    suspend fun addSleepRecord(@Body record: SleepRecord): SleepRecord

    @GET("api/exercise")
    suspend fun getExerciseRecords(): List<ExerciseRecord>

    @POST("api/exercise")
    suspend fun addExerciseRecord(@Body record: ExerciseRecord): ExerciseRecord

    @GET("api/wellness")
    suspend fun getWellnessRecords(): List<WellnessRecord>

    @POST("api/wellness")
    suspend fun addWellnessRecord(@Body record: WellnessRecord): WellnessRecord

    @GET("api/assessment")
    suspend fun getAssessmentRecords(): List<AssessmentRecord>

    @POST("api/assessment")
    suspend fun addAssessmentRecord(@Body record: AssessmentRecord): AssessmentResponse

    @GET("api/appointments")
    suspend fun getAppointments(): List<Appointment>

    @POST("api/appointments")
    suspend fun bookAppointment(@Body request: BookAppointmentRequest): Appointment

    @POST("api/feedback")
    suspend fun submitFeedback(@Body request: FeedbackRequest): FeedbackResponse
    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): GenericResponse

    @POST("api/auth/verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): GenericResponse

    @POST("api/auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): GenericResponse

    @POST("api/reports/save")
    suspend fun uploadReport(@Body request: ReportUploadRequest): GenericResponse
}

data class GoogleLoginRequest(@SerializedName("idToken") val idToken: String)
data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(val name: String, val email: String, val password: String)
data class ForgotPasswordRequest(val email: String)
data class VerifyOtpRequest(val email: String, val otp: String)
data class ResetPasswordRequest(val email: String, val newPassword: String)
data class ReportUploadRequest(val fileData: String)
data class GenericResponse(val message: String?)
data class AuthResponse(val token: String, val user: User)
data class UpdateProfileRequest(
    val name: String,
    val email: String,
    val profileImagePath: String?,
    val heightCm: Float?,
    val weightKg: Float?
)
data class AssessmentResponse(val id: String, val userId: String)
data class BookAppointmentRequest(
    val doctorName: String,
    val date: String,
    val time: String,
    val reason: String
)
data class FeedbackRequest(val name: String, val message: String)
data class FeedbackResponse(val id: String, val userId: String)
