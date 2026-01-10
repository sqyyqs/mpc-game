package main.kotlin.ru.sqy.game

import main.kotlin.ru.sqy.model.dto.Player
import main.kotlin.ru.sqy.model.dto.PlayerStatus
import ru.sqy.model.dto.TurnQueue

data class GameState(
    var id: String,
    var counter: Int = 0,
    var players: MutableList<Player> = mutableListOf(),
    var m: Int,
    private var turnQueue: TurnQueue<String>,
) {
    val activePlayers: List<Player>
        get() = players.filter { it.status == PlayerStatus.ACTIVE }

    fun initQueue() {
        turnQueue = TurnQueue(players.map { it.id }.toMutableList())
    }

    fun isMyTurn(): Boolean {
        return turnQueue.current() == id
    }

    fun isMatches(desiredState: List<String>): Boolean {
        return activePlayers.map { it.id }.containsAll(desiredState)
    }

    fun setStatus(playerId: String, playerStatus: PlayerStatus) {
        players.forEachIndexed { index, player ->
            if (player.id == playerId) {
                players[index] = player.copy(status = playerStatus)
            }
            if (playerStatus != PlayerStatus.ACTIVE) {
                turnQueue.remove(playerId)
            }
        }
    }

    fun updateTurnIndex() {
        turnQueue.next()
    }

    fun allOtherPlayerIds(): List<String> {
        return players.filter { it.id != id }.map { it.id }
    }
}