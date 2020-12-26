package com.gmaniliapp.notes.ui.note.detail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.gmaniliapp.notes.R
import com.gmaniliapp.notes.data.local.entities.Note
import com.gmaniliapp.notes.ui.BaseFragment
import com.gmaniliapp.notes.util.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_note_detail.*

@AndroidEntryPoint
class NoteDetailFragment : BaseFragment(R.layout.fragment_note_detail) {

    private val viewModel: NoteDetailViewModel by viewModels()

    private val args: NoteDetailFragmentArgs by navArgs()

    private var currentNote: Note? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (args.id.isNotEmpty()) {
            viewModel.selectNoteById(args.id)
        }

        subscribeToObservers()

        fabEditNote.setOnClickListener {
            findNavController().navigate(
                NoteDetailFragmentDirections.actionNoteDetailFragmentToModifyNoteFragment(args.id)
            )
        }
    }

    private fun subscribeToObservers() {
        viewModel.note.observe(viewLifecycleOwner, Observer {
            it?.getContentIfNotHandled()?.let { result ->
                when (result.status) {
                    Status.SUCCESS -> {
                        result.data?.let {
                            val note = result.data
                            currentNote = note
                            tvNoteTitle.text = note.title
                            tvNoteContent.text = note.content
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
}