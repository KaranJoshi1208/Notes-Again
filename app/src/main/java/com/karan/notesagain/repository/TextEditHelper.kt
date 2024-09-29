package com.karan.notesagain.repository

import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import java.util.LinkedList

class TextEditHelper(private val editView: EditText) {
    
    /**
     * To observe weather an undo/redo task is already taking place.
     */

    var inAction = false

    val editor = Editor()

    private val textWatcher = object : TextWatcher {

        var beforeText = ""
        var afterText = ""

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            if (inAction) return
            beforeText = s?.subSequence(start, start + count).toString()
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (inAction) return
            afterText = s?.subSequence(start, start + count).toString()
            val temp = Edit(start, beforeText, afterText)
            editor.addEdit(temp)
//            Log.d(
//                "textChanged",
//                "start position : ${temp.start}, Before : ${temp.beforeText} , After : ${temp.afterText}"
//            )
        }

        override fun afterTextChanged(s: Editable?) {
        }
    }

    init {
        editView.addTextChangedListener(textWatcher)
    }

    fun undo() {
        val item: Edit = editor.getPreviousEdit() ?: return

        val start: Int = item.start
        val end: Int = start + item.afterText.length
        val text = editView.editableText

        inAction = true
        text.replace(start, end, item.beforeText)
        inAction = false
        Selection.setSelection(
            text,
            if (item.beforeText.isEmpty()) 0 else start + item.beforeText.length
        )
    }

    fun redo() {
        val item: Edit = editor.getEdit() ?: return

        val start = item.start
        val end = start + item.beforeText.length
        val text = editView.editableText

        inAction = true
        text.replace(start, end, item.afterText)
        inAction = false

        Selection.setSelection(
            text,
            if (item.afterText.isEmpty()) 0 else start + item.afterText.length
        )
    }

    fun clearHistory() {
        editor.clear()
    }


    inner class Edit(var start: Int = 0, var beforeText: String = "", var afterText: String = "")


    inner class Editor {

        private var position = 0

        private val editList = LinkedList<Edit>()


        fun clear() {
            position = 0
            editList.clear()
        }

        fun addEdit(edit: Edit) {
            while (editList.size > position) {
                editList.removeLast()
            }
            editList.add(edit)
            position++
//            Log.d("linkedList", "$editList")
        }

        fun getEdit(): Edit? {
            if (position >= editList.size) return null
            return editList[position++]
        }

        fun getPreviousEdit(): Edit? {
            if (position < 1) return null
            position--
            return editList[position]
        }
    }
}