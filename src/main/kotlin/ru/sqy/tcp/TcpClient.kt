package main.kotlin.ru.sqy.tcp

import main.kotlin.ru.sqy.model.dto.ConnectionInfo
import java.io.InputStream
import java.io.PrintWriter
import java.net.Socket
import kotlin.concurrent.thread

class TcpClient(
    connectInfo: ConnectionInfo,
) {
    val socket = Socket(connectInfo.host, connectInfo.port)
    val input: InputStream = socket.getInputStream()
    val writer = PrintWriter(socket.getOutputStream(), true)

    fun startThread(callback: (String) -> Unit) {
        thread {
            val buffer = ByteArray(16364)
            while (true) {
                val n = input.read(buffer)
                if (n == -1) break
                val msg = String(buffer, 0, n)
                print("SERVER: $msg")
                callback(msg)
            }
        }
    }

    fun send(str: String, to: List<String>) {
        val tcpMessage = buildString {
            append("send ")
            append(to.joinToString(","))
            append(" $str")
        }

        writer.write(tcpMessage)
    }

}