package com.techsavvy.showcaseme.data.repo.log

import com.techsavvy.showcaseme.common.Resource
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText

//https://firebase.google.com/docs/crashlytics/customize-crash-reports?platform=android
class FcmLogImpl : FcmLog {
    override suspend fun logException(response: HttpResponse) : Resource.Failure{
            val throwable = toException(response)
//        if(!BuildConfig.DEBUG) {
//            auth.uid?.let { Firebase.crashlytics.setUserId(it) }
//            auth.currentUser?.email?.let { Firebase.crashlytics.log("Production Issue for User - $it") }
//            Firebase.crashlytics.recordException(throwable)
//        }
            return Resource.Failure(
                errorCode = response.status.value,
                message = response.bodyAsText()
            )
    }

    override suspend fun logException(throwable: Exception): Resource.Failure{
//        if(!BuildConfig.DEBUG) {
//            auth.uid?.let { Firebase.crashlytics.setUserId(it) }
//            auth.currentUser?.email?.let { Firebase.crashlytics.log("Production Issue for User - $it") }
//            Firebase.crashlytics.recordException(throwable)
//        }
        return Resource.Failure(
            message = throwable.message.toString()
        )
    }
}

private suspend fun toException(response: HttpResponse): Exception {
    return Exception("HTTP Error: ${response.status.value} - ${response.status.description}. Body: ${response.bodyAsText()}")
}