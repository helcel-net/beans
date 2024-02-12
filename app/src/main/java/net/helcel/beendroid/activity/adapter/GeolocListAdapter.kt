package net.helcel.beendroid.activity.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import net.helcel.beendroid.R
import net.helcel.beendroid.countries.GeoLoc
import net.helcel.beendroid.helper.colorWrapper
import net.helcel.beendroid.helper.visited
import java.util.*


class GeolocListAdapter(
    private val ctx: Context, l: List<GeoLoc>) : RecyclerView.Adapter<GeolocListAdapter.FoldingListViewHolder>()  {

    private val cg : MutableMap<GeoLoc,Boolean> = l.sortedBy { it.fullName }.fold(LinkedHashMap<GeoLoc,Boolean>()) { acc, e ->
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
        holder.addListeners( {
            if (!el.first.isEnd) {
                cg[el.first] = !el.second
                notifyItemChanged(position)
            }
            !el.first.isEnd
        }, {
            visited!!.setVisited(el.first, it)
        })
    }

    override fun getItemCount(): Int {
        return cg.size
    }

    class FoldingListViewHolder(private val ctx: Context, itemView: View) : RecyclerView.ViewHolder(itemView) {
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
                progressView.text = ctx.getString(R.string.rate,(el.first.children.map { visited!!.getVisited(it)>0 }.count { it }),el.first.children.size)

                textView.background = colorWrapper(ctx, android.R.attr.panelColorBackground)
                textView.background.alpha = 128

                list.adapter = GeolocListAdapter(ctx, el.first.children)
                textView.parent.parent.requestChildFocus(textView, textView)
            }
            checkBox.checkedState =
                if (visited!!.getVisited(el.first)>0) MaterialCheckBox.STATE_CHECKED
                else if (el.first.children.any { visited!!.getVisited(it)>0 }) MaterialCheckBox.STATE_INDETERMINATE
                else MaterialCheckBox.STATE_UNCHECKED
        }

        fun addListeners(expandLambda: ()->Boolean, visitedLambda: (Int)->Unit) {
            textView.setOnClickListener { expandLambda() }
            checkBox.addOnCheckedStateChangedListener { _, e ->
                visitedLambda( if(e == MaterialCheckBox.STATE_CHECKED) 1 else 0)
            }
        }

    }
}
