package net.helcel.beans.activity.fragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.TextInputEditText
import net.helcel.beans.R
import net.helcel.beans.databinding.FragmentEditGroupsAddBinding
import net.helcel.beans.helper.Data
import net.helcel.beans.helper.Groups
import net.helcel.beans.helper.Theme.colorToHex6


class EditGroupAddFragment(
    private val key: Int = 0,
    val onAddCb: (Int) -> Unit,
    val onDelCb: (Int) -> Unit,
    private val deleteEnabled: Boolean = true
) : DialogFragment() {

    private lateinit var _binding: FragmentEditGroupsAddBinding
    private val grp = Data.groups.getGroupFromKey(key)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = MaterialAlertDialogBuilder(requireContext())
        _binding = FragmentEditGroupsAddBinding.inflate(layoutInflater)

        setupSlider(_binding.colorR, grp.color.color.red / 255F)
        setupSlider(_binding.colorG, grp.color.color.green / 255F)
        setupSlider(_binding.colorB, grp.color.color.blue / 255F)
        setupText(_binding.groupColor, grp)

        _binding.colorView.background = ColorDrawable(grp.color.color)


        if (key == 0 || !deleteEnabled) {
            _binding.btnDelete.visibility = View.INVISIBLE
            _binding.btnDelete.isEnabled = false
        }
        _binding.btnDelete.setOnClickListener {
            MaterialAlertDialogBuilder(requireActivity())
                .setMessage(R.string.delete_group)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    val pos = Data.groups.findGroupPos(key)
                    // Remove all countries belonging to that group
                    Data.visits.deleteVisited(key)
                    // Delete the group
                    Data.groups.deleteGroup(key)
                    Data.saveData()
                    onDelCb(pos)
                    dialog?.dismiss()
                }
                .setNegativeButton(android.R.string.cancel) { _, _ -> }
                .show()
        }

        _binding.btnOk.setOnClickListener {
            val name = _binding.groupName.text.toString()
            val color = _binding.groupColor.text.toString()
            val key = (if (key != 0) key else Data.groups.genKey())
            Data.groups.setGroup(key, name, ColorDrawable(Color.parseColor("#$color")))
            Data.saveData()
            onAddCb(key)
            dialog?.dismiss()
        }

        _binding.btnCancel.setOnClickListener {
            dialog?.cancel()
        }

        _binding.groupName.setText(grp.name)
        builder.setView(_binding.root)
        return builder.create()
    }

    private fun setupText(s: TextInputEditText, grp: Groups.Group?) {
        s.setText(colorToHex6(ColorDrawable(grp?.color?.color ?: 0)).substring(1))
        s.addTextChangedListener(
            EditTextListener(
                _binding.colorR,
                _binding.colorG,
                _binding.colorB,
                _binding.groupColor,
                _binding.colorView
            )
        )
    }

    private fun setupSlider(s: Slider, v: Float) {
        s.valueFrom = 0F
        s.valueTo = 1F
        s.value = v
        s.addOnChangeListener(
            SliderOnChangeListener(
                _binding.colorR,
                _binding.colorG,
                _binding.colorB,
                _binding.groupColor,
                _binding.colorView
            )
        )
    }

}


private class EditTextListener(
    private val colorEditR: Slider,
    private val colorEditG: Slider,
    private val colorEditB: Slider,
    private val colorEditText: TextInputEditText,
    private val colorView: View
) : TextWatcher {

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    override fun afterTextChanged(s: Editable?) {
        val col: Color
        try {
            col = Color.valueOf(Color.parseColor("#${colorEditText.text}"))
        } catch (e: Exception) {
            return
        }

        colorEditR.value = col.red()
        colorEditG.value = col.green()
        colorEditB.value = col.blue()
        colorView.background = ColorDrawable(col.toArgb())
    }
}

private class SliderOnChangeListener(
    private val colorEditR: Slider,
    private val colorEditG: Slider,
    private val colorEditB: Slider,
    private val colorEditText: TextInputEditText,
    private val colorView: View
) : Slider.OnChangeListener {
    override fun onValueChange(slider: Slider, value: Float, fromUser: Boolean) {
        val rgb =
            ColorDrawable(Color.argb(1F, colorEditR.value, colorEditG.value, colorEditB.value))
        colorEditText.setText(colorToHex6(rgb).substring(1))
        colorView.background = rgb
    }
}
