package main.kotlin.ru.sqy.model.message

data class OutOfGame(
    val from: String,
    val status: OutOfGameStatus
) : Message {
    override fun from() = from
    override fun typeName() = "OutOfGame"
}
