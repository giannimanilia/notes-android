package com.gmaniliapp.notes.ui.note.detail

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.gmaniliapp.notes.R
import com.gmaniliapp.notes.data.local.entities.Note
import com.gmaniliapp.notes.ui.BaseFragment
import com.gmaniliapp.notes.ui.dialog.AddOwnerDialog
import com.gmaniliapp.notes.util.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_note_detail.*

const val ADD_OWNER_DIALOG_TAG = "ADD_OWNER_DIALOG_TAG"

@AndroidEntryPoint
class NoteDetailFragment : BaseFragment(R.layout.fragment_note_detail) {

    private val viewModel: NoteDetailViewModel by viewModels()

    private val args: NoteDetailFragmentArgs by navArgs()

    private var currentNote: Note? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

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

        if (savedInstanceState != null) {
            val addOwnerDialog = parentFragmentManager.findFragmentByTag(ADD_OWNER_DIALOG_TAG)
                    as AddOwnerDialog?
            addOwnerDialog?.setPositiveListener {
                addOwnerToCurNote(it)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_note_detail, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.add_owner -> showAddOwnerDialog()
        }
        return super.onOptionsItemSelected(item)
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

        viewModel.addOwnerStatus.observe(viewLifecycleOwner, Observer { event ->
            event?.getContentIfNotHandled()?.let { result ->
                when(result.status) {
                    Status.SUCCESS -> {
                        addOwnerProgressBar.visibility = View.GONE
                        showSnackBar(result.data ?: "Successfully added owner to note")
                    }
                    Status.ERROR -> {
                        addOwnerProgressBar.visibility = View.GONE
                        showSnackBar(result.data ?: "An unknown error occurred")
                    }
                    Status.LOADING -> {
                        addOwnerProgressBar.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun showAddOwnerDialog() {
        AddOwnerDialog().apply {
            setPositiveListener {
                addOwnerToCurNote(it)
            }
        }.show(parentFragmentManager, ADD_OWNER_DIALOG_TAG)
    }

    private fun addOwnerToCurNote(email: String) {
        currentNote?.let { note ->
            viewModel.addOwnerToNote(email, note.id)
        }
    }
}