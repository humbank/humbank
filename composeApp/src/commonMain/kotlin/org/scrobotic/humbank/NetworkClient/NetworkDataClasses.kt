package org.scrobotic.humbank.NetworkClient

import kotlinx.serialization.Serializable

@Serializable
data class LoginOut(
    val username: String,
    val pin: String
)

@Serializable
data class LoginIn(
    val token: String,
    val username: String
)
