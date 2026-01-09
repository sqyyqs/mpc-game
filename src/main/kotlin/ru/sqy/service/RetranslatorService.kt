package main.kotlin.ru.sqy.service

import main.kotlin.ru.sqy.model.message.Counter
import main.kotlin.ru.sqy.model.message.EncryptedShare
import main.kotlin.ru.sqy.model.message.Message
import main.kotlin.ru.sqy.model.message.OutOfGame
import main.kotlin.ru.sqy.model.message.Players
import main.kotlin.ru.sqy.model.message.PublicKey
import main.kotlin.ru.sqy.model.message.RangeProof
import main.kotlin.ru.sqy.service.mapper.MessageMapper
import main.kotlin.ru.sqy.tcp.TcpClient
import java.util.concurrent.LinkedBlockingQueue

class RetranslatorService(
    private val tcpClient: TcpClient,
    private val messageMapper: MessageMapper,
    private val id: String
) {
    val players = LinkedBlockingQueue<Players>()
    val publicKeys = LinkedBlockingQueue<PublicKey>()
    val shares = LinkedBlockingQueue<EncryptedShare>()
    val counter = LinkedBlockingQueue<Counter>()
    val outOfGame = LinkedBlockingQueue<OutOfGame>()
    val rangeProof = LinkedBlockingQueue<RangeProof>()

    init {
        tcpClient.startThread { dispatch(it) }
    }

    private fun dispatch(raw: String) {
        if (raw.contains("Pick nickname:")) {
            tcpClient.send("$id\n")
            return
        }

        if (raw.contains("available connections:")) {
            players.put(Players(ids = listOf(), from = ""))
            return
        }

        when (val message = messageMapper.fromRawString(raw)) {
            is Players -> players.put(message)
            is PublicKey -> publicKeys.put(message)
            is EncryptedShare -> shares.put(message)
            is Counter -> counter.put(message)
            is OutOfGame -> outOfGame.put(message)
            is RangeProof -> rangeProof.put(message)
        }
    }

    fun sendPlayers() {
        tcpClient.send("print\n")
    }

    fun send(message: Message, to: String) {
        send(message, listOf(to))
    }

    fun send(message: Message, to: List<String>) {
        tcpClient.send(messageMapper.mapToString(message), to)
    }
}