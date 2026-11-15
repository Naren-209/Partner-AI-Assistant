package com.partner.assistant.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val action: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis()
)