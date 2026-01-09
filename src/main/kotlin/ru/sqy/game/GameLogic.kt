package main.kotlin.ru.sqy.game

import main.kotlin.ru.sqy.model.dto.result.CheckPassedPlayersResult
import main.kotlin.ru.sqy.model.dto.result.CheckWinnerResult
import main.kotlin.ru.sqy.model.dto.Player
import main.kotlin.ru.sqy.model.dto.PlayerStatus
import main.kotlin.ru.sqy.model.dto.result.ChooseActionResult
import main.kotlin.ru.sqy.model.message.OutOfGame
import main.kotlin.ru.sqy.model.message.OutOfGameStatus
import main.kotlin.ru.sqy.service.CryptoService
import main.kotlin.ru.sqy.service.RetranslatorService
import main.kotlin.ru.sqy.service.ShareService
import main.kotlin.ru.sqy.service.mapper.CryptoMapper

class GameLogic(
    private val desiredPlayersState: List<String>,
    private val retranslatorService: RetranslatorService,
    private val cryptoService: CryptoService,
    private val cryptoMapper: CryptoMapper,
    private val shareService: ShareService,
    private val gameState: GameState,
) {

    fun game() {
        phaseWaitAll()

        while (true) {
            if (gameState.isMyTurn() && input() == ChooseActionResult.PASS) {
                out(OutOfGameStatus.PASSED)
            }

            val checkPassedPlayersResult = phaseCheckForPassedPlayers()
            if (checkPassedPlayersResult == CheckPassedPlayersResult.PASSED) {
                gameState.updateTurnIndex()
                continue
            }

            phasePublicKey()
            phaseEncryptedShares()
            phaseUpdateCounter()
            phaseConfirmCounter()
            phaseSendCounterBelowMProof()
            phaseCheckCounterBelowMProof()

            if (phaseCheckGameRunning() == CheckWinnerResult.GAME_DONE) {
                phaseRevealWinner()
                return
            }

            gameState.updateTurnIndex()
        }
    }


    private fun phaseWaitAll() {
        gameState.players = retranslatorService.players.take().ids.map { Player(it) }.toMutableList()
        while (!gameState.isMatches(desiredPlayersState)) {
            Thread.sleep(500)
            gameState.players = retranslatorService.players.take().ids.map { Player(it) }.toMutableList()
        }
    }

    private fun phaseCheckForPassedPlayers(): CheckPassedPlayersResult {
        if (gameState.isMyTurn()) {
            return CheckPassedPlayersResult.NOT_WAITING
        }

        var totalCount = retranslatorService.publicKeys.size + retranslatorService.outOfGame.size
        while (totalCount == 0) {
            Thread.sleep(400)
            totalCount = retranslatorService.publicKeys.size + retranslatorService.outOfGame.size
        }

        return if (retranslatorService.outOfGame.isNotEmpty()) {
            retranslatorService.outOfGame.forEach { outOfGameMessage ->
                gameState.setStatus(
                    playerId = outOfGameMessage.from,
                    playerStatus = PlayerStatus.from(outOfGameMessage.status)
                )
            }
            CheckPassedPlayersResult.PASSED
        } else {
            CheckPassedPlayersResult.SENT_PUBLIC_KEY
        }
    }

    private fun phasePublicKey() {
        if (gameState.isMyTurn()) {
            val publicKey = cryptoService.generatePublicKey()
            retranslatorService.send(publicKey, gameState.allOtherPlayerIds())
        } else {
            Thread.sleep(500)
        }
    }

    private fun phaseEncryptedShares() {
        if (!gameState.isMyTurn()) {
            val publicKey = retranslatorService.publicKeys.take()
            val encryptedShare = cryptoService.encryptShare(shareService.generateShare())
            retranslatorService.send(encryptedShare, publicKey.from)
        } else {
            Thread.sleep(1000)
        }
    }

    private fun phaseUpdateCounter() {
        if (gameState.isMyTurn()) {
            val encryptedShares = retranslatorService.shares.take(gameState.activePlayers.size - 1)
            val decryptedShares = cryptoService.decryptShares(encryptedShares)

            val encryptedOldCounter = cryptoService.encryptCounter(gameState.counter)
            gameState.counter += shareService.calculateFromShares(decryptedShares)
            val encryptedCounter = cryptoService.encryptCounter(gameState.counter)

            val encryptedCounterMessage = cryptoMapper.encryptedCounterToMessage(
                encryptedCounter,
                encryptedOldCounter,
                encryptedShares
            )

            retranslatorService.send(encryptedCounterMessage, gameState.allOtherPlayerIds())
        } else {
            Thread.sleep(1000)
        }
    }

    private fun phaseConfirmCounter() {
        if (!gameState.isMyTurn()) {
            val updatedCounterMessage = retranslatorService.counter.take()
            cryptoService.checkCounterComputation(updatedCounterMessage)
        }
    }


    private fun phaseSendCounterBelowMProof() {
        if (gameState.isMyTurn()) {
            val proof = cryptoService.generateProof(gameState.counter)
            if (cryptoService.verifyProof(proof)) {
                out(OutOfGameStatus.OVERFLOWED)
            }
            retranslatorService.send(proof, gameState.allOtherPlayerIds())
        }
    }

    private fun phaseCheckCounterBelowMProof() {
        if (!gameState.isMyTurn()) {
            val rangeProof = retranslatorService.rangeProof.take()
            if (!cryptoService.verifyProof(rangeProof)) {
                gameState.setStatus(rangeProof.from, PlayerStatus.OVERFLOWED)
            }
        }
    }

    private fun phaseCheckGameRunning(): CheckWinnerResult {
        return if (gameState.activePlayers.isEmpty()) {
            CheckWinnerResult.STILL_GAMING
        } else {
            CheckWinnerResult.GAME_DONE
        }
    }

    private fun phaseRevealWinner() {
        println("Ваш счет: " + gameState.counter)
    }

    private fun input(): ChooseActionResult {
        println("Ход/пасс? (r/p)")

        while (true) {
            when (readlnOrNull()) {
                "r" -> return ChooseActionResult.PASS
                "p" -> return ChooseActionResult.DICE_ROLL
                else -> {
                    println("Неизвестная команда! попробуйте еще раз")
                    println("Ход/пасс? (r/p)")
                }
            }
        }
    }

    private fun out(status: OutOfGameStatus) {
        gameState.setStatus(gameState.id, PlayerStatus.from(status))
        retranslatorService.send(
            message = OutOfGame(
                from = gameState.id,
                status = status
            ),
            to = gameState.allOtherPlayerIds()
        )
    }

}