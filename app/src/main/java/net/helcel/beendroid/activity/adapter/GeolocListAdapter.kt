package net.helcel.beendroid.activity.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import net.helcel.beendroid.R
import net.helcel.beendroid.activity.fragment.EditPlaceColorFragment
import net.helcel.beendroid.countries.GeoLoc
import net.helcel.beendroid.helper.colorWrapper
import net.helcel.beendroid.helper.groups
import net.helcel.beendroid.helper.saveData
import net.helcel.beendroid.helper.selected_geoloc
import net.helcel.beendroid.helper.selected_group
import net.helcel.beendroid.helper.visits
class GeolocListAdapter(
    private val ctx: FragmentActivity, l: List<GeoLoc>) : RecyclerView.Adapter<GeolocListAdapter.FoldingListViewHolder>()  {

    private val cg : MutableMap<GeoLoc,Boolean> = l.sortedBy { it.fullName }.fold(LinkedHashMap()) { acc, e ->
        acc[e] = false
        acc
     }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): FoldingListViewHolder {
        val view: View = LayoutInflater
            .from(viewGroup.context)
            .inflate(R.layout.item_list_geoloc, viewGroup, false)
        return FoldingListViewHolder(ctx, view)
    }

    override fun onBindViewHolder(holder: FoldingListViewHolder, position: Int) {
        val el = cg.toList()[position]

        holder.bind(el)
        holder.addListeners(el) {
            if (!el.first.isEnd) {
                cg[el.first] = !el.second
                notifyItemChanged(position)
            }
            !el.first.isEnd
        }
    }

    override fun getItemCount(): Int {
        return cg.size
    }

    class FoldingListViewHolder(private val ctx: FragmentActivity, itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.textView)
        private val progressView: TextView = itemView.findViewById(R.id.name)
        private val checkBox: MaterialCheckBox = itemView.findViewById(R.id.checkBox)
        private val subItemView: View = itemView.findViewById(R.id.sub_item)
        private val list: RecyclerView = itemView.findViewById(R.id.list_list)
        init {
            list.layoutManager = LinearLayoutManager(ctx, RecyclerView.VERTICAL, false)
            list.itemAnimator = null //TODO: Fix slow recycler expansion
        }

        fun bind(el: Pair<GeoLoc, Boolean>) {
            subItemView.visibility = if (el.second) View.VISIBLE else View.GONE

            textView.text = el.first.fullName
            if (el.first.children.isEmpty()) {

                textView.backgroundTintList = null
                textView.background = colorWrapper(ctx, android.R.attr.colorBackground)
                textView.isActivated = false
            }else {
                textView.setTypeface(null, Typeface.BOLD)
                progressView.text = ctx.getString(R.string.rate,(el.first.children.map { visits!!.getVisited(it)>0 }.count { it }),el.first.children.size)

                textView.background = colorWrapper(ctx, android.R.attr.panelColorBackground)
                textView.background.alpha = 128

                list.adapter = GeolocListAdapter(ctx, el.first.children)
                textView.parent.parent.requestChildFocus(textView, textView)
            }
            refreshCheck(el.first)
        }

        fun addListeners(el: Pair<GeoLoc, Boolean>, expandLambda: () -> Boolean) {
            textView.setOnClickListener { expandLambda() }
            checkBox.setOnClickListener {
                selected_geoloc = el.first
                if (groups!!.size() != 1) {
                    val dialogFragment = EditPlaceColorFragment(this)
                    selected_group = null
                    dialogFragment.show(ctx.supportFragmentManager, "AddColorDialogFragment")
                } else {
                    selected_group = groups!!.getUniqueEntry()!!
                    onColorDialogDismiss(false)
                }
            }
        }

        fun onColorDialogDismiss(clear: Boolean) {
            if(clear){
                visits!!.setVisited(selected_geoloc!!,0)
                saveData()
            }
            if(selected_group!=null && selected_geoloc!=null) {
                visits!!.setVisited(selected_geoloc!!, selected_group!!.key)
                saveData()
            }
            selected_geoloc?.let { refreshCheck(it) }
            selected_geoloc = null
            selected_group = null
        }

        private fun refreshCheck(geoLoc: GeoLoc){
            val col = groups!!.getGroupFromKey(visits!!.getVisited(geoLoc))?.color?.color?:Color.WHITE
            checkBox.checkedState =
                if (visits!!.getVisited(geoLoc)!=0) MaterialCheckBox.STATE_CHECKED
                else if (geoLoc.children.any { visits!!.getVisited(it)!=0 }) MaterialCheckBox.STATE_INDETERMINATE
                else MaterialCheckBox.STATE_UNCHECKED

            checkBox.buttonTintList = ColorStateList(arrayOf(
                intArrayOf(-android.R.attr.state_checked), intArrayOf(android.R.attr.state_checked)),
                intArrayOf(col, col))
        }

    }
}
