package com.techsavvy.showcaseme.data.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserSetting(
    @SerialName("allowGallery")
    val allowGallery: Boolean? = null,
    @SerialName("allowProducts")
    val allowProducts: Boolean? = null,
    @SerialName("allowSocialLinks")
    val allowSocialLinks: Boolean? = null,
    @SerialName("blockReason")
    val blockReason: String? = null,
    @SerialName("blockedUntil")
    val blockedUntil: String? = null,
    @SerialName("createdAt")
    val createdAt: String? = null,
    @SerialName("isBlocked")
    val isBlocked: Boolean? = null,
    @SerialName("maxTabsAllowed")
    val maxTabsAllowed: Int? = null,
    @SerialName("settingId")
    val settingId: Int? = null,
    @SerialName("updatedAt")
    val updatedAt: String? = null,
    @SerialName("user")
    val user: UserModel? = null,
    @SerialName("userId")
    val userId: Int? = null
)