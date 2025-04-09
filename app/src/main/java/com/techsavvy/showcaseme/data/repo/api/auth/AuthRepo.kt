package com.techsavvy.showcaseme.data.repo.api.auth

import com.techsavvy.showcaseme.common.Resource
import com.techsavvy.showcaseme.common.Response
import com.techsavvy.showcaseme.data.models.api_response.JwtVerifyResponse
import com.techsavvy.showcaseme.data.models.api_response.LoginResponse

interface AuthRepo {
    suspend fun login(email: String, password: String): Resource<Response<LoginResponse>>
    suspend fun register(name: String, email: String, password: String): Resource<Response<LoginResponse>>

    suspend fun jwtVerify(token: String): Resource<Response<JwtVerifyResponse>>
}