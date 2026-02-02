package org.scrobotic.humbank.NetworkClient

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable
sealed class NetworkResult<out T> {

    @Serializable
    data class Success<T>(val data: T) : NetworkResult<T>()


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
            val error = bodyAsText()
            NetworkResult.Failure(ErrorHandler(error))
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
        NetworkResult.Failure(e.message.toString())
    }
}
