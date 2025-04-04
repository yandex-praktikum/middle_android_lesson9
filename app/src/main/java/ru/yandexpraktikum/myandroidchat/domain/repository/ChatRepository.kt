package ru.yandexpraktikum.myandroidchat.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.yandexpraktikum.myandroidchat.data.websocket.ConnectionStatus
import ru.yandexpraktikum.myandroidchat.domain.model.Message

interface ChatRepository {
    fun connect(url: String)
    fun disconnect()
    fun sendMessage(message: String): Boolean
    fun observeMessages(): Flow<List<Message>>
    fun observeConnectionStatus(): Flow<ConnectionStatus>
} 
