package net.helcel.beendroid.activity

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
import net.helcel.beendroid.countries.Visited
import net.helcel.beendroid.helper.colorBackground
import net.helcel.beendroid.helper.colorPanelBackground
import java.util.*


class FoldingListAdapter(
    private val ctx: Context, l: List<GeoLoc>,
    private val visited: Visited,
                         ) : RecyclerView.Adapter<FoldingListAdapter.FoldingListViewHolder>()  {

    private val cg : MutableMap<GeoLoc,Boolean> = l.sortedBy { it.fullName }.fold(LinkedHashMap<GeoLoc,Boolean>()) { acc, e ->
        acc[e] = false
        acc
     }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): FoldingListViewHolder {
        val view: View = LayoutInflater
            .from(viewGroup.context)
            .inflate(R.layout.item_list, viewGroup, false)
        return FoldingListViewHolder(ctx, view, visited)
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
            visited.setVisited(el.first, it)
        })
    }

    override fun getItemCount(): Int {
        return cg.size
    }

    class FoldingListViewHolder(private val ctx: Context, itemView: View,
                                private val visited: Visited,
                                ) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.textView)
        private val progressView: TextView = itemView.findViewById(R.id.progressView)
        private val checkBox: MaterialCheckBox = itemView.findViewById(R.id.checkBox)
        private val subItemView: View = itemView.findViewById(R.id.sub_item)
        private val list: RecyclerView = itemView.findViewById(R.id.list_list)
        init {
            list.layoutManager = LinearLayoutManager(ctx, RecyclerView.VERTICAL, false)
            list.setItemAnimator(null) //TODO: Fix slow recycler expansion
            //list.setHasFixedSize(true)
        }

        fun bind(el: Pair<GeoLoc, Boolean>) {
            subItemView.visibility = if (el.second) View.VISIBLE else View.GONE

            textView.text = el.first.fullName
            if (el.first.children.isEmpty()) {

                textView.backgroundTintList = null
                textView.background = colorBackground(ctx)
                textView.isActivated = false
            }else {
                textView.setTypeface(null, Typeface.BOLD)
                progressView.text = ctx.getString(R.string.rate,(el.first.children.map { visited.getVisited(it) }.count { it }),el.first.children.size)

                textView.background = colorPanelBackground(ctx)
                textView.background.alpha = 128

                list.adapter = FoldingListAdapter(ctx, el.first.children, visited)
                textView.parent.parent.requestChildFocus(textView, textView)
            }
            checkBox.checkedState =
                if (visited.getVisited(el.first)) MaterialCheckBox.STATE_CHECKED
                else if (el.first.children.any { visited.getVisited(it) }) MaterialCheckBox.STATE_INDETERMINATE
                else MaterialCheckBox.STATE_UNCHECKED

        }

        fun addListeners(expandLambda: ()->Boolean, visitedLambda: (Boolean)->Unit) {
            textView.setOnClickListener { expandLambda() }
            checkBox.addOnCheckedStateChangedListener { _, e ->
                visitedLambda(e == MaterialCheckBox.STATE_CHECKED)
            }
        }

    }
}
