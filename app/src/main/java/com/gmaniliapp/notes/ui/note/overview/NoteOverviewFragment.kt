package com.gmaniliapp.notes.ui.note.overview

import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER
import android.graphics.Canvas
import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmaniliapp.notes.R
import com.gmaniliapp.notes.ui.BaseFragment
import com.gmaniliapp.notes.ui.note.overview.adapter.NoteAdapter
import com.gmaniliapp.notes.util.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_note_overview.*

@AndroidEntryPoint
class NoteOverviewFragment : BaseFragment(R.layout.fragment_note_overview) {

    private lateinit var noteAdapter: NoteAdapter

    private val viewModel: NoteOverviewViewModel by viewModels()

    private val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
        0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    ) {

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                isSwipingItem.postValue(isCurrentlyActive)
            }
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.layoutPosition
            val note = noteAdapter.notes[position]
            viewModel.deleteNoteById(note.id)
        }
    }

    private val isSwipingItem = MutableLiveData(false)

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
                when (result.status) {
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

        isSwipingItem.observe(viewLifecycleOwner, Observer {
            swipeRefreshLayout.isEnabled = !it
        })
    }

    private fun setupRecyclerView() = rvNotes.apply {
        noteAdapter = NoteAdapter()
        adapter = noteAdapter
        layoutManager = LinearLayoutManager(context)
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(this)
    }
}