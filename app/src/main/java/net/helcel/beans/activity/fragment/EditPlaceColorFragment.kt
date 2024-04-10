package net.helcel.beans.activity.fragment

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import net.helcel.beans.R
import net.helcel.beans.activity.adapter.GroupListAdapter
import net.helcel.beans.databinding.FragmentEditPlacesColorsBinding
import net.helcel.beans.helper.Data
import net.helcel.beans.helper.DialogCloser


class EditPlaceColorFragment(private val parent: DialogCloser, private val delete: Boolean = false) :
    DialogFragment() {

    private lateinit var _binding: FragmentEditPlacesColorsBinding
    private lateinit var listAdapt: GroupListAdapter
    private var clear: Boolean = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val ctx = requireContext()
        val builder = MaterialAlertDialogBuilder(ctx)
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
        listAdapt = GroupListAdapter(requireActivity(), this, delete)
        _binding.groupsColor.layoutManager =
            LinearLayoutManager(ctx, RecyclerView.VERTICAL, false)
        _binding.groupsColor.adapter = listAdapt

        if (delete) {
            _binding.btnAdd.visibility = View.GONE
            _binding.btnClear.text = ctx.getString(R.string.cancel)
            _binding.warningText.text = ctx.getString(R.string.select_group)
        } else {
            _binding.warningText.text = ctx.getString(R.string.edit_group)
        }

        return dialog
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        parent.onDialogDismiss(clear)
    }
}
