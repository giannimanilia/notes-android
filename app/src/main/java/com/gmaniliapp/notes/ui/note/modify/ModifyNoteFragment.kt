package com.gmaniliapp.notes.ui.note.modify

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.gmaniliapp.notes.R
import com.gmaniliapp.notes.data.local.entities.Note
import com.gmaniliapp.notes.ui.BaseFragment
import com.gmaniliapp.notes.ui.dialog.ColorPickerDialogFragment
import com.gmaniliapp.notes.util.Constants.DEFAULT_NOTE_COLOR
import com.gmaniliapp.notes.util.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_modify_note.*
import java.util.*

const val FRAGMENT_TAG = "ModifyNoteFragment"

@AndroidEntryPoint
class ModifyNoteFragment : BaseFragment(R.layout.fragment_modify_note) {

    private val viewModel: ModifyNoteViewModel by viewModels()

    private val args: ModifyNoteFragmentArgs by navArgs()

    private var currentNote: Note? = null

    private var currentNoteColor = DEFAULT_NOTE_COLOR

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (args.id.isNotEmpty()) {
            viewModel.selectNoteById(args.id)
        }

        subscribeToObservers()

        if (savedInstanceState != null) {
            val colorPickerDialog =
                parentFragmentManager.findFragmentByTag(FRAGMENT_TAG) as ColorPickerDialogFragment?
            colorPickerDialog?.setPositiveListener {
                changeViewNoteColor(it)
            }
        }

        viewNoteColor.setOnClickListener {
            ColorPickerDialogFragment().apply {
                setPositiveListener {
                    changeViewNoteColor(it)
                }
            }.show(parentFragmentManager, FRAGMENT_TAG)
        }
    }

    override fun onPause() {
        super.onPause()
        saveNote()
    }

    private fun subscribeToObservers() {
        viewModel.note.observe(viewLifecycleOwner, Observer {
            it?.getContentIfNotHandled()?.let { result ->
                when (result.status) {
                    Status.SUCCESS -> {
                        result.data?.let {
                            val note = result.data
                            currentNote = note
                            etNoteTitle.setText(note.title)
                            etNoteContent.setText(note.content)
                            changeViewNoteColor(note.color)
                        }
                    }
                    Status.ERROR -> {
                        showSnackBar("Unexpected error")
                    }
                    Status.LOADING -> {

                    }
                }
            }
        })
    }

    private fun saveNote() {
        val title = etNoteTitle.text.toString()
        val content = etNoteContent.text.toString()
        if (title.isEmpty()) {
            showSnackBar("Title cannot be empty")
            return
        } else if (content.isEmpty()) {
            showSnackBar("Content cannot be empty")
            return
        }

        val note = Note(
            id = currentNote?.id ?: UUID.randomUUID().toString(),
            title = title,
            content = content,
            date = System.currentTimeMillis(),
            color = currentNoteColor,
            owners = currentNote?.owners ?: listOf(viewModel.getLoggedInEmail())
        )

        if (currentNote != null) {
            viewModel.updateNote(note)
        } else {
            viewModel.insertNote(note)
        }
    }

    private fun changeViewNoteColor(colorString: String) {
        val drawable = ResourcesCompat.getDrawable(resources, R.drawable.circle_shape, null)
        drawable?.let {
            val wrappedDrawable = DrawableCompat.wrap(it)
            val color = Color.parseColor("#${colorString}")
            DrawableCompat.setTint(wrappedDrawable, color)
            viewNoteColor.background = wrappedDrawable
            currentNoteColor = colorString
        }
    }
}