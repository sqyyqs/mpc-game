package main.kotlin.ru.sqy.service

import main.kotlin.ru.sqy.model.message.Counter
import main.kotlin.ru.sqy.model.message.EncryptedShare
import main.kotlin.ru.sqy.model.message.Message
import main.kotlin.ru.sqy.model.message.OutOfGame
import main.kotlin.ru.sqy.model.message.Players
import main.kotlin.ru.sqy.model.message.PublicKey
import main.kotlin.ru.sqy.service.mapper.MessageMapper
import main.kotlin.ru.sqy.tcp.TcpClient
import java.util.concurrent.LinkedBlockingQueue

class RetranslatorService(
    private val tcpClient: TcpClient,
    private val messageMapper: MessageMapper,
) {
    val players = LinkedBlockingQueue<Players>()
    val publicKeys = LinkedBlockingQueue<PublicKey>()
    val shares = LinkedBlockingQueue<EncryptedShare>()
    val counter = LinkedBlockingQueue<Counter>()
    val outOfGame = LinkedBlockingQueue<OutOfGame>()

    init {
        tcpClient.startThread { dispatch(it) }
    }

    private fun dispatch(raw: String) {
        when (val message = messageMapper.fromRawString(raw)) {
            is Players -> players.put(message)
            is PublicKey -> publicKeys.put(message)
            is EncryptedShare -> shares.put(message)
            is Counter -> counter.put(message)
            is OutOfGame -> outOfGame.put(message)
        }
    }

    fun send(message: Message, to: String) {
        send(message, listOf(to))
    }

    fun send(message: Message, to: List<String>) {
        tcpClient.send(messageMapper.mapToString(message), to)
    }
}