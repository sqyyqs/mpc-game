package main.kotlin.ru.sqy.model.message

data class RangeProof(
    val data: ByteArray,
    val from: String
): Message {
    override fun from() = from
    override fun typeName() = "RangeProof"
}
