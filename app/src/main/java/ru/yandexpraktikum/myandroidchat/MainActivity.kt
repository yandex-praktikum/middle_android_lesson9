package ru.yandexpraktikum.myandroidchat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import dagger.hilt.android.AndroidEntryPoint
import ru.yandexpraktikum.myandroidchat.presentation.ChatScreen
import ru.yandexpraktikum.myandroidchat.ui.theme.MyAndroidChatTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAndroidChatTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    ChatScreen(
                        websocketUrl = "ws://your-websocket-server-url"
                    )
                }
            }
        }
    }
}