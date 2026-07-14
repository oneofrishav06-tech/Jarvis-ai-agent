package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.models.Role

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val text: String,
    val role: Role,
    val timestamp: Long,
    val isError: Boolean
)
