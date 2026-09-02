package com.techsavvy.showcaseme.data.repo.api.auth

import com.techsavvy.showcaseme.common.Resource
import com.techsavvy.showcaseme.common.Response
import com.techsavvy.showcaseme.data.models.api_request.RegisterRequest
import com.techsavvy.showcaseme.data.models.api_response.JwtVerifyResponse
import com.techsavvy.showcaseme.data.models.api_response.LoginResponse

interface AuthRepo {
    suspend fun login(email: String, password: String): Resource<Response<LoginResponse>>

    suspend fun register(request: RegisterRequest): Resource<Response<LoginResponse>>

    suspend fun jwtVerify(token: String): Resource<Response<JwtVerifyResponse>>

    /** Mails a 6-digit OTP to [email]; the OTP is valid for 10 minutes. */
    suspend fun forgotPassword(email: String): Resource<Response<String?>>

    suspend fun resetPassword(email: String, otp: String, password: String): Resource<Response<String?>>
}
