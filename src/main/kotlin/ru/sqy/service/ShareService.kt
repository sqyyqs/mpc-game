package ru.sqy.service

import ru.sqy.model.dto.GameParameters
import java.security.SecureRandom

class ShareService(
    private val gameParameters: GameParameters,
    private val secureRandom: SecureRandom = SecureRandom(),
) {
    fun generateShare(playerCount: Int): Int {
        val upperBound = (gameParameters.n / (playerCount - 1)) + 1
        return secureRandom.nextInt(upperBound)
    }

    fun calculateFromShares(shares: List<Int>): Int {
        return shares.sum()
    }
}