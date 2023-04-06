package net.helcel.beendroid

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.helcel.beendroid.countries.GeoLoc
import net.helcel.beendroid.countries.LocType
import java.util.*


class FoldingListAdapter(private val ctx: Context, l: List<GeoLoc>)  :
    RecyclerView.Adapter<FoldingListAdapter.FoldingListViewHolder>()  {

    private var cg : MutableMap<GeoLoc,Boolean> = l.sortedBy { it.fullName }.fold(LinkedHashMap<GeoLoc,Boolean>()) { acc, e ->
        acc[e] = false
        acc
     }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): FoldingListViewHolder {
        val view: View = LayoutInflater
            .from(viewGroup.context)
            .inflate(R.layout.item_list, viewGroup, false)
        return FoldingListViewHolder(ctx, view)
    }

    override fun onBindViewHolder(holder: FoldingListViewHolder, position: Int) {
        val el = cg.toList()[position]
        holder.bind(el)
        holder.itemView.setOnClickListener {
            holder.checkBox.toggle()
        }
        holder.itemView.setOnLongClickListener {
            if (el.second) {
                cg[el.first] = false
            }else {
                cg.forEach {
                    cg[it.key] = it.key == el.first
                }

            }
            notifyItemChanged(position)
            true
        }
    }

    override fun getItemCount(): Int {
        return cg.size
    }

    class FoldingListViewHolder(private val ctx: Context, itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView
        val checkBox: CheckBox
        private val subItemView: View

        init {
            textView = itemView.findViewById(R.id.textView)
            checkBox = itemView.findViewById(R.id.checkBox)
            subItemView = itemView.findViewById(R.id.sub_item)
        }

        fun bind(el: Pair<GeoLoc, Boolean>) {
            subItemView.visibility = if (el.second) View.VISIBLE else View.GONE
            textView.text = el.first.fullName
            if(el.first.type == LocType.STATE || el.first.children.isEmpty())
                return

            val list: RecyclerView = itemView.findViewById(R.id.list_list)
            list.layoutManager = LinearLayoutManager(ctx, RecyclerView.VERTICAL, false)
            list.adapter = FoldingListAdapter(ctx, el.first.children)
        }

    }
}
