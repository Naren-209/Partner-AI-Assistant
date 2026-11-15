package com.partner.assistant.ui.state

enum class ListeningStatus(val label: String) {
    OFF("Off"),
    ARMED("Armed — waiting for wake word"),
    CAPTURING("Listening…"),
    PROCESSING("Processing…")
}