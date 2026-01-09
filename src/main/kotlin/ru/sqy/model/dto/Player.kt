package main.kotlin.ru.sqy.model.dto

data class Player(
    val id: String,
    val status: PlayerStatus = PlayerStatus.ACTIVE
)