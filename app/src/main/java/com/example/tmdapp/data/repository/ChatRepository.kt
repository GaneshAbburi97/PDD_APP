package com.example.tmdapp.data.repository

import com.example.tmdapp.data.remote.GroqMessage
import com.example.tmdapp.data.remote.GroqRequest
import com.example.tmdapp.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChatRepository {
    private val groqApi = RetrofitClient.groqApiService
    private val systemPrompt = "You are an AI Health Assistant specifically focused on Temporomandibular Joint (TMJ) disorders and general jaw/facial pain wellness. Provide helpful, empathetic, and concise guidance. Always include a disclaimer that you are not a doctor and users should consult a professional for medical advice."
    private val chatHistory = mutableListOf<GroqMessage>()

    suspend fun sendMessage(userMessage: String): String {
        return withContext(Dispatchers.IO) {
            try {
                chatHistory.add(GroqMessage(role = "user", content = userMessage))

                val messages = mutableListOf<GroqMessage>()
                messages.add(GroqMessage(role = "system", content = systemPrompt))
                messages.addAll(chatHistory)

                val request = GroqRequest(
                    model = "llama-3.3-70b-versatile",
                    messages = messages
                )

                val response = groqApi.createChatCompletion(request)
                val assistantMessage = response.choices.firstOrNull()?.message?.content
                    ?: "I'm sorry, I couldn't generate a response."

                chatHistory.add(GroqMessage(role = "assistant", content = assistantMessage))
                assistantMessage
            } catch (e: Exception) {
                chatHistory.removeLastOrNull()
                "AI assistant temporarily unavailable. Error: ${e.message}"
            }
        }
    }
}
