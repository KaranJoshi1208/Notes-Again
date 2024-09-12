package com.karan.notesagain.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Note::class],
    version = 1
)
abstract class NoteDB : RoomDatabase() {

    abstract fun getNoteDao(): NoteDao

    companion object {

        @Volatile
        private var INSTANCE: NoteDB? = null

        fun getInstance(context: Context): NoteDB {
            return INSTANCE ?: synchronized(this) {
                val db = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDB::class.java,
                    "note_db"
                ).build()
                INSTANCE = db
                return db
            }
        }
    }
}