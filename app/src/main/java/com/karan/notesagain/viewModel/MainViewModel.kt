package com.karan.notesagain.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.karan.notesagain.database.Note
import com.karan.notesagain.database.NoteDB
import com.karan.notesagain.repository.NoteRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repo: NoteRepo
    val allNotes: LiveData<List<Note>>

    init {
        val dao = NoteDB.getInstance(application).getNoteDao()
        repo = NoteRepo(dao)
        allNotes = repo.allNotes
    }

    fun deleteNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repo.delete(note)
    }

    fun updateNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repo.update(note)
    }

    fun insertNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repo.insert(note)
    }

}