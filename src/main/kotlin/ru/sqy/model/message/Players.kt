package main.kotlin.ru.sqy.model.message

data class Players(
    val ids: List<String>,
    val from: String,
): Message {
    override fun from() = from
    override fun typeName() = "Players"
}
