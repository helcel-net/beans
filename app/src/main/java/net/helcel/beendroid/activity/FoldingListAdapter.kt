package net.helcel.beendroid.activity

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import net.helcel.beendroid.R
import net.helcel.beendroid.countries.GeoLoc
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
        holder.bind(el) {
            notifyItemChanged(position)
            parentLambda()
        }

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
        private val textView: TextView
        private val expand: ImageView
        private val checkBox: MaterialCheckBox
        private val subItemView: View
        private val list: RecyclerView

        init {
            textView = itemView.findViewById(R.id.textView)
            expand = itemView.findViewById(R.id.expand)
            expand.setImageDrawable(AppCompatResources.getDrawable(ctx,R.drawable.chevron_right_solid))
            checkBox = itemView.findViewById(R.id.checkBox)
            subItemView = itemView.findViewById(R.id.sub_item)
            list =  itemView.findViewById(R.id.list_list)
            list.layoutManager = LinearLayoutManager(ctx, RecyclerView.VERTICAL, false)
        }

        fun bind(el: Pair<GeoLoc, Boolean>, parentLambda: () -> Unit) {
            expand.rotation = if(el.second) 90f else 0f
            subItemView.visibility = if (el.second) View.VISIBLE else View.GONE
            expand.visibility = if(!el.first.isEnd) View.VISIBLE else View.GONE

            textView.text = el.first.fullName
            checkBox.checkedState =
                if(visited.visited(el.first)) MaterialCheckBox.STATE_CHECKED
                else if (el.first.children.any { visited.visited(it) }) MaterialCheckBox.STATE_INDETERMINATE
                else MaterialCheckBox.STATE_UNCHECKED

            textView.parent.parent.requestChildFocus(textView,textView)
            list.adapter = FoldingListAdapter(ctx, el.first.children,visited, parentLambda)
        }

        fun addListeners(expandLambda: ()->Boolean, visitedLambda: (Boolean)->Unit) {

            textView.setOnClickListener { checkBox.toggle() }
            checkBox.addOnCheckedStateChangedListener { _, e ->
                visitedLambda(e == MaterialCheckBox.STATE_CHECKED)
            }

            textView.setOnLongClickListener{ expandLambda() }
            expand.setOnClickListener { expandLambda() }
        }

    }
}
