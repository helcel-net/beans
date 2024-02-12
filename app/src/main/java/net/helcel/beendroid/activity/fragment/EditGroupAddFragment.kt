package net.helcel.beendroid.activity.fragment

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import net.helcel.beendroid.R
import net.helcel.beendroid.helper.colorToHex6
import net.helcel.beendroid.helper.groups


class EditGroupAddFragment(private val key: Int =0, val onAddCb : (Int)->Unit) : DialogFragment() {
    private var colorNameEditText: EditText? = null
    private var colorEditText: EditText? = null
    private val grp = groups!!.getGroupFromKey(key)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(
            requireActivity()
        )
        val inflater = requireActivity().layoutInflater
        val view: View = inflater.inflate(R.layout.fragment_edit_groups_add, null)

        colorNameEditText = view.findViewById(R.id.group_name)
        colorEditText = view.findViewById(R.id.group_color)

        if(grp!=null){
            view.findViewById<EditText>(R.id.group_name).setText(grp.first)
            view.findViewById<EditText>(R.id.group_color).setText(colorToHex6(grp.second))
        }
        builder.setView(view)
            .setTitle("Add Color")
            .setPositiveButton("Add") { _: DialogInterface?, _: Int ->
                val name = colorNameEditText!!.text.toString()
                val color = colorEditText!!.text.toString()
                val key = (if (key!=0) key else groups!!.genKey())
                groups!!.setGroup(key,name, ColorDrawable(Color.parseColor(color)))
                onAddCb(key)
            }
            .setNegativeButton(
                "Cancel"
            ) { dialog: DialogInterface, _: Int -> dialog.cancel() }
        return builder.create()
    }
}