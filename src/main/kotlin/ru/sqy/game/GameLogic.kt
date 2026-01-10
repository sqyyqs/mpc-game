package ru.sqy.game

import ru.sqy.model.dto.result.CheckPassedPlayersResult
import ru.sqy.model.dto.result.CheckWinnerResult
import ru.sqy.model.dto.Player
import ru.sqy.model.dto.PlayerStatus
import ru.sqy.model.dto.result.ChooseActionResult
import ru.sqy.model.message.EncryptedShare
import ru.sqy.model.message.OutOfGame
import ru.sqy.model.message.OutOfGameStatus
import ru.sqy.service.CryptoService
import ru.sqy.service.RetranslatorService
import ru.sqy.service.ShareService
import main.kotlin.ru.sqy.service.mapper.CryptoMapper
import java.util.concurrent.LinkedBlockingQueue
import kotlin.system.exitProcess

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
        gameState.initQueue()

        while (true) {
            if (phaseCheckGameRunning() == CheckWinnerResult.GAME_DONE) {
                break
            }
            if (gameState.isMyTurn()) {
                println("Счетчик: ${gameState.counter}")
            }
            if (gameState.isMyTurn() && input() == ChooseActionResult.PASS) {
                out(OutOfGameStatus.PASSED)
                if (phaseCheckGameRunning() == CheckWinnerResult.GAME_DONE) {
                    break
                }
            }

            val checkPassedPlayersResult = phaseCheckForPassedPlayers()
            if (checkPassedPlayersResult == CheckPassedPlayersResult.PASSED) {
                continue
            }
            phasePublicKey()
            phaseEncryptedShares()
            phaseUpdateCounter()
            phaseConfirmCounter()
            phaseSendCounterBelowMProof()
            val isOverflowNotHappened = phaseCheckCounterBelowMProof()
            if (gameState.counter >= gameState.m) {
                println("Перебор...")
                out(OutOfGameStatus.OVERFLOWED)
            }
            if (isOverflowNotHappened) {
                gameState.updateTurnIndex()
            }
        }
        phaseRevealWinner()
    }


    private fun phaseWaitAll() {
        fun RetranslatorService.retrieveIds() = this.players.take().ids.sorted().map { Player(it) }.toMutableList()

        gameState.players = retranslatorService.retrieveIds()
        while (!gameState.isMatches(desiredPlayersState)) {
            retranslatorService.sendPlayers()
            Thread.sleep(1000)
            gameState.players = retranslatorService.retrieveIds()
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
            retranslatorService.outOfGame.takeN(retranslatorService.outOfGame.size).forEach { outOfGameMessage ->
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
            val encryptedShare = cryptoService.encryptShare(shareService.generateShare(), publicKey)
            retranslatorService.send(encryptedShare, publicKey.from)
        } else {
            Thread.sleep(1000)
        }
    }

    private fun phaseUpdateCounter() {
        if (gameState.isMyTurn()) {
            val encryptedShares = mutableListOf<EncryptedShare>()
            repeat(gameState.players.size - 1) {
                encryptedShares.add(retranslatorService.shares.take())
            }

            val decryptedShares = cryptoService.decryptShares(encryptedShares)

            val encryptedOldCounter = cryptoService.encryptCounter(gameState.counter)
            val numberToAdd = shareService.calculateFromShares(decryptedShares)
            println("Добавляемое значение: $numberToAdd")
            gameState.counter += numberToAdd
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
        } else {
            Thread.sleep(1000)
        }
    }

    private fun phaseSendCounterBelowMProof() {
        if (gameState.isMyTurn()) {
            val proof = cryptoService.generateProof(gameState.counter)
            retranslatorService.send(proof, gameState.allOtherPlayerIds())
        } else {
            Thread.sleep(1000)
        }
    }

    private fun phaseCheckCounterBelowMProof(): Boolean {
        if (!gameState.isMyTurn()) {
            val rangeProof = retranslatorService.rangeProof.take()
            val verifyProof = cryptoService.verifyProof(rangeProof)
            if (!verifyProof) {
                gameState.setStatus(rangeProof.from, PlayerStatus.OVERFLOWED)
            }
            return verifyProof
        } else {
            Thread.sleep(1000)
        }
        return true
    }

    private fun phaseCheckGameRunning(): CheckWinnerResult {
        return if (gameState.activePlayers.isEmpty()) {
            CheckWinnerResult.GAME_DONE
        } else {
            CheckWinnerResult.STILL_GAMING
        }
    }

    private fun phaseRevealWinner() {
        println("Конец игры!")
        println("Ваш счет: " + gameState.counter)
        exitProcess(0)
    }

    private fun input(): ChooseActionResult {
        println("Ход/пасс? (r/p)")

        while (true) {
            when (readlnOrNull()) {
                "p" -> return ChooseActionResult.PASS
                "r" -> return ChooseActionResult.DICE_ROLL
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

    private fun <E> LinkedBlockingQueue<E & Any>.takeN(times: Int): List<E> {
        val list = mutableListOf<E>()
        repeat(times) {
            list.add(take())
        }
        return list
    }

}