package net.helcel.beendroid.activity

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import net.helcel.beendroid.R
import net.helcel.beendroid.countries.GeoLoc
import net.helcel.beendroid.countries.LocType
import net.helcel.beendroid.countries.Visited
import java.util.*


class FoldingListAdapter(
    private val ctx: Context, l: List<GeoLoc>,
    private val visited: Visited,
    private val parentLambda: () -> Unit,
                         ) : RecyclerView.Adapter<FoldingListAdapter.FoldingListViewHolder>()  {

    private var cg : MutableMap<GeoLoc,Boolean> = l.sortedBy { it.fullName }.fold(LinkedHashMap<GeoLoc,Boolean>()) { acc, e ->
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
        holder.bind(el) { parentLambda() }

        holder.addListeners( {
            if (!el.first.isEnd) {
                cg[el.first] = !el.second
                notifyItemChanged(position)
            }
            !el.first.isEnd
        }, {
            visited.setVisited(el.first, it)
            parentLambda()
        })
    }

    override fun getItemCount(): Int {
        return cg.size
    }

    class FoldingListViewHolder(private val ctx: Context, itemView: View,
                                private val visited: Visited,
                                ) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.textView)
        private val checkBox: MaterialCheckBox = itemView.findViewById(R.id.checkBox)
        private val subItemView: View = itemView.findViewById(R.id.sub_item)
        private val list: RecyclerView = itemView.findViewById(R.id.list_list)
        init {
            list.layoutManager = LinearLayoutManager(ctx, RecyclerView.VERTICAL, false)
        }

        fun bind(el: Pair<GeoLoc, Boolean>, parentLambda: () -> Unit) {
            subItemView.visibility = if (el.second) View.VISIBLE else View.GONE

            textView.text = el.first.fullName
            if (el.first.type == LocType.GROUP) {
                textView.setTypeface(null, Typeface.BOLD)

                val colorGrayTyped = TypedValue()
                ctx.theme.resolveAttribute(android.R.attr.panelColorBackground, colorGrayTyped, true)
                val color = Color.valueOf(colorGrayTyped.data)
                textView.setBackgroundColor(Color.valueOf(color.red(), color.green(), color.blue(), 0.5f).toArgb())
                list.adapter = FoldingListAdapter(ctx, el.first.children,visited, parentLambda)
                textView.parent.parent.requestChildFocus(textView,textView)

            } else {
                val colorBackgroundTyped = TypedValue()
                ctx.theme.resolveAttribute(android.R.attr.colorBackground, colorBackgroundTyped, true)
                textView.backgroundTintList = null
                textView.background = ColorDrawable(colorBackgroundTyped.data)
                textView.isActivated = false

                val layoutParam = checkBox.layoutParams
                layoutParam.width = 125
                checkBox.layoutParams = layoutParam
                checkBox.visibility = View.VISIBLE
            }
            checkBox.checkedState =
                if (visited.visited(el.first)) MaterialCheckBox.STATE_CHECKED
                else if (el.first.children.any { visited.visited(it) }) MaterialCheckBox.STATE_INDETERMINATE
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
