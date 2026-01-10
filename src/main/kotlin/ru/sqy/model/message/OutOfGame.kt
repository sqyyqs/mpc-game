package ru.sqy.model.message

import main.kotlin.ru.sqy.model.message.Message

data class OutOfGame(
    val from: String,
    val status: OutOfGameStatus
) : Message {
    override fun from() = from
    override fun typeName() = "OutOfGame"
}
