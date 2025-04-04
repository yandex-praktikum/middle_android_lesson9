package ru.yandexpraktikum.myandroidchat.data.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import ru.yandexpraktikum.myandroidchat.data.websocket.ConnectionStatus
import ru.yandexpraktikum.myandroidchat.domain.model.Message
import ru.yandexpraktikum.myandroidchat.domain.repository.ChatRepository
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.random.Random

/**
 * Фейковый репозиторий для чата.
 * Используется для демонстрации функционала без необходимости запуска реального веб-сокета.
 * @property executorService Сервис для выполнения задач по расписанию (чтобы симулировать получение случайных сообщений)
 */
class FakeChatRepositoryImpl @Inject constructor() : ChatRepository {

    private val executorService: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()

    private val _messages = MutableStateFlow(
        emptyList<Message>()
    )
    private val messages: Flow<List<Message>> = _messages.asStateFlow()

    private val messageList = mutableListOf<Message>()

    init {
        val list = listOf(
            Message(
                id = "1",
                content = "Привет!",
                sender = "user",
                timestamp = System.currentTimeMillis()
            ),
            Message(
                id = "2",
                content = "Привет",
                sender = "bot",
                timestamp = System.currentTimeMillis()
            ),
            Message(
                id = "3",
                content = "Как дела?",
                sender = "user",
                timestamp = System.currentTimeMillis()
            ),
            Message(
                id = "4",
                content = "Норм! Как у тебя?",
                sender = "bot",
                timestamp = System.currentTimeMillis()
            )
        )

        messageList.addAll(list)
        _messages.value = messageList.toList()
        startSendingPeriodicMessages()
    }

    private fun startSendingPeriodicMessages() {
        executorService.scheduleWithFixedDelay({
            val newMessage = Message(
                id = (messageList.size + 1).toString(),
                content = randomAnnoyingMessage(),
                sender = "bot",
                timestamp = System.currentTimeMillis()
            )

            messageList.add(newMessage)
                _messages.tryEmit(messageList.toList())
            Log.d("FakeChatRepository", "Sent periodic message: ${newMessage.content}")
        }, 5, 5, TimeUnit.SECONDS)
    }

    override fun connect(url: String) {
        Log.d("FakeChatRepository", "Connecting to fake url")
    }

    override fun disconnect() {
        Log.d("FakeChatRepository", "Disconnecting from fake url")
    }

    override fun sendMessage(message: String): Boolean {
        val newMessage = Message(
            id = (messageList.size + 1).toString(),
            content = message,
            sender = "user",
            timestamp = System.currentTimeMillis()
        )
        messageList.add(newMessage)
        _messages.tryEmit(messageList.toList())
        return true
    }

    override fun observeMessages(): Flow<List<Message>> {
        return messages
    }

    override fun observeConnectionStatus(): Flow<ConnectionStatus> {
        return flow {
            emit(ConnectionStatus.CONNECTED)
        }
    }

    private fun randomAnnoyingMessage(): String {
        val messages = listOf(
            "Вы где",
            "Бросили",
            "Вы бросили меня",
            "Бросили",
            "Доброе утро",
            "Бросили",
            "Ответьте",
            "Бросили")

        val randomIndex = Random.nextInt(messages.size)
        return messages[randomIndex]
    }
}