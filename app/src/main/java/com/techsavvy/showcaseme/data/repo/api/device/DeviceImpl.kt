package com.techsavvy.showcaseme.data.repo.api.device

import com.techsavvy.showcaseme.common.ApiError
import com.techsavvy.showcaseme.common.Resource
import com.techsavvy.showcaseme.common.Response
import com.techsavvy.showcaseme.data.models.api_request.RegisterDeviceRequest
import com.techsavvy.showcaseme.data.models.api_request.UnregisterDeviceRequest
import com.techsavvy.showcaseme.data.repo.log.FcmLog
import com.techsavvy.showcaseme.network.ApiRoutes
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import javax.inject.Inject

class DeviceImpl @Inject constructor(
    private val client: HttpClient,
    private val fcmLog: FcmLog,
) : DeviceRepo {

    override suspend fun register(
        authToken: String,
        fcmToken: String,
        deviceLabel: String?,
    ): Resource<Response<Unit?>> = call {
        client.post {
            url(ApiRoutes.DEVICES)
            header("Authorization", "Bearer $authToken")
            setBody(RegisterDeviceRequest(token = fcmToken, deviceLabel = deviceLabel))
        }
    }

    override suspend fun unregister(
        authToken: String,
        fcmToken: String,
    ): Resource<Response<Unit?>> = call {
        client.delete {
            url(ApiRoutes.DEVICES)
            header("Authorization", "Bearer $authToken")
            setBody(UnregisterDeviceRequest(token = fcmToken))
        }
    }

    /** Same shape as AuthImpl.call: status first, then the user-facing message. */
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
                ?: "Could not register this device (${response.status.value})."
            Resource.Failure(errorCode = response.status.value, message = message)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        fcmLog.logException(e)
    }
}
