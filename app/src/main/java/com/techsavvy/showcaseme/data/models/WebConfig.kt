package com.techsavvy.showcaseme.data.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WebConfig(
    @SerialName("bgImage")
    val bgImage: String? = null,
    @SerialName("bgType")
    val bgType: String? = null,
    @SerialName("createdAt")
    val createdAt: String? = null,
    @SerialName("endGradient")
    val endGradient: String? = null,
    @SerialName("gradientType")
    val gradientType: String? = null,
    @SerialName("startGradient")
    val startGradient: String? = null,
    @SerialName("updatedAt")
    val updatedAt: String? = null,
    @SerialName("user")
    val user: UserModel? = null,
    @SerialName("userId")
    val userId: Int? = null,
    @SerialName("webConfigId")
    val webConfigId: Int? = null,
    @SerialName("webStyle")
    val webStyle: String? = null
)