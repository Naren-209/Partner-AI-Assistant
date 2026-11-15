package com.partner.assistant.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Changed version from 1 to 2
@Database(entities = [NoteEntity::class, EventEntity::class], version = 2, exportSchema = false)
abstract class PartnerDatabase : RoomDatabase() {
    abstract fun partnerDao(): PartnerDao

    companion object {
        @Volatile
        private var INSTANCE: PartnerDatabase? = null

        fun getDatabase(context: Context): PartnerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PartnerDatabase::class.java,
                    "partner_database"
                )
                    .fallbackToDestructiveMigration() // This prevents crashes if the schema changes!
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}