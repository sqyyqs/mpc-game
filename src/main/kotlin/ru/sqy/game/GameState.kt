package main.kotlin.ru.sqy.game

import main.kotlin.ru.sqy.model.dto.Player
import main.kotlin.ru.sqy.model.dto.PlayerStatus

data class GameState(
    var id: String,
    var counter: Int = 0,
    var players: MutableList<Player> = mutableListOf(),
    private var currentTurnIndex: Int = 0,
) {
    val activePlayers: List<Player>
        get() = players.filter { it.status == PlayerStatus.ACTIVE }

    fun isMyTurn(): Boolean {
        return activePlayers[currentTurnIndex].id == id
    }

    fun isMatches(desiredState: List<String>): Boolean {
        return desiredState.containsAll(activePlayers.map { it.id })
    }

    //todo почему не используется?
    fun isPlayerActive(id: String = this.id) =
        players.find { it.id == id }?.status == PlayerStatus.ACTIVE

    fun setStatus(playerId: String, playerStatus: PlayerStatus) {
        players.forEachIndexed { index, player ->
            if (player.id == playerId) {
                players[index] = player.copy(status = playerStatus)
            }
        }
    }

    fun updateTurnIndex() {
        currentTurnIndex = (currentTurnIndex + 1) % activePlayers.size
    }

    fun allOtherPlayerIds(): List<String> {
        return players.filter { it.id != id }.map { it.id }
    }
}