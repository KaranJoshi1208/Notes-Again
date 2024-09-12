package com.karan.notesagain.ui

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.karan.notesagain.R
import com.karan.notesagain.database.Note
import com.karan.notesagain.databinding.ActivityNoteBinding

class NoteActivity : AppCompatActivity() {

    private lateinit var binder: ActivityNoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binder = DataBindingUtil.setContentView(this, R.layout.activity_note)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        var note: Note? = null
        try {
            note = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getSerializableExtra("note_view", Note::class.java)
            } else {
                intent.getSerializableExtra("note_view") as Note?
            }
            if (note != null) {
                binder.apply {
                    titleView.text = note.title
                    dateView.text = note.date
                    labelView.text = note.label
                }
            } else {
                Toast.makeText(
                    this@NoteActivity,
                    "Display Error(Null Object) !",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (_: Exception) {
        }
    }
}