package at.aau.serg.websocketbrokerdemo.network

enum class MessageType {
    JOIN_LOBBY,
    START_GAME,
    ROLL_DICE,
    PLAYER_MOVED,
    CAN_BUY_PROPERTY,
    BUY_PROPERTY,
    PROPERTY_BOUGHT,
    DRAW_EVENT_RISIKO_CARD,
    DRAW_EVENT_BANK_CARD,
    PAY_TAX,
    GO_TO_JAIL,
    SKIPPED,
    MUST_PAY_RENT,
    LOBBY_UPDATE,
    ERROR
}