package com.example.miniplayer

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.toMutableStateList

class ChatUiState (title:String, initialMessages:List<Message>){
    private val _messages: MutableList<Message> = initialMessages.toMutableStateList()
    val messages:List<Message> = _messages

    val title:String=title

    fun addMessage(msg: Message){
        _messages.add(msg)
    }
}

enum class MessageType{
    MESSAGE_TYPE_REPLY,
    MESSAGE_TYPE_USER,
    MESSAGE_TYPE_MUSIC_PLAYER,
}

@Immutable
data class Message (
    val type: MessageType,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
)
