package com.techsavvy.showcaseme.data.repo.api.auth

import com.techsavvy.showcaseme.common.ApiError
import com.techsavvy.showcaseme.common.Resource
import com.techsavvy.showcaseme.common.Response
import com.techsavvy.showcaseme.data.models.api_request.ForgotPasswordRequest
import com.techsavvy.showcaseme.data.models.api_request.LoginRequest
import com.techsavvy.showcaseme.data.models.api_request.RegisterRequest
import com.techsavvy.showcaseme.data.models.api_request.ResetPasswordRequest
import com.techsavvy.showcaseme.data.models.api_response.JwtVerifyResponse
import com.techsavvy.showcaseme.data.models.api_response.LoginResponse
import com.techsavvy.showcaseme.data.repo.log.FcmLog
import com.techsavvy.showcaseme.network.ApiRoutes
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import javax.inject.Inject

class AuthImpl @Inject constructor(
    private val client: HttpClient,
    private val fcmLog: FcmLog
) : AuthRepo {

    override suspend fun login(
        email: String,
        password: String
    ): Resource<Response<LoginResponse>> = call {
        client.post {
            url(ApiRoutes.LOGIN)
            setBody(LoginRequest(email = email, password = password))
        }
    }

    override suspend fun register(request: RegisterRequest): Resource<Response<LoginResponse>> = call {
        client.post {
            url(ApiRoutes.REGISTER)
            setBody(request)
        }
    }

    override suspend fun jwtVerify(token: String): Resource<Response<JwtVerifyResponse>> = call {
        client.post {
            url(ApiRoutes.JWT_VERIFY)
            header("Authorization", "Bearer $token")
        }
    }

    override suspend fun forgotPassword(email: String): Resource<Response<String?>> = call {
        client.post {
            url(ApiRoutes.FORGOT_PASSWORD)
            setBody(ForgotPasswordRequest(email = email))
        }
    }

    override suspend fun resetPassword(
        email: String,
        otp: String,
        password: String
    ): Resource<Response<String?>> = call {
        client.post {
            url(ApiRoutes.RESET_PASSWORD)
            setBody(ResetPasswordRequest(email = email, otp = otp, password = password))
        }
    }

    /**
     * Runs [request] and maps it onto [Resource].
     *
     * The API answers failures with a real HTTP status (400/401/422/500) and an
     * `ApiResponse::fail` body. Decoding those straight into `Response<T>` broke
     * on 422 because `data` carries the validator bag, so the status is checked
     * first and only `message` is read off the error path — that message is
     * already written for end users.
     */
    private suspend inline fun <reified T> call(
        request: () -> HttpResponse
    ): Resource<Response<T>> = try {
        val response = request()
        if (response.status.isSuccess()) {
            Resource.Success(response.body())
        } else {
            val message = runCatching { response.body<ApiError>().message }
                .getOrNull()
                ?.takeIf { it.isNotBlank() }
                ?: "Something went wrong (${response.status.value}). Please try again."
            Resource.Failure(errorCode = response.status.value, message = message)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        fcmLog.logException(e)
    }
}
