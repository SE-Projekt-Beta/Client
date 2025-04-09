package at.aau.serg.websocketbrokerdemo.ui.theme

object MyLog {
    fun i(tag: String, message: String) {
        println("[$tag] $message")
    }

    fun w(tag: String, message: String) {
        println("[$tag] WARN: $message")
    }
}