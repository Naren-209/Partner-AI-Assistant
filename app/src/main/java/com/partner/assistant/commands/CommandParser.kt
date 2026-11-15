package com.partner.assistant.commands

class CommandParser {

    fun parse(text: String): String {
        val lowerText = text.lowercase()

        return when {
            lowerText.contains("flashlight") || lowerText.contains("torch") -> "ACTION_FLASHLIGHT"
            lowerText.contains("volume") || lowerText.contains("sound") -> "ACTION_VOLUME"
            lowerText.contains("open") && lowerText.contains("youtube") -> "ACTION_OPEN_YOUTUBE"
            lowerText.startsWith("call ") -> "ACTION_CALL"
            lowerText.startsWith("set an alarm") || lowerText.startsWith("set alarm") -> "ACTION_ALARM"
            lowerText.startsWith("note ") || lowerText.startsWith("take a note ") -> "ACTION_NOTE"
            lowerText.contains("hello") || lowerText.contains("hi") -> "ACTION_GREETING"
            else -> "ACTION_UNKNOWN"
        }
    }

    fun extractVolumeLevel(text: String): Int {
        val match = Regex("(\\d+)").find(text)
        return match?.value?.toIntOrNull() ?: 50
    }

    fun extractNumber(text: String): String {
        // Replaces words like "five" with "5" and removes non-numbers
        val numberMap = mapOf("zero" to "0", "one" to "1", "two" to "2", "three" to "3", "four" to "4",
            "five" to "5", "six" to "6", "seven" to "7", "eight" to "8", "nine" to "9")
        var cleaned = text.lowercase()
        numberMap.forEach { (word, digit) -> cleaned = cleaned.replace(word, digit) }
        return cleaned.filter { it.isDigit() || it == '+' }
    }

    fun extractNote(text: String): String {
        return text.replace("take a note", "", ignoreCase = true)
            .replace("note", "", ignoreCase = true)
            .trim()
    }

    fun extractHour(text: String): Int {
        val match = Regex("(\\d+)").find(text)
        return match?.value?.toIntOrNull() ?: 7
    }
}