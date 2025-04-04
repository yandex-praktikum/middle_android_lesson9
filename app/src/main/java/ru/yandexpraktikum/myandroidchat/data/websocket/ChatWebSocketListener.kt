package ru.yandexpraktikum.myandroidchat.data.websocket

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class ChatWebSocketListener : WebSocketListener() {
    private val _messageFlow = MutableSharedFlow<String>()
    val messageFlow: SharedFlow<String> = _messageFlow.asSharedFlow()

    private val _connectionStatus = MutableSharedFlow<ConnectionStatus>()
    val connectionStatus: SharedFlow<ConnectionStatus> = _connectionStatus.asSharedFlow()

    override fun onOpen(webSocket: WebSocket, response: Response) {
        _connectionStatus.tryEmit(ConnectionStatus.CONNECTED)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        _messageFlow.tryEmit(text)
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        // Обработка бинарных сообщений
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null)
        _connectionStatus.tryEmit(ConnectionStatus.DISCONNECTED)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        _connectionStatus.tryEmit(ConnectionStatus.ERROR)
    }

    companion object {
        private const val NORMAL_CLOSURE_STATUS = 1000
    }
}

enum class ConnectionStatus {
    CONNECTED,
    DISCONNECTED,
    ERROR
} 