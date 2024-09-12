package com.karan.notesagain.repository

import androidx.lifecycle.LiveData
import com.karan.notesagain.database.Note
import com.karan.notesagain.database.NoteDao

class NoteRepo(private val dao: NoteDao) {

    val allNotes: LiveData<List<Note>> = dao.getAllNotes()

    suspend fun insert(note: Note): Unit = dao.insertNote(note)

    suspend fun delete(note: Note): Unit = dao.deleteNote(note)

    suspend fun update(note: Note): Unit = dao.updateNote(note)
}