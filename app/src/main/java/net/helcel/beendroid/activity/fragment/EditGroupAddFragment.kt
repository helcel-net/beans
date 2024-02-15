package net.helcel.beendroid.activity.fragment

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.fragment.app.DialogFragment
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.TextInputEditText
import net.helcel.beendroid.R
import net.helcel.beendroid.helper.Groups
import net.helcel.beendroid.helper.colorToHex6
import net.helcel.beendroid.helper.groups
import net.helcel.beendroid.helper.saveData
import java.lang.Exception


class EditGroupAddFragment(private val key: Int =0, val onAddCb : (Int)->Unit) : DialogFragment() {
    private lateinit var colorNameEditText: TextInputEditText
    private lateinit var colorEditText: TextInputEditText

    private lateinit var colorView : View

    private lateinit var colorEditR : Slider
    private lateinit var colorEditG : Slider
    private lateinit var colorEditB : Slider

    private val grp = groups!!.getGroupFromKey(key)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(
            requireActivity()
        )
        val inflater = requireActivity().layoutInflater
        val view: View = inflater.inflate(R.layout.fragment_edit_groups_add, null)

        colorNameEditText = view.findViewById(R.id.group_name)
        colorEditText = view.findViewById(R.id.group_color)
        colorView = view.findViewById(R.id.colorView)
        colorEditR = view.findViewById(R.id.colorR)
        colorEditG = view.findViewById(R.id.colorG)
        colorEditB = view.findViewById(R.id.colorB)

        setupSlider(colorEditR,(grp?.color?.color?.red ?: 0)/ 255F)
        setupSlider(colorEditG,(grp?.color?.color?.green ?: 0)/ 255F)
        setupSlider(colorEditB,(grp?.color?.color?.blue ?: 0)/ 255F)

        setupText(colorEditText,grp)

        colorView.background = ColorDrawable(grp?.color?.color ?: 0)


        colorNameEditText.setText(grp?.name ?: "")
        builder.setView(view)
            .setPositiveButton("Ok") { _: DialogInterface?, _: Int ->
                val name = colorNameEditText.text.toString()
                val color = colorEditText.text.toString()
                val key = (if (key!=0) key else groups!!.genKey())
                groups!!.setGroup(key,name, ColorDrawable(Color.parseColor("#$color")))
                saveData()
                onAddCb(key)
            }
            .setNegativeButton(
                "Cancel"
            ) { dialog: DialogInterface, _: Int -> dialog.cancel() }
        return builder.create()
    }

    private fun setupText(s: TextInputEditText, grp: Groups.Group?) {
        s.setText( colorToHex6(ColorDrawable(grp?.color?.color ?: 0)).substring(1))
        s.addTextChangedListener(EditTextListener(colorEditR, colorEditG, colorEditB, colorEditText, colorView))
    }

    private fun setupSlider(s: Slider, v: Float){
        s.valueFrom = 0F
        s.valueTo = 1F
        s.value = v
        s.addOnChangeListener(SliderOnChangeListener( colorEditR, colorEditG, colorEditB,colorEditText, colorView))
    }

}



private class EditTextListener(
    private val colorEditR: Slider,
    private val colorEditG: Slider,
    private val colorEditB: Slider,
    private val colorEditText: TextInputEditText,
    private val colorView: View
): TextWatcher  {


    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        val col : Color
        try{
            col = Color.valueOf(Color.parseColor("#${colorEditText.text}"))
        }catch (e:Exception){
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
): Slider.OnChangeListener {
    override fun onValueChange(slider: Slider, value: Float, fromUser: Boolean) {
        val rgb = ColorDrawable(Color.argb(1F,colorEditR.value, colorEditG.value, colorEditB.value))
        colorEditText.setText(colorToHex6(rgb).substring(1))
        colorView.background = rgb
    }

}
