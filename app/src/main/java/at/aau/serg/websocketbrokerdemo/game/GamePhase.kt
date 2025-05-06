package at.aau.serg.websocketbrokerdemo.game

enum class GamePhase {
    WAITING_FOR_TURN,
    ROLLING_DICE,
    MOVING,
    EVENT,
    BUYING,
    END_TURN
}