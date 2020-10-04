package com.gmaniliapp.notes.ui.note.overview

import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER
import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gmaniliapp.notes.R
import com.gmaniliapp.notes.ui.BaseFragment
import com.gmaniliapp.notes.ui.note.overview.adapter.NoteAdapter
import com.gmaniliapp.notes.util.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_note_overview.*

@AndroidEntryPoint
class NoteOverviewFragment : BaseFragment(R.layout.fragment_note_overview) {

    private val viewModel: NoteOverviewViewModel by viewModels()

    private lateinit var noteAdapter: NoteAdapter

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

        requireActivity().requestedOrientation = SCREEN_ORIENTATION_USER

        setupRecyclerView()

        subscribeToObservers()

        noteAdapter.setOnItemClickListener {
            findNavController().navigate(
                NoteOverviewFragmentDirections.actionNoteOverviewFragmentToNoteDetailFragment(it.id)
            )
        }

        fabAddNote.setOnClickListener {
            findNavController().navigate(
                NoteOverviewFragmentDirections.actionNoteOverviewFragmentToModifyNoteFragment(
                    ""
                )
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_note_overview, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> viewModel.logout()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun subscribeToObservers() {
        viewModel.navigateToLogout.observe(viewLifecycleOwner, Observer { navigate ->
            navigate?.let {
                when (navigate) {
                    true -> {
                        val navigationOptions = NavOptions.Builder()
                            .setPopUpTo(R.id.noteOverviewFragment, true)
                            .build()

                        findNavController().navigate(
                            NoteOverviewFragmentDirections.actionNoteOverviewFragmentToAuthFragment(),
                            navigationOptions
                        )

                        viewModel.displayLogoutCompleted()
                    }
                    false -> {
                    }
                }
            }
        })

        viewModel.allNotes.observe(viewLifecycleOwner, Observer {
            it?.let { event ->
                val result = event.getContent()
                when(result.status) {
                    Status.SUCCESS -> {
                        result.data?.let { notes ->
                            noteAdapter.notes = notes
                        }
                        swipeRefreshLayout.isRefreshing = false
                    }
                    Status.ERROR -> {
                        event.getContentIfNotHandled()?.let { error ->
                            showSnackBar("Error communicating with server")
                        }
                        result.data?.let { notes ->
                            noteAdapter.notes = notes
                        }
                        swipeRefreshLayout.isRefreshing = false
                    }
                    Status.LOADING -> {
                        result.data?.let { notes ->
                            noteAdapter.notes = notes
                        }
                        swipeRefreshLayout.isRefreshing = true
                    }
                }

            }
        })
    }

    private fun setupRecyclerView() = rvNotes.apply {
        noteAdapter = NoteAdapter()
        adapter = noteAdapter
        layoutManager = LinearLayoutManager(context)
    }
}