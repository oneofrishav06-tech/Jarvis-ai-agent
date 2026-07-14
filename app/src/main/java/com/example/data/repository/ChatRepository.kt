package com.example.data.repository

import com.example.data.local.ChatDao
import com.example.data.local.ChatMessageEntity
import com.example.data.remote.Content
import com.example.data.remote.GenerateContentRequest
import com.example.data.remote.GenerationConfig
import com.example.data.remote.Part
import com.example.data.remote.RetrofitClient
import com.example.domain.models.ChatMessage
import com.example.domain.models.Role
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ChatRepository(
    private val chatDao: ChatDao,
    private val settingsRepository: SettingsRepository
) {

    val chatHistory: Flow<List<ChatMessage>> = chatDao.getAllMessages().map { entities ->
        entities.map { it.toDomainModel() }
    }

    suspend fun sendMessage(text: String, currentHistory: List<ChatMessage>): ChatMessage {
        val apiKey = settingsRepository.apiKeyFlow.first()
        if (apiKey.isBlank()) {
            throw Exception("API Key not configured. Please set it in Settings.")
        }

        // Add user message to DB
        val userMsg = ChatMessage(text = text, role = Role.USER)
        chatDao.insertMessage(userMsg.toEntity())

        // Build request history
        val contents = currentHistory.map {
            Content(
                role = if (it.role == Role.USER) "user" else "model",
                parts = listOf(Part(text = it.text))
            )
        } + Content(role = "user", parts = listOf(Part(text = text)))

        val request = GenerateContentRequest(
            contents = contents,
            systemInstruction = Content(
                parts = listOf(Part(text = "You are Jarvis, a highly advanced, premium, concise, and helpful Personal AI Assistant. Speak professionally and intelligently, much like Tony Stark's AI."))
            ),
            generationConfig = GenerationConfig(temperature = 0.7f)
        )

        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.service.generateContent(apiKey, request)
                val replyText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "I am unable to process that request at this time."
                
                val modelMsg = ChatMessage(text = replyText, role = Role.MODEL)
                chatDao.insertMessage(modelMsg.toEntity())
                modelMsg
            } catch (e: Exception) {
                val errorMsg = ChatMessage(text = "Error communicating with servers: ${e.localizedMessage}", role = Role.MODEL, isError = true)
                chatDao.insertMessage(errorMsg.toEntity())
                throw e
            }
        }
    }

    suspend fun clearHistory() {
        chatDao.clearHistory()
    }
}

fun ChatMessageEntity.toDomainModel() = ChatMessage(
    id = id,
    text = text,
    role = role,
    timestamp = timestamp,
    isError = isError
)

fun ChatMessage.toEntity() = ChatMessageEntity(
    id = id,
    text = text,
    role = role,
    timestamp = timestamp,
    isError = isError
)
