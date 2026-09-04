package com.techsavvy.showcaseme.data.repo.api.device

import com.techsavvy.showcaseme.common.Resource
import com.techsavvy.showcaseme.common.Response

interface DeviceRepo {

    /** Tell the API this install can receive notifications. */
    suspend fun register(
        authToken: String,
        fcmToken: String,
        deviceLabel: String?,
    ): Resource<Response<Unit?>>

    /** Stop notifications for this install, used on sign out. */
    suspend fun unregister(authToken: String, fcmToken: String): Resource<Response<Unit?>>
}
