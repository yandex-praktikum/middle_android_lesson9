package ru.yandexpraktikum.myandroidchat.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.yandexpraktikum.myandroidchat.domain.model.Message

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey
    val id: String,
    val content: String,
    val sender: String,
    val timestamp: Long
) {
    fun toMessage(): Message {
        return Message(
            id = id,
            content = content,
            sender = sender,
            timestamp = timestamp
        )
    }

    companion object {
        fun fromMessage(message: Message): MessageEntity {
            return MessageEntity(
                id = message.id,
                content = message.content,
                sender = message.sender,
                timestamp = message.timestamp
            )
        }
    }
} 