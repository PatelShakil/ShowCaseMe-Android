package com.techsavvy.showcaseme.data.models.api_response

import com.techsavvy.showcaseme.data.models.UserModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    @SerialName("token")
    val token: String? = null,
    @SerialName("user")
    val user: UserModel? = null
)