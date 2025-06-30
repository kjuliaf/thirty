package se.umu.cs.ens21jfg.thirty

import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult

class ListAlertDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val id = requireArguments().getInt("id")
        val title = requireArguments().getString("title")
        val items = requireArguments().getStringArray("items")!!
        val played = requireArguments().getBooleanArray("played")!!

        val adapter = object : ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, items.toList()) {
            override fun isEnabled(position: Int): Boolean {
                return !played[position]
            }

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

        return AlertDialog.Builder(requireActivity())
            .setTitle(title)
            .setAdapter(adapter) { _, which ->
                if (!played[which]) {
                    val result = Bundle().apply {
                        putInt("item", which)
                    }
                    setFragmentResult(ITEM_REQUEST_KEY, result)
                }
            }
            .create()
    }


    companion object {
        const val ITEM_REQUEST_KEY = "item_request"

        fun newInstance(id: Int, title: String, items: Array<String>, played: BooleanArray): ListAlertDialogFragment {
            return ListAlertDialogFragment().apply {
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
