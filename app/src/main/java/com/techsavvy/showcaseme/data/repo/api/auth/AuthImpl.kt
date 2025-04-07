package com.techsavvy.showcaseme.data.repo.api.auth

import com.techsavvy.showcaseme.common.Resource
import com.techsavvy.showcaseme.common.Response
import com.techsavvy.showcaseme.data.models.api_response.LoginResponse
import com.techsavvy.showcaseme.data.repo.log.FcmLog
import com.techsavvy.showcaseme.network.ApiRoutes
import io.ktor.client.HttpClient
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.url
import javax.inject.Inject


class AuthImpl @Inject constructor(
    private val client : HttpClient,
    private val fcmLog : FcmLog
) : AuthRepo {

    override suspend fun login(
        email: String,
        password: String
    ): Resource<Response<LoginResponse>> {
        return try {
            Resource.Success(client.post {
                url(ApiRoutes.LOGIN)
                parameter("email", email)
                parameter("password", password)
            }.body()
            )
        }catch (e : NoTransformationFoundException){
            fcmLog.logException(e)
        }
        catch (e: ResponseException) {
            fcmLog.logException(e)
        } catch (e: ClientRequestException) {
            fcmLog.logException(e)
        } catch (e: ServerResponseException) {
            fcmLog.logException(e)
        } catch (e: Exception) {
            e.printStackTrace()
            fcmLog.logException(e)
        }    }

    override suspend fun register(
        name: String,
        email: String,
        password: String
    ): Resource<Response<LoginResponse>> {
        TODO("Not yet implemented")
    }
}