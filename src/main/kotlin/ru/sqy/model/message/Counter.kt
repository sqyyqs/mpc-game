package ru.sqy.model.message

import main.kotlin.ru.sqy.model.message.Message
import java.math.BigInteger

data class Counter(
    val value: BigInteger,
    val oldValue: BigInteger,
    val shares: List<BigInteger>,
    val from: String,
) : Message {
    override fun from() = from
    override fun typeName() = "Counter"
}
