package com.techsavvy.showcaseme.data.repo.user

import com.techsavvy.showcaseme.common.Resource
import com.techsavvy.showcaseme.common.Response
import com.techsavvy.showcaseme.data.models.UserModel
import com.techsavvy.showcaseme.data.models.api_response.LoginResponse
import java.io.File

interface UserRepo {
//    suspend fun getUserDetails(token : String): Resource

    suspend fun checkUserExists(username : String): Resource<Response<String?>>
    suspend fun register(user : UserModel,profilePicture : File): Resource<Response<LoginResponse>>
}