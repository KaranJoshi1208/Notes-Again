package com.karan.notesagain.viewModel

import android.app.Application
import android.widget.EditText
import androidx.lifecycle.AndroidViewModel
import com.karan.notesagain.repository.TextEditHelper

class AddNoteViewModel(application: Application) : AndroidViewModel(application) {


    var isUpdating = false
    var dateTime: String? = null

    /**
     * to implement undo/redo functionalities in respective EditView.
     */

    var helper: TextEditHelper? = null

    fun initHelper(editView: EditText) {
        if (helper == null) {
            helper = TextEditHelper(editView)
        }
    }

}