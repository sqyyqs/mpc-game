package ru.sqy.tcp

import ru.sqy.model.dto.ConnectionInfo
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import kotlin.concurrent.thread

class TcpClient(
    connectInfo: ConnectionInfo,
) {
    val socket = Socket(connectInfo.host, connectInfo.port)
    val input = BufferedReader(InputStreamReader(socket.getInputStream()))
    val writer = PrintWriter(socket.getOutputStream(), true)

    fun startThread(callback: (String) -> Unit) {
        thread {
            val buffer = CharArray(16364)
            while (true) {
                val n = input.read(buffer)
                if (n == -1) {
                    break
                }

                var msg = String(buffer, 0, n)

                if (msg.last() == '\n') {
                    msg = msg.dropLast(1)
                }

//                println("SERVER: $msg")

                callback(msg)
            }
        }
    }

    fun send(str: String) {
        writer.appendLine(str)
        writer.flush()
    }

    fun send(str: String, to: List<String>) {
        val tcpMessage = buildString {
            append("send ")
            append(to.joinToString(","))
            append(" $str")
        }
        send(tcpMessage)
    }

}