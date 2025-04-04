package ru.yandexpraktikum.myandroidchat.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.json.JSONObject
import ru.yandexpraktikum.myandroidchat.data.local.MessageDao
import ru.yandexpraktikum.myandroidchat.data.local.MessageEntity
import ru.yandexpraktikum.myandroidchat.data.websocket.ChatWebSocketClient
import ru.yandexpraktikum.myandroidchat.data.websocket.ConnectionStatus
import ru.yandexpraktikum.myandroidchat.domain.model.Message
import ru.yandexpraktikum.myandroidchat.domain.repository.ChatRepository
import java.util.UUID
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val webSocketClient: ChatWebSocketClient,
    private val messageDao: MessageDao
) : ChatRepository {

    init {
        webSocketClient.messageFlow
            .map { messageStr ->
                val json = JSONObject(messageStr)
                Message(
                    id = json.getString("id"),
                    content = json.getString("content"),
                    sender = json.getString("sender"),
                    timestamp = json.getLong("timestamp")
                )
            }
            .onEach { message ->
                messageDao.insertMessage(MessageEntity.fromMessage(message))
                messageDao.deleteOldMessages()
            }
    }

    override fun connect(url: String) {
        webSocketClient.connect(url)
    }

    override fun disconnect() {
        webSocketClient.disconnect()
    }

    override fun sendMessage(message: String): Boolean {
        val messageJson = JSONObject().apply {
            put("id", UUID.randomUUID().toString())
            put("content", message)
            put("sender", "user")
            put("timestamp", System.currentTimeMillis())
        }
        return webSocketClient.sendMessage(messageJson.toString())
    }

    override fun observeMessages(): Flow<List<Message>> {
        return messageDao.getRecentMessages()
            .map { entities ->
                entities.map { it.toMessage() }
                    .sortedBy { it.timestamp }
            }
    }

    override fun observeConnectionStatus(): Flow<ConnectionStatus> {
        return webSocketClient.connectionStatus
    }
} 