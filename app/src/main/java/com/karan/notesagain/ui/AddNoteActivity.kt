package com.karan.notesagain.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.window.OnBackInvokedCallback
import android.window.OnBackInvokedDispatcher
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.karan.notesagain.R
import com.karan.notesagain.database.Note
import com.karan.notesagain.databinding.ActivityAddNoteBinding
import com.karan.notesagain.viewModel.AddNoteViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddNoteActivity : AppCompatActivity() {

    private lateinit var binder: ActivityAddNoteBinding
    private lateinit var viewModel: AddNoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binder = DataBindingUtil.setContentView(this, R.layout.activity_add_note)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            onBackPressedDispatcher.addCallback {
                wannaExit {
                    viewModel.helper?.clearHistory()
                    finish()
                }
            }
        }

        viewModel = ViewModelProvider(
            this@AddNoteActivity,
            ViewModelProvider.AndroidViewModelFactory(application)
        )[AddNoteViewModel::class.java]


        var oldNote: Note? = null                                                                    //  We can store this in the ViewModel but this will also work properly
        try {
            oldNote = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getSerializableExtra("old_note", Note::class.java)
            } else {
                intent.getSerializableExtra("old_note") as Note?
            }

        } catch (_: Exception) {}

        if (oldNote != null) {
            viewModel.isUpdating = true
            binder.apply {
                editTitle.setText(oldNote.title)
                editLabel.setText(oldNote.label)
                dateTxt.text = oldNote.date
            }

        } else {

            /**
             *why a if(viewModel.dateTime == null) condition ?

             *because if screen orientation or theme is changed while the user is on this activity,
             *the time will be updated,
             *which should not happen.
             */

            if (viewModel.dateTime == null) {
                viewModel.dateTime = SimpleDateFormat(
                    "EEEE, d MMM yyyy, HH:mm a",
                    Locale.getDefault()
                ).format(Date())
            }
            binder.dateTxt.text = viewModel.dateTime
        }

        viewModel.initHelper(binder.editLabel)                                                        // initialising undo redo helper

        binder.backBtn.setOnClickListener {
            // Alert Dialog to confirm leaving
            wannaExit {
                viewModel.helper?.clearHistory()
                finish()
            }
        }

        binder.apply {
            editTitle.setOnEditorActionListener { v, _, _ ->
                if (v != null && v.toString().isNotEmpty()) {
                    saveBtn.visibility = View.VISIBLE
                } else {
                    saveBtn.visibility = View.INVISIBLE
                }
                true
            }
        }

        binder.apply {
            undoBtn.setOnClickListener {
                viewModel.helper?.undo()
            }

            redoBtn.setOnClickListener {
                viewModel.helper?.redo()
            }
        }

        binder.apply {
            saveBtn.setOnClickListener {

                if (viewModel.isUpdating) {
                    Intent().apply {
                        putExtra("note", oldNote?.apply {
                            title = editTitle.text.toString()
                            label = editLabel.text.toString()
                        })
                        setResult(Activity.RESULT_OK, this)
                    }
                } else {
                    val title = editTitle.text.toString()
                    val label = editLabel.text.toString()
                    val date =
                        // use DateTimeFormatter for API 26 and above
                        SimpleDateFormat("EEEE, d MMM yyyy HH:mm a", Locale.getDefault()).format(
                            Date()
                        )

//                    Log.d("Checking", "$date , $title , $label")

                    Intent().apply {
                        putExtra("note", Note(0, title, label, date))
                        setResult(Activity.RESULT_OK, this)
                    }
                }
                finish()
            }
        }
    }


    fun wannaExit(yes: () -> Unit) {

        val alert = AlertDialog.Builder(this@AddNoteActivity).apply {
            setTitle("Exit ?")
            setMessage("The content will be deleted. Are you sure you want to exit ?")
            setPositiveButton("Yes") { dialog, _ ->
                dialog.dismiss()
                yes()                                                                                // finish activity
            }
            setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        }.create()

        alert.setOnShowListener {
            alert.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(ContextCompat.getColor(this, R.color.black))
            alert.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(ContextCompat.getColor(this, R.color.black))
        }

        alert.show()
    }

    @Deprecated("sup...")
    override fun onBackPressed() {
        wannaExit {
            finish()
        }
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            onBackInvokedDispatcher.unregisterOnBackInvokedCallback {
                wannaExit {
                    viewModel.helper?.clearHistory()
                    finish()
                }
            }
        }
    }
}