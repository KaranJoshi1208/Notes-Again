package com.karan.notesagain.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var title: String,
    var label: String,
    val date: String
) : Serializable