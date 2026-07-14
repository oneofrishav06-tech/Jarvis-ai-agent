package com.example.domain.models

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val role: Role,
    val timestamp: Long = System.currentTimeMillis(),
    val isError: Boolean = false
)

enum class Role {
    USER, MODEL
}
