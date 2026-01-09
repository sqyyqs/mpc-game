package main.kotlin.ru.sqy.service

import main.kotlin.ru.sqy.model.dto.GameParameters
import java.security.SecureRandom
import kotlin.math.roundToInt

class ShareService(
    private val gameParameters: GameParameters,
    private val secureRandom: SecureRandom = SecureRandom(),
) {
    fun generateShare(): Int {
        return secureRandom.nextInt(gameParameters.n)
    }

    fun calculateFromShares(shares: List<Int>): Int {
        return shares.average().roundToInt()
    }
}