package com.karan.notesagain.adapter

import android.graphics.Color
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.karan.notesagain.R
import com.karan.notesagain.database.Note
import com.karan.notesagain.databinding.ListItemBinding
import kotlin.random.Random

class NoteAdapter(
    private val listener : NotesItemClickListener
) : RecyclerView.Adapter<NoteAdapter.NoteHolder>() {

    private val allNotes = ArrayList<Note>()

    private val displayNotes = ArrayList<Note>()

    private val colors : Array<Int> = arrayOf(
        Color.parseColor("#FADADD"),
        Color.parseColor("#D3D3D3"),
        Color.parseColor("#E6E6FA"),
        Color.parseColor("#D3D3D3"),
        Color.parseColor("#FFFDD0"),
        Color.parseColor("#FFDAB9"),
        Color.parseColor("#FFFACD"),
        Color.parseColor("#C5CBE1"),
        Color.parseColor("#D8BFD8"),
        Color.parseColor("#FFF5EE"),
        Color.parseColor("#F0FFF0"),
        Color.parseColor("#FFFFF0"),
        Color.parseColor("#FFF0F5")
        )

    private val random = Random(SystemClock.elapsedRealtime())

    inner class NoteHolder(var binder : ListItemBinding) : RecyclerView.ViewHolder(binder.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
        val binding : ListItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_item,
            parent,
            false
        )
        return NoteHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteHolder, position: Int) {
        val note = displayNotes[position]
        holder.binder.apply {
            titleTxt.text = note.title
            labelTxt.text = note.label
            dateTxt.text = note.date

            noteLayout.setCardBackgroundColor(randomColour())

            noteLayout.setOnClickListener {
                listener.onItemClicked(displayNotes[holder.adapterPosition])
            }

            noteLayout.setOnLongClickListener {
                listener.onItemLongClicked(displayNotes[holder.adapterPosition], noteLayout)
                true
            }

        }

    }

    override fun getItemCount(): Int = displayNotes.size                                             // Will give error in Filtering list , if you set it to "allNotes.size"



    fun updateList( newList : List<Note>) {
        allNotes.clear()
        allNotes.addAll(newList)

        displayNotes.clear()
        displayNotes.addAll(newList)
        notifyDataSetChanged()

    }


    fun filterList(search : String) {
        displayNotes.clear()
        for (item in allNotes) {
            if(item.title.lowercase().contains(search.lowercase()) || item.label.lowercase().contains(search.lowercase())) {
                displayNotes.add(item)
            }
        }
        notifyDataSetChanged()
    }

    private fun randomColour() : Int {
        return colors[random.nextInt(colors.size)].also {
            Log.d("randomColor", "$it")
        }
    }


    interface NotesItemClickListener {

        fun onItemClicked(note : Note)

        fun onItemLongClicked(note : Note, card : CardView)
    }
}