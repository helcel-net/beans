package net.helcel.beendroid.activity.fragment

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.helcel.beendroid.R
import net.helcel.beendroid.activity.adapter.GeolocListAdapter
import net.helcel.beendroid.activity.adapter.GroupListAdapter


class EditPlaceColorFragment(private val parent: GeolocListAdapter.FoldingListViewHolder) : DialogFragment() {
    private lateinit var listAdapt : GroupListAdapter
    private lateinit var list : RecyclerView
    private var clear : Boolean = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(
            requireActivity()
        )
        val inflater = requireActivity().layoutInflater
        val view: View = inflater.inflate(R.layout.fragment_edit_places_colors, null)

        val dialog = builder.setView(view).setNegativeButton("Clear") { dialogInterface: DialogInterface, i: Int -> clear = true }
            .create()
        listAdapt = GroupListAdapter(requireActivity(),this)
        list = view.findViewById(R.id.groups_color)!!
        list.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        list.adapter = listAdapt

        return dialog
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        parent.onColorDialogDismiss(clear)
    }
}
