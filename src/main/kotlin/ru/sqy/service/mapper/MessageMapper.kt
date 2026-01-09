package main.kotlin.ru.sqy.service.mapper

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import main.kotlin.ru.sqy.model.message.Message

class MessageMapper {
    private val objectMapper = jacksonObjectMapper()

    fun fromRawString(raw: String): Message {
        return objectMapper.readValue(raw, Message::class.java)
    }

    fun mapToString(message: Message): String {
        return objectMapper.writeValueAsString(message)
    }
}