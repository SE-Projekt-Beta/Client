package at.aau.serg.websocketbrokerdemo.network.dto

enum class GameMessageType {
    GAME_STATE,
    ROLL_DICE,
    PLAYER_MOVED,
    CAN_BUY_PROPERTY,
    BUY_PROPERTY,
    PROPERTY_BOUGHT,
    EVENT_CARD_DRAWN,
    CASH_TASK,
    GAME_OVER,
    MUST_PAY_RENT,
    ERROR,
    CURRENT_PLAYER,
    REQUEST_GAME_STATE,
    PLAYER_LOST,
    PAY_RENT,
    DRAW_RISK_CARD,
    DRAW_BANK_CARD,
    PAY_TAX,
    GO_TO_JAIL,
    PASS_START,
    BUILD_HOUSE,
    BUILD_HOTEL
}