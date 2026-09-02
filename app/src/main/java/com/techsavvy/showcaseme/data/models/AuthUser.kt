package com.techsavvy.showcaseme.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The `users` row returned by the AapdiWebsite Laravel API
 * (`/api/auth/login`, `/api/auth/register`, `/api/my/profile`).
 *
 * Field names are snake_case on the wire — the legacy [UserModel] used the old
 * ShowCaseMe camelCase schema, so every key silently deserialised to null.
 */
@Serializable
data class AuthUser(
    @SerialName("id")
    val id: Int? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("email")
    val email: String? = null,
    @SerialName("phone")
    val phone: String? = null,
    @SerialName("profile_picture")
    val profilePicture: String? = null,
    @SerialName("state")
    val state: String? = null,
    @SerialName("city")
    val city: String? = null,
    @SerialName("pincode")
    val pincode: String? = null,
    @SerialName("is_admin")
    val isAdmin: Boolean? = null,
    @SerialName("max_sites")
    val maxSites: Int? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
)
