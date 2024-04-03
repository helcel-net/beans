package net.helcel.beans.activity.fragment

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import net.helcel.beans.activity.adapter.GeolocListAdapter
import net.helcel.beans.activity.adapter.GroupListAdapter
import net.helcel.beans.databinding.FragmentEditPlacesColorsBinding
import net.helcel.beans.helper.Data


class EditPlaceColorFragment(private val parent: GeolocListAdapter.FoldingListViewHolder) :
    DialogFragment() {

    private lateinit var _binding: FragmentEditPlacesColorsBinding
    private lateinit var listAdapt: GroupListAdapter
    private var clear: Boolean = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = MaterialAlertDialogBuilder(requireContext())
        _binding = FragmentEditPlacesColorsBinding.inflate(layoutInflater)
        _binding.btnAdd.setOnClickListener {
            EditGroupAddFragment(0, {
                listAdapt.notifyItemInserted(Data.groups.findGroupPos(it))
            }, {}).show(requireActivity().supportFragmentManager, "AddColorDialogFragment")
        }
        _binding.btnClear.setOnClickListener {
            clear = true
            dialog?.dismiss()
        }

        val dialog = builder.setView(_binding.root).create()
        listAdapt = GroupListAdapter(requireActivity(), this)
        _binding.groupsColor.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        _binding.groupsColor.adapter = listAdapt

        return dialog
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        parent.onColorDialogDismiss(clear)
    }
}
