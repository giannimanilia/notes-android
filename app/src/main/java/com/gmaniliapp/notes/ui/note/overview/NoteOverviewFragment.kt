package com.gmaniliapp.notes.ui.note.overview

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.gmaniliapp.notes.R
import com.gmaniliapp.notes.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_note_overview.*

@AndroidEntryPoint
class NoteOverviewFragment : BaseFragment(R.layout.fragment_note_overview) {

    private val viewModel: NoteOverviewViewModel by viewModels()

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

        subscribeToObservers()

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
    }
}