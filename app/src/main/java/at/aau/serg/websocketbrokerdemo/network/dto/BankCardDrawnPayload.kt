package at.aau.serg.websocketbrokerdemo.network.dto

data class BankCardDrawnPayload(
    val playerId: Int,
    val amount: Int,
    val newCash: Int,
    val description: String
)
