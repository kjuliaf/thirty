package se.umu.cs.ens21jfg.thirty

import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult

/**
 * @author Julia Forsberg
 * @version 1.0
 *
 * GameModeDialog is a dialog for selecting a game mode. Extends the DialogFragment class.
 */
class GameModeDialog : DialogFragment() {
    /**
     * Creates a new dialog for selecting a game mode, as a dialog fragment.
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val id = requireArguments().getInt("id")
        val title = requireArguments().getString("title")
        val items = requireArguments().getStringArray("items")!!
        val played = requireArguments().getBooleanArray("played")!!

        val selectableModesAdapter = createSelectableModesAdapter(items, played)
        return buildDialog(title, played, selectableModesAdapter)
    }

    /**
     * Creates an adapter for the dialog game modes selection list.
     * @param items The game modes to select from.
     * @param played Whether each game mode has already been played.
     */
    private fun createSelectableModesAdapter(items: Array<String>, played: BooleanArray): ArrayAdapter<String> {
        return object : ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, items.toList()) {
            // User cannot choose an item that has already been played
            override fun isEnabled(position: Int): Boolean {
                return !played[position]
            }
            // Get the item text and add a checkmark if it has been played
            override fun getView(position: Int, convertView: android.view.View?, parent: ViewGroup): android.view.View {
                val view = super.getView(position, convertView, parent)
                if (played[position]) {
                    view.isEnabled = false
                    view.alpha = 0.5f
                    "${items[position]} ✓".also { (view as TextView).text = it }
                } else {
                    view.alpha = 1.0f
                }
                return view
            }
        }
    }

    /**
     * Builds the dialog.
     * @param title The title of the dialog.
     * @param played Whether each game mode has already been played.
     * @param selectableModesAdapter The adapter for the dialog game modes selection list.
     */
    private fun buildDialog(title: String?, played: BooleanArray, selectableModesAdapter: ArrayAdapter<String>): AlertDialog {
        return AlertDialog.Builder(requireActivity())
            .setTitle(title)
            .setAdapter(selectableModesAdapter) { _, which ->
                if (!played[which]) {
                    val result = Bundle().apply {
                        putInt("item", which)
                    }
                    setFragmentResult(ITEM_REQUEST_KEY, result)
                }
            }
            .create()
    }

    /**
     * Companion object for the GameModeDialog class.
     */
    companion object {
        const val ITEM_REQUEST_KEY = "item_request"

        fun newInstance(id: Int, title: String, items: Array<String>, played: BooleanArray): GameModeDialog {
            return GameModeDialog().apply {
                arguments = Bundle().apply {
                    putInt("id", id)
                    putString("title", title)
                    putStringArray("items", items)
                    putBooleanArray("played", played)
                }
            }
        }
    }
}
