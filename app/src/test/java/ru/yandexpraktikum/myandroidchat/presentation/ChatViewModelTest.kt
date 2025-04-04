package ru.yandexpraktikum.myandroidchat.presentation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.yandexpraktikum.myandroidchat.data.websocket.ConnectionStatus
import ru.yandexpraktikum.myandroidchat.domain.model.Message
import ru.yandexpraktikum.myandroidchat.domain.repository.ChatRepository
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {
    private lateinit var chatRepository: ChatRepository
    private lateinit var viewModel: ChatViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        chatRepository = mock()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when connecting to websocket, repository connect is called with correct url`() = runTest {
        // Given
        val url = "ws://test.com"
        whenever(chatRepository.observeMessages()).thenReturn(flowOf(emptyList()))
        whenever(chatRepository.observeConnectionStatus()).thenReturn(
            MutableSharedFlow()
        )
        viewModel = ChatViewModel(chatRepository)

        // When
        viewModel.connect(url)

        // Then
        verify(chatRepository).connect(url)
    }

    @Test
    fun `when message is received, messages state is updated`() = runTest {
        // Given
        val testMessage = Message(
            id = "1",
            content = "Hello",
            sender = "user",
            timestamp = 123L
        )
        whenever(chatRepository.observeMessages()).thenReturn(
            flowOf(listOf(testMessage))
        )
        whenever(chatRepository.observeConnectionStatus()).thenReturn(
            MutableSharedFlow()
        )

        // When
        viewModel = ChatViewModel(chatRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(listOf(testMessage), viewModel.messages.value)
    }

    @Test
    fun `when connection status changes, status state is updated`() = runTest {
        // Given
        val statusFlow = MutableSharedFlow<ConnectionStatus>()
        whenever(chatRepository.observeMessages()).thenReturn(flowOf(emptyList()))
        whenever(chatRepository.observeConnectionStatus()).thenReturn(statusFlow)
        viewModel = ChatViewModel(chatRepository)

        // When
        statusFlow.emit(ConnectionStatus.CONNECTED)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertEquals(ConnectionStatus.CONNECTED, viewModel.connectionStatus.value)
    }

    @Test
    fun `when sending message, repository sendMessage is called with correct content`() = runTest {
        // Given
        val message = "Test message"
        whenever(chatRepository.observeMessages()).thenReturn(flowOf(emptyList()))
        whenever(chatRepository.observeConnectionStatus()).thenReturn(
            MutableSharedFlow()
        )
        viewModel = ChatViewModel(chatRepository)

        // When
        viewModel.sendMessage(message)

        // Then
        verify(chatRepository).sendMessage(message)
    }
} 