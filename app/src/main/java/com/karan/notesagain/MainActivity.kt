package com.karan.notesagain

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.karan.notesagain.adapter.NoteAdapter
import com.karan.notesagain.database.Note
import com.karan.notesagain.databinding.ActivityMainBinding
import com.karan.notesagain.ui.AddNoteActivity
import com.karan.notesagain.viewModel.MainViewModel
import com.karan.notesagain.ui.NoteActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binder : ActivityMainBinding
    private lateinit var adapter : NoteAdapter
    private lateinit var listner : NoteAdapter.NotesItemClickListener
    private lateinit var viewModel : MainViewModel

    private val updateContent : ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if(result.resultCode == Activity.RESULT_OK) {                                            // RESULT_OK = -1

                val note = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    result.data?.getSerializableExtra("note" , Note::class.java)
                } else {
                    result.data?.getSerializableExtra("note") as Note?
                }

                if(note != null) {
                    viewModel.updateNote(note)
                }else{
                    Toast.makeText(this@MainActivity, "Cannot Update !", Toast.LENGTH_SHORT).show()
                }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binder = DataBindingUtil.setContentView(this, R.layout.activity_main, null)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(application)
        )[MainViewModel::class.java]

        initListener()                                                                                 // Initialising Listener Object

        adapter = NoteAdapter(listner)

        binder.apply {                                                                               // Initialising Recycler View
            recyclerView.adapter = adapter
            recyclerView.layoutManager = StaggeredGridLayoutManager(2, LinearLayout.VERTICAL)        // 1 -> VERTICAL
        }

        viewModel.allNotes.observe(
            this,
            Observer { list ->
                list?.let {
                    adapter.updateList(list)
                }
            }
        )


        val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if(result.resultCode == Activity.RESULT_OK) {

                val note = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    result.data?.getSerializableExtra("note" , Note::class.java)
                } else {
                    result.data?.getSerializableExtra("note") as Note?
                }

                if(note != null) {
                    viewModel.insertNote(note)
                }else{
                    Toast.makeText(this@MainActivity, "Insertion Failed !", Toast.LENGTH_SHORT).show()
                }
            }
        }


        binder.searchView.apply {
            clearFocus()
            val textListener = object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if(newText != null) {
                        adapter.filterList(newText.toString())
                    }
                    return true
                }
            } as SearchView.OnQueryTextListener

            setOnQueryTextListener(textListener)

        }

        binder.apply {
            addBtn.setOnClickListener {
                Intent(this@MainActivity, AddNoteActivity::class.java).also {
                    getContent.launch(it)
                }
            }
        }
    }

    private fun initListener() {
        listner = object : NoteAdapter.NotesItemClickListener {

            override fun onItemClicked(note: Note) {
                Intent(this@MainActivity, NoteActivity::class.java).also {
                    it.putExtra("note_view", note)
                    startActivity(it)
                }
            }

            override fun onItemLongClicked(note: Note, card: CardView) {
                val popup = PopupMenu(this@MainActivity, card)
                popup.inflate(R.menu.popup_menu)
                popup.setOnMenuItemClickListener { item ->
                    when(item.itemId) {
                        (R.id.delBtn) -> viewModel.deleteNote(note)
                        (R.id.updateBtn) -> Intent(this@MainActivity , AddNoteActivity::class.java).also {
                            it.putExtra("old_note", note)
                            updateContent.launch(it)
                        }
                    }
                    true
                }
                popup.show()
            }
        }
    }
}