package main.kotlin.ru.sqy.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import main.kotlin.ru.sqy.model.dto.ConnectionInfo
import main.kotlin.ru.sqy.model.dto.GameParameters

@Suppress("UNCHECKED_CAST")
class ConfigService {
    val gameParameters: GameParameters
    val players: List<String>
    val connectionInfo: ConnectionInfo

    init {
        val mapper = ObjectMapper()
        val file = Thread.currentThread().contextClassLoader.getResource("config.json")!!.file
        val typeRef
                : TypeReference<HashMap<String, Any>> = object : TypeReference<HashMap<String, Any>>() {}
        val value: Map<String, Any> = mapper.readValue(file, typeRef)

        gameParameters = gameParametersFrom(value)
        players = playersFrom(value)
        connectionInfo = connectionInfoFrom(value)
    }

    private fun gameParametersFrom(params: Map<String, Any>): GameParameters {
        val params = params["params"] as Map<String, Int>

        return GameParameters(
            m = params["m"]!!,
            n = params["n"]!!
        )
    }

    private fun playersFrom(params: Map<String, Any>) = params["players"] as List<String>

    private fun connectionInfoFrom(params: Map<String, Any>): ConnectionInfo {
        val connectInfoMap = params["connect"] as Map<String, String>

        return ConnectionInfo(
            host = connectInfoMap["host"]!!,
            port = connectInfoMap["port"]!!.toInt()
        )
    }
}