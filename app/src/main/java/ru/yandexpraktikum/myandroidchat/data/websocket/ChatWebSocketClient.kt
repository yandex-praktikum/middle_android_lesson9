package ru.yandexpraktikum.myandroidchat.data.websocket

import kotlinx.coroutines.flow.SharedFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatWebSocketClient @Inject constructor(
    private val client: OkHttpClient,
    private val webSocketListener: ChatWebSocketListener
) {
    private var webSocket: WebSocket? = null
    
    val messageFlow: SharedFlow<String> = webSocketListener.messageFlow
    val connectionStatus: SharedFlow<ConnectionStatus> = webSocketListener.connectionStatus

    fun connect(url: String) {
        val request = Request.Builder()
            .url(url)
            .build()
        
        webSocket = client.newWebSocket(request, webSocketListener)
    }

    fun sendMessage(message: String): Boolean {
        return webSocket?.send(message) ?: false
    }

    fun disconnect() {
        webSocket?.close(NORMAL_CLOSURE_STATUS, null)
        webSocket = null
    }

    companion object {
        private const val NORMAL_CLOSURE_STATUS = 1000
    }
} 