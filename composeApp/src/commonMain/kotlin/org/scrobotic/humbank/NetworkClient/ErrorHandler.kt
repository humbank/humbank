package org.scrobotic.humbank.NetworkClient

import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

fun ErrorHandler(errorBody: String): String{
    val errorMessage = try {
        val json = Json.parseToJsonElement(errorBody).jsonObject
        json["Error"]?.jsonPrimitive?.content ?: "Unknown error"
    } catch (e: Exception) {
        errorBody
    }
    return errorMessage
}