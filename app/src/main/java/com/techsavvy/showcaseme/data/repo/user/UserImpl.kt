package com.techsavvy.showcaseme.data.repo.user

import com.techsavvy.showcaseme.common.Resource
import com.techsavvy.showcaseme.common.Response
import com.techsavvy.showcaseme.data.models.UserModel
import com.techsavvy.showcaseme.data.models.api_response.LoginResponse
import com.techsavvy.showcaseme.data.repo.log.FcmLog
import com.techsavvy.showcaseme.network.ApiRoutes
import io.ktor.client.HttpClient
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import java.io.File
import javax.inject.Inject

class UserImpl @Inject constructor(
    private val client: HttpClient,
    private val fcmLog: FcmLog
) : UserRepo {
    override suspend fun checkUserExists(username: String): Resource<Response<String?>> {
        return try {
            Resource.Success(
                client.get {
                    url(ApiRoutes.CHECK_USER_EXISTS + "/$username")
//                    parameter("username", username)
                }.body()
            )
        } catch (e: NoTransformationFoundException) {
            fcmLog.logException(e)
        } catch (e: ResponseException) {
            fcmLog.logException(e)
        } catch (e: ClientRequestException) {
            fcmLog.logException(e)
        } catch (e: ServerResponseException) {
            fcmLog.logException(e)
        } catch (e: Exception) {
            e.printStackTrace()
            fcmLog.logException(e)
        }

    }

    override suspend fun register(
        user: UserModel,
        profilePicture: File
    ): Resource<Response<LoginResponse>> {
        return try {
            Resource.Success(
                client.post {
                    url(ApiRoutes.REGISTER) // Should point to /signup

                    // Set the multipart form data content
                    setBody(
                        MultiPartFormDataContent(
                            formData {
                                // These must match the names in your UserSignupDto
                                append("username", user.username!!)
                                append("name", user.name!!)
                                append("email", user.email!!)
                                append("phone", user.phone!!)
                                append("password", user.password!!)
                                append("bio", user.bio ?: "")

                                // Attach profile picture only if available
                                profilePicture.let { file ->
                                    append(
                                        key = "profilePicture",
                                        value = file.readBytes(),
                                        headers = Headers.build {
                                            append(
                                                HttpHeaders.ContentType,
                                                "image/*"
                                            ) // or detect dynamically
                                            append(
                                                HttpHeaders.ContentDisposition,
                                                "filename=${file.name}"
                                            )
                                        }
                                    )
                                }
                            }
                        )
                    )
                }.body()
            )

        } catch (e: NoTransformationFoundException) {
            fcmLog.logException(e)
        } catch (e: ResponseException) {
            fcmLog.logException(e)
        } catch (e: ClientRequestException) {
            fcmLog.logException(e)
        } catch (e: ServerResponseException) {
            fcmLog.logException(e)
        } catch (e: Exception) {
            e.printStackTrace()
            fcmLog.logException(e)
        }
    }

}