package ru.sqy

import ru.sqy.crypto.zkprange.ZkpRange
import ru.sqy.game.GameLogic
import ru.sqy.game.GameState
import ru.sqy.model.dto.CryptoState
import ru.sqy.service.ConfigService
import ru.sqy.service.CryptoService
import ru.sqy.service.RetranslatorService
import ru.sqy.service.ShareService
import main.kotlin.ru.sqy.service.mapper.CryptoMapper
import ru.sqy.service.mapper.MessageMapper
import ru.sqy.tcp.TcpClient
import ru.sqy.model.dto.TurnQueue

fun main(args: Array<String>) {
    val configService = ConfigService()

    val id = args[0]

    val tcpClient = TcpClient(configService.connectionInfo)
    val messageMapper = MessageMapper()
    val cryptoMapper = CryptoMapper(id)
    val zkpRange = ZkpRange(configService.gameParameters)

    val retranslatorService = RetranslatorService(tcpClient, messageMapper, id)
    val cryptoService = CryptoService(CryptoState(), cryptoMapper, zkpRange)
    val shareService = ShareService(configService.gameParameters)

    val gameLogic = GameLogic(
        desiredPlayersState = configService.players,
        retranslatorService = retranslatorService,
        cryptoService = cryptoService,
        shareService = shareService,
        gameState = GameState(id = id, m = configService.gameParameters.m, turnQueue = TurnQueue(mutableListOf())),
        cryptoMapper = cryptoMapper
    )

    gameLogic.game()
}