package com.techsavvy.showcaseme.data.repo.log

import com.techsavvy.showcaseme.common.Resource
import io.ktor.client.statement.HttpResponse

interface FcmLog {
    suspend fun logException(response: HttpResponse): Resource.Failure

    suspend fun logException(throwable: Exception):Resource.Failure
}