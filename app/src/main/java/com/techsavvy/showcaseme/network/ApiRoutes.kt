package com.techsavvy.showcaseme.network

import com.techsavvy.showcaseme.common.URLS

object ApiRoutes {
    private const val BASE_URL = URLS.API_URL

    //Auth
    const val LOGIN = "${BASE_URL}auth/login"
    const val REGISTER = "${BASE_URL}auth/register"
    const val JWT_VERIFY = "${BASE_URL}auth/jwt-verify"


}
