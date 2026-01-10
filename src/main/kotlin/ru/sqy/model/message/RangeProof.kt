package ru.sqy.model.message

import main.kotlin.ru.sqy.model.message.Message

data class RangeProof(
    val data: ByteArray,
    val from: String
): Message {
    override fun from() = from
    override fun typeName() = "RangeProof"
}
