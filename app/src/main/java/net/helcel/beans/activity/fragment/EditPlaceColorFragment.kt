package net.helcel.beans.activity.fragment

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.helcel.beans.R
import net.helcel.beans.activity.adapter.GeolocListAdapter
import net.helcel.beans.activity.adapter.GroupListAdapter
import net.helcel.beans.helper.Data


class EditPlaceColorFragment(private val parent: GeolocListAdapter.FoldingListViewHolder) :
    DialogFragment() {
    private lateinit var listAdapt: GroupListAdapter
    private lateinit var list: RecyclerView
    private var clear: Boolean = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(
            requireActivity()
        )
        val inflater = requireActivity().layoutInflater
        val view: View = inflater.inflate(R.layout.fragment_edit_places_colors, null)

        val btnAdd: AppCompatButton = view.findViewById(R.id.btnAdd)
        val btnClear: AppCompatButton = view.findViewById(R.id.btnClear)
        btnAdd.setOnClickListener {
            EditGroupAddFragment(0, {
                listAdapt.notifyItemInserted(Data.groups.findGroupPos(it))
            }, {}).show(requireActivity().supportFragmentManager, "AddColorDialogFragment")
        }
        btnClear.setOnClickListener {
            clear = true
            dialog?.dismiss()
        }

        val dialog = builder.setView(view).create()
        listAdapt = GroupListAdapter(requireActivity(), this)
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
