package ru.yandexpraktikum.myandroidchat.domain.model

data class Message(
    val id: String,
    val content: String,
    val sender: String,
    val timestamp: Long
) 