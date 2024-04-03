package net.helcel.beans.activity.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import net.helcel.beans.R
import net.helcel.beans.activity.fragment.EditPlaceColorFragment
import net.helcel.beans.activity.fragment.EditPlaceFragment
import net.helcel.beans.countries.GeoLoc
import net.helcel.beans.helper.Data
import net.helcel.beans.helper.Settings
import net.helcel.beans.helper.Theme.colorWrapper

class GeolocListAdapter(
    private val ctx: EditPlaceFragment, val l: GeoLoc, private val pager: ViewPagerAdapter
) : RecyclerView.Adapter<GeolocListAdapter.FoldingListViewHolder>() {

    private lateinit var view: View

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): FoldingListViewHolder {
        view = LayoutInflater
            .from(viewGroup.context)
            .inflate(R.layout.item_list_geoloc, viewGroup, false)
        return FoldingListViewHolder(ctx.requireActivity(), view)
    }

    override fun onBindViewHolder(holder: FoldingListViewHolder, position: Int) {
        val el = l.children[position]
        holder.bind(el)
        holder.addListeners(el) {
            if (el.children.isNotEmpty())
                pager.addFragment(ctx, EditPlaceFragment(el, pager))
            true
        }
    }

    override fun getItemCount(): Int {
        return l.children.size
    }

    class FoldingListViewHolder(private val ctx: FragmentActivity, itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.textView)
        private val progressView: TextView = itemView.findViewById(R.id.name)
        private val checkBox: MaterialCheckBox = itemView.findViewById(R.id.checkBox)

        private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx)
        private val statsPref = sharedPreferences.getString(
            ctx.getString(R.string.key_stats),
            ctx.getString(R.string.counters)
        )

        fun bind(el: GeoLoc) {
            textView.text = el.fullName
            if (el.shouldShowChildren(ctx)) {
                textView.setTypeface(null, Typeface.BOLD)

                val numerator = el.children.map { Data.visits.getVisited(it) != 0 }.count { it }
                val denominator = el.children.size

                progressView.text = when (statsPref) {
                    ctx.getString(R.string.percentages) -> ctx.getString(
                        R.string.percentage,
                        (100 * (numerator.toFloat() / denominator.toFloat())).toInt()
                    )

                    else -> ctx.getString(R.string.rate, numerator, denominator)
                }
                textView.backgroundTintList = ColorStateList.valueOf(
                    colorWrapper(
                        ctx,
                        android.R.attr.panelColorBackground
                    ).color
                ).withAlpha(128)
                textView.parent.parent.requestChildFocus(textView, textView)
            } else {
                textView.backgroundTintList =
                    ColorStateList.valueOf(colorWrapper(ctx, android.R.attr.colorBackground).color)
            }
            refreshCheck(el)
        }

        fun addListeners(el: GeoLoc, expandLambda: () -> Boolean) {
            if (el.shouldShowChildren(ctx)) {
                textView.setOnClickListener { expandLambda() }
            }
            checkBox.setOnClickListener {
                Data.selected_geoloc = el
                if (Data.groups.size() == 1 && Settings.isSingleGroup(ctx)) {
                    if (checkBox.isChecked) {
                        // If one has just checked the box (assign unique group)
                        Data.selected_group = Data.groups.getUniqueEntry()
                        onColorDialogDismiss(false)
                    } else {
                        // If one has just unchecked the box (unassign unique group)
                        Data.selected_group = null
                        onColorDialogDismiss(true)
                    }
                } else {
                    Data.selected_group = null
                    EditPlaceColorFragment(this).show(
                        ctx.supportFragmentManager,
                        "AddColorDialogFragment"
                    )
                }
            }
        }

        fun onColorDialogDismiss(clear: Boolean) {
            if (clear) {
                Data.visits.setVisited(Data.selected_geoloc, 0)
                Data.saveData()
            }
            if (Data.selected_group != null && Data.selected_geoloc != null) {
                Data.visits.setVisited(Data.selected_geoloc, Data.selected_group?.key ?: 0)
                Data.saveData()
            }
            Data.selected_geoloc?.let { refreshCheck(it) }
            Data.selected_geoloc = null
            Data.selected_group = null
        }

        private fun refreshCheck(geoLoc: GeoLoc) {
            var col = Data.groups.getGroupFromKey(Data.visits.getVisited(geoLoc)).color.color
            if (col == Color.TRANSPARENT)
                col = Color.GRAY
            checkBox.checkedState =
                if (Data.visits.getVisited(geoLoc) != 0) MaterialCheckBox.STATE_CHECKED
                else if (geoLoc.children.any { Data.visits.getVisited(it) != 0 }) MaterialCheckBox.STATE_INDETERMINATE
                else MaterialCheckBox.STATE_UNCHECKED

            checkBox.buttonTintList = ColorStateList(
                arrayOf(
                    intArrayOf(-android.R.attr.state_checked),
                    intArrayOf(android.R.attr.state_checked)
                ),
                intArrayOf(col, col)
            )
        }

    }
}
