package com.techsavvy.showcaseme.network

import android.util.Log
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Inject

class ShowCaseMeHttpClient @Inject constructor() {

fun getHttpClient() = HttpClient(Android) {

    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        },
            contentType = ContentType.Application.Json
        )

        engine {
            connectTimeout = TIME_OUT
            socketTimeout = TIME_OUT
        }

    }


    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) {
                Log.v(TAG_KTOR_LOGGER, message)
            }
        }
        level = LogLevel.ALL
    }

    install(ResponseObserver) {
        onResponse { response ->
            Log.d(TAG_HTTP_STATUS_LOGGER, "${response.status.value}")
        }
    }

    install(DefaultRequest) {
        header("Content-Type", "application/json")
//        header("uid",auth.uid.toString())
    }
}

companion object {
    private const val TIME_OUT = 30_000
    private const val TAG_KTOR_LOGGER = "ktor_logger:"
    private const val TAG_HTTP_STATUS_LOGGER = "http_status:"
}
}