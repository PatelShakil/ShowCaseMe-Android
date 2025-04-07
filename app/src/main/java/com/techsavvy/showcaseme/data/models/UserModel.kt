package com.techsavvy.showcaseme.data.models


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserModel(
//    @SerialName("achievements")
//    val achievements: List<Any?>? = null,
//    @SerialName("announcements")
//    val announcements: List<Any?>? = null,
    @SerialName("bio")
    val bio: String? = null,
//    @SerialName("blogs")
//    val blogs: List<Any?>? = null,
    @SerialName("createdAt")
    val createdAt: String? = null,
    @SerialName("email")
    val email: String? = null,
//    @SerialName("events")
//    val events: List<Any?>? = null,
//    @SerialName("forms")
//    val forms: List<Any?>? = null,
//    @SerialName("galleries")
//    val galleries: List<Any?>? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("password")
    val password: String? = null,
    @SerialName("phone")
    val phone: String? = null,
//    @SerialName("productCategories")
//    val productCategories: List<Any?>? = null,
//    @SerialName("products")
//    val products: List<Any?>? = null,
    @SerialName("profilePicture")
    val profilePicture: String? = null,
//    @SerialName("projects")
//    val projects: List<Any?>? = null,
//    @SerialName("skills")
//    val skills: List<Any?>? = null,
//    @SerialName("socialLinks")
//    val socialLinks: List<Any?>? = null,
    @SerialName("updatedAt")
    val updatedAt: String? = null,
    @SerialName("userId")
    val userId: Int? = null,
    @SerialName("userSetting")
    val userSetting: UserSetting? = null,
//    @SerialName("userSubscriptions")
//    val userSubscriptions: List<Any?>? = null,
//    @SerialName("userTabs")
//    val userTabs: List<Any?>? = null,
    @SerialName("username")
    val username: String? = null,
    @SerialName("webConfig")
    val webConfig: WebConfig? = null
)