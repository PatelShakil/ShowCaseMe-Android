package com.techsavvy.showcaseme.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Response<T>(
    @SerialName("status")
    val status: Boolean,
    @SerialName("message")
    val message: String,
    @SerialName("data")
    val data : T? = null
)