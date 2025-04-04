package ru.yandexpraktikum.myandroidchat.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.yandexpraktikum.myandroidchat.data.websocket.ConnectionStatus
import ru.yandexpraktikum.myandroidchat.domain.model.Message
import ru.yandexpraktikum.myandroidchat.domain.repository.ChatRepository
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _connectionStatus = MutableStateFlow(ConnectionStatus.DISCONNECTED)
    val connectionStatus: StateFlow<ConnectionStatus> = _connectionStatus.asStateFlow()

    init {
        observeMessages()
        observeConnectionStatus()
    }

    fun connect(url: String) {
        chatRepository.connect(url)
    }

    fun disconnect() {
        chatRepository.disconnect()
    }

    fun sendMessage(message: String) {
        if (message.isBlank()) return
        viewModelScope.launch {
            chatRepository.sendMessage(message)
        }
    }

    private fun observeMessages() {
        chatRepository.observeMessages()
            .onEach { messages ->
                _messages.value = messages
            }
            .launchIn(viewModelScope)
    }

    private fun observeConnectionStatus() {
        chatRepository.observeConnectionStatus()
            .onEach { status ->
                _connectionStatus.value = status
            }
            .launchIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
    }
} 