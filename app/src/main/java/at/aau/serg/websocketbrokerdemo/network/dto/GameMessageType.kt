package at.aau.serg.websocketbrokerdemo.network.dto

enum class GameMessageType {
    ROLL_DICE,
    PLAYER_MOVED,
    CAN_BUY_PROPERTY,
    BUY_PROPERTY,
    PROPERTY_BOUGHT,
    EVENT_CARD_DRAWN,
    RISK_CARD_DRAWN,
    PAY_TAX,
    GO_TO_JAIL,
    SKIPPED,
    MUST_PAY_RENT,
    ERROR,
    REQUEST_GAME_STATE,
    GAME_STATE,
    CURRENT_PLAYER,
    PLAYER_UPDATE
}