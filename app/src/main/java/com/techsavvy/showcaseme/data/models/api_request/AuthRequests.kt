package com.techsavvy.showcaseme.data.models.api_request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** POST /api/auth/login */
@Serializable
data class LoginRequest(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
)

/**
 * POST /api/auth/register — mirrors AuthController::register exactly.
 *
 * name       required, max 100
 * email      required, email, max 150, unique
 * phone      optional, max 20, unique
 * password   required, min 6, max 100
 * state/city optional, max 100
 * pincode    optional, max 10
 */
@Serializable
data class RegisterRequest(
    @SerialName("name") val name: String,
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
    @SerialName("phone") val phone: String? = null,
    @SerialName("state") val state: String? = null,
    @SerialName("city") val city: String? = null,
    @SerialName("pincode") val pincode: String? = null,
)

/** POST /api/auth/forgot-password — mails a 6-digit OTP valid for 10 minutes. */
@Serializable
data class ForgotPasswordRequest(
    @SerialName("email") val email: String,
)

/** POST /api/auth/reset-password */
@Serializable
data class ResetPasswordRequest(
    @SerialName("email") val email: String,
    @SerialName("otp") val otp: String,
    @SerialName("password") val password: String,
)

/**
 * POST /api/my/devices — registers this install so the API can push to it.
 *
 * Sent on every launch once signed in, and again whenever Firebase rotates the
 * token. The API upserts on the token, so repeating it is harmless.
 */
@Serializable
data class RegisterDeviceRequest(
    @SerialName("token") val token: String,
    @SerialName("platform") val platform: String = "android",
    @SerialName("device_label") val deviceLabel: String? = null,
)

/** DELETE /api/my/devices — stops notifications for one install. */
@Serializable
data class UnregisterDeviceRequest(
    @SerialName("token") val token: String,
)
