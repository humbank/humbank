package org.scrobotic.humbank.NetworkClient

import androidx.compose.foundation.layout.ContextualFlowRow
import io.ktor.client.*
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType.Application
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.internal.readJson


val token = ""

fun createNetworkClient(): HttpClient {
    return HttpClient {
        install(HttpRequestRetry){
            retryOnServerErrors(maxRetries = 2)
            exponentialDelay()
        }
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                ignoreUnknownKeys = true
            })
        }
        install(Logging) {
            level = LogLevel.ALL
            logger = object : Logger {
                override fun log(message: String) {
                    println("KtorLogger: $message")
                }
            }
        }
        install(HttpTimeout) {
            socketTimeoutMillis = 3000
            requestTimeoutMillis = 3000
            connectTimeoutMillis = 3000
        }
        // Configure default request parameters
        defaultRequest {
            url{
                protocol= URLProtocol.HTTPS
                host = "humbank.cv"
            }
            headers{
                append(HttpHeaders.Authorization, "Bearer $token")
            }

        }
    }
}