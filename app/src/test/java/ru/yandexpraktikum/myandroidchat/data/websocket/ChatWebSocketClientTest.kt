package ru.yandexpraktikum.myandroidchat.data.websocket

import kotlinx.coroutines.flow.MutableSharedFlow
import okhttp3.OkHttpClient
import okhttp3.WebSocket
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ChatWebSocketClientTest {
    private lateinit var okHttpClient: OkHttpClient
    private lateinit var webSocket: WebSocket
    private lateinit var webSocketListener: ChatWebSocketListener
    private lateinit var chatWebSocketClient: ChatWebSocketClient

    @Before
    fun setup() {
        okHttpClient = mock()
        webSocket = mock()
        webSocketListener = ChatWebSocketListener()

        whenever(okHttpClient.newWebSocket(any(), any())).thenReturn(webSocket)

        chatWebSocketClient = ChatWebSocketClient(okHttpClient, webSocketListener)
    }

    @Test
    fun `when connecting, websocket is created with correct url`() {
        // Given
        val url = "ws://test.com"

        // When
        chatWebSocketClient.connect(url)

        // Then
        verify(okHttpClient).newWebSocket(any(), any())
    }

    @Test
    fun `when sending message, websocket send is called with correct message`() {
        // Given
        val message = "Test message"
        chatWebSocketClient.connect("ws://test.com") // Need to connect first

        // When
        chatWebSocketClient.sendMessage(message)

        // Then
        verify(webSocket).send(message)
    }

    @Test
    fun `when disconnecting, websocket close is called`() {
        // Given
        chatWebSocketClient.connect("ws://test.com")

        // When
        chatWebSocketClient.disconnect()

        // Then
        verify(webSocket).close(1000, null)
    }
}

class ChatWebSocketListenerTest {
    private lateinit var webSocket: WebSocket
    private lateinit var listener: ChatWebSocketListener

    @Before
    fun setup() {
        webSocket = mock()
        listener = ChatWebSocketListener()
    }

    @Test
    fun `when onOpen is called, connection status is updated to CONNECTED`() {
        // Given
        val statusFlow = MutableSharedFlow<ConnectionStatus>()
        var emittedStatus: ConnectionStatus? = null
        statusFlow.subscribeInBackground { emittedStatus = it }

        // When
        listener.onOpen(webSocket, mock())

        // Then
        assert(emittedStatus == ConnectionStatus.CONNECTED)
    }

    @Test
    fun `when onMessage is called, message is emitted to flow`() {
        // Given
        val messageFlow = MutableSharedFlow<String>()
        var emittedMessage: String? = null
        messageFlow.subscribeInBackground { emittedMessage = it }
        val testMessage = "Test message"

        // When
        listener.onMessage(webSocket, testMessage)

        // Then
        assert(emittedMessage == testMessage)
    }

    @Test
    fun `when onFailure is called, connection status is updated to ERROR`() {
        // Given
        val statusFlow = MutableSharedFlow<ConnectionStatus>()
        var emittedStatus: ConnectionStatus? = null
        statusFlow.subscribeInBackground { emittedStatus = it }

        // When
        listener.onFailure(webSocket, Exception(), null)

        // Then
        assert(emittedStatus == ConnectionStatus.ERROR)
    }

    private fun <T> MutableSharedFlow<T>.subscribeInBackground(onEach: (T) -> Unit) {
        // Helper function to subscribe to flow in tests
        kotlinx.coroutines.runBlocking {
            this@subscribeInBackground.collect { onEach(it) }
        }
    }
} 