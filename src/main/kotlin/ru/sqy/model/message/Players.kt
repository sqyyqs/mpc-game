package ru.sqy.model.message

import main.kotlin.ru.sqy.model.message.Message

data class Players(
    val ids: List<String>,
    val from: String,
): Message {
    override fun from() = from
    override fun typeName() = "Players"
}
