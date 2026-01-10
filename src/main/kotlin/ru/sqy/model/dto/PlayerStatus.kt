package ru.sqy.model.dto

import ru.sqy.model.message.OutOfGameStatus

enum class PlayerStatus {
    ACTIVE,
    OVERFLOWED,
    PASSED;

    companion object {
        fun from(outOfGameStatus: OutOfGameStatus): PlayerStatus {
            return when (outOfGameStatus) {
                OutOfGameStatus.PASSED -> PASSED
                OutOfGameStatus.OVERFLOWED -> OVERFLOWED
            }
        }
    }
}
