package com.gmaniliapp.notes.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.gmaniliapp.notes.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_note_detail.*

class AddOwnerDialog : DialogFragment() {

    private var positiveListener: ((String) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val addOwnerEditText = LayoutInflater.from(requireContext()).inflate(
            R.layout.dialog_add_owner,
            clNoteContainer,
            false
        ) as TextInputLayout

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setIcon(R.drawable.ic_add_person)
            .setTitle("Add Owner to Note")
            .setMessage(
                "Enter the email of a person you want to share the note with." +
                        "This person will be able to read and edit the note."
            )
            .setView(addOwnerEditText)
            .setPositiveButton("Add", null)
            .setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.cancel()
            }
            .create()

        dialog.setOnShowListener {
            run {
                val positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                positive.setOnClickListener {
                    val email =
                        addOwnerEditText.findViewById<EditText>(R.id.etAddOwnerEmail).text.toString()
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        addOwnerEditText.findViewById<TextInputLayout>(R.id.tilAddOwnerEmail).error =
                            "Invalid email"
                    } else {
                        positiveListener?.let { yes ->
                            yes(email)
                        }
                        dialog.dismiss()
                    }
                }
            }
        }

        return dialog
    }

    fun setPositiveListener(listener: (String) -> Unit) {
        positiveListener = listener
    }
}