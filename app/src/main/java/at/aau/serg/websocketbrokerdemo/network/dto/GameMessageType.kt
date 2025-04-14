package at.aau.serg.websocketbrokerdemo.network.dto

enum class GameMessageType {
    ROLL_DICE,
    PLAYER_MOVED,
    CAN_BUY_PROPERTY,
    BUY_PROPERTY,
    PROPERTY_BOUGHT,
    DRAW_EVENT_BANK_CARD,
    DRAW_EVENT_RISIKO_CARD,
    PAY_TAX,
    GO_TO_JAIL,
    SKIPPED,
    MUST_PAY_RENT,
    ERROR
}