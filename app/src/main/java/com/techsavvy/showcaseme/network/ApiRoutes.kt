package com.techsavvy.showcaseme.network

import com.techsavvy.showcaseme.common.URLS

object ApiRoutes {
    private const val BASE_URL = URLS.API_URL

    // Auth — the first-party AapdiWebsite endpoints (routes/api.php, `auth` prefix)
    const val LOGIN = "${BASE_URL}auth/login"
    const val REGISTER = "${BASE_URL}auth/register"
    const val JWT_VERIFY = "${BASE_URL}auth/verify"
    const val FORGOT_PASSWORD = "${BASE_URL}auth/forgot-password"
    const val RESET_PASSWORD = "${BASE_URL}auth/reset-password"

    // Profile
    const val PROFILE = "${BASE_URL}my/profile"

    // Push notification devices
    const val DEVICES = "${BASE_URL}my/devices"

    // The legacy /user/check-user-exists and /user/signup shim is intentionally
    // absent: signup now goes through REGISTER, the only endpoint that persists
    // every field it is sent. The API keeps those routes for older installed APKs.
}
