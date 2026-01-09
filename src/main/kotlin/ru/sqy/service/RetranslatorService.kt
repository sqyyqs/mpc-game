package main.kotlin.ru.sqy.service

import com.fasterxml.jackson.core.JacksonException
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
    private val id: String,
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
        try {
            when (val message = messageMapper.fromRawString(raw)) {
                is Players -> players.put(message)
                is PublicKey -> publicKeys.put(message)
                is EncryptedShare -> shares.put(message)
                is Counter -> counter.put(message)
                is OutOfGame -> outOfGame.put(message)
                is RangeProof -> rangeProof.put(message)
            }
        } catch (e: Exception) {
            if (raw.contains("Pick nickname:")) {
                tcpClient.send("$id")
            } else {
                val ids = (raw.split("\n").drop(1) + id).sorted()
                players.put(Players(ids = ids, from = ""))
            }
        }
    }

    fun sendPlayers() {
        tcpClient.send("print")
    }

    fun send(message: Message, to: String) {
        send(message, listOf(to))
    }

    fun send(message: Message, to: List<String>) {
        tcpClient.send(messageMapper.mapToString(message), to)
    }
}