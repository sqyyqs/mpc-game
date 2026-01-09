package main.kotlin.ru.sqy

import main.kotlin.ru.sqy.game.GameLogic
import main.kotlin.ru.sqy.game.GameState
import main.kotlin.ru.sqy.model.dto.CryptoState
import main.kotlin.ru.sqy.service.ConfigService
import main.kotlin.ru.sqy.service.CryptoService
import main.kotlin.ru.sqy.service.RetranslatorService
import main.kotlin.ru.sqy.service.ShareService
import main.kotlin.ru.sqy.service.mapper.CryptoMapper
import main.kotlin.ru.sqy.service.mapper.MessageMapper
import main.kotlin.ru.sqy.tcp.TcpClient

fun main(args: Array<String>) {
    val configService = ConfigService()

    val id = args[0]

    val tcpClient = TcpClient(configService.connectionInfo)
    val messageMapper = MessageMapper()
    val cryptoMapper = CryptoMapper(id)

    val retranslatorService = RetranslatorService(tcpClient, messageMapper)
    val cryptoService = CryptoService(CryptoState(), cryptoMapper)
    val shareService = ShareService(configService.gameParameters)

    val gameLogic = GameLogic(
        desiredPlayersState = configService.players,
        retranslatorService = retranslatorService,
        cryptoService = cryptoService,
        shareService = shareService,
        gameState = GameState(id),
        cryptoMapper = cryptoMapper
    )

    gameLogic.game()
}