package org.scrobotic.humbank.NetworkClient

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException

@Serializable
sealed class NetworkResult<out T> {
    // This subclass is correctly annotated.
    @Serializable
    data class Success<T>(val data: T) : NetworkResult<T>()

    // This subclass is also correctly annotated.
    @Serializable
    data class Failure(val errorMessage: String) : NetworkResult<Nothing>()
}

suspend inline fun <reified T> HttpResponse.handleResponse(): NetworkResult<T> {
    return when (status.value) {
        in 200..299 -> {
            try {
                // Check if the expected type is Unit, which has no body to parse.
                val result = if (T::class == Unit::class) Unit as T else body<T>()
                NetworkResult.Success(result)
            } catch (e: SerializationException) {
                // This catch block is crucial for debugging serialization issues.
                NetworkResult.Failure("Serialization error: ${e.message}")
            }
        }
        else -> {
            val errorBody = bodyAsText()
            NetworkResult.Failure("Error ${status.value}: $errorBody")
        }
    }
}

suspend inline fun <reified T> HttpClient.safeRequest(
    crossinline block: suspend HttpClient.() -> HttpResponse
): NetworkResult<T> {
    return try {
        val response = block()
        response.handleResponse<T>()
    } catch (e: Exception) {
        // This catch block will wrap any exception, including the serialization one.
        NetworkResult.Failure("Network error: ${e.message}")
    }
}
