package main.kotlin.ru.sqy.model.message

import java.math.BigInteger

class EncryptedShare(
    val share: BigInteger,
    val from: String,
): Message {
    override fun from() = from
    override fun typeName() = "EncryptedShare"
}
