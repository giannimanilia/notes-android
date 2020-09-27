package com.gmaniliapp.notes.ui.note.overview

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.gmaniliapp.notes.R
import com.gmaniliapp.notes.ui.BaseFragment
import kotlinx.android.synthetic.main.fragment_note_overview.*

class NoteOverviewFragment : BaseFragment(R.layout.fragment_note_overview) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fabAddNote.setOnClickListener {
            findNavController().navigate(
                NoteOverviewFragmentDirections.actionNoteOverviewFragmentToModifyNoteFragment(
                    ""
                )
            )
        }
    }
}