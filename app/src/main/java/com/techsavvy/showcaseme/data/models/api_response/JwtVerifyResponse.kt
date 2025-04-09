package com.techsavvy.showcaseme.data.models.api_response

import com.techsavvy.showcaseme.data.models.UserModel
import com.techsavvy.showcaseme.data.models.WebConfig
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JwtVerifyResponse(
    @SerialName("token")
    val token: String? = null,
    @SerialName("user")
    val user: UserModel? = null,
    @SerialName("webConfig")
    val webConfig: WebConfig? = null
)

