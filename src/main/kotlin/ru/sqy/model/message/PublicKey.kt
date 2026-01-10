package ru.sqy.model.message

import main.kotlin.ru.sqy.model.message.Message
import java.math.BigInteger

data class PublicKey(
    val n: BigInteger,
    val g: BigInteger,
    val bits: Int,
    val from: String,
) : Message {
    override fun from() = from
    override fun typeName() = "PublicKey"
}