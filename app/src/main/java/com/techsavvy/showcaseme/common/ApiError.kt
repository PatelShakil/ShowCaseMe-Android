package com.techsavvy.showcaseme.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Non-2xx envelope from `App\Helpers\ApiResponse::fail`.
 *
 * `data` is deliberately absent here: on a 422 the API puts the Laravel
 * validator bag in it, which does not fit `Response<LoginResponse>` and used to
 * blow up deserialisation into a generic Ktor failure. Only `message` is
 * user-facing, and it already reads well ("The email has already been taken.").
 */
@Serializable
data class ApiError(
    @SerialName("status")
    val status: Boolean = false,
    @SerialName("message")
    val message: String = "",
)
