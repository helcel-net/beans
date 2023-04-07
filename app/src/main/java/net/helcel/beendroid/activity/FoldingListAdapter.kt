package net.helcel.beendroid.activity

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.helcel.beendroid.R
import net.helcel.beendroid.countries.GeoLoc
import net.helcel.beendroid.countries.LocType
import net.helcel.beendroid.countries.Visited
import java.util.*


class FoldingListAdapter(private val ctx: Context, l: List<GeoLoc>, private val visited: Visited)  :
    RecyclerView.Adapter<FoldingListAdapter.FoldingListViewHolder>()  {

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
        holder.bind(el)

        val expandLambda = { _:View ->
            if (el.first.children.isEmpty() || el.first.type == LocType.STATE) {
                false
            } else {
                cg[el.first] = !el.second
                if (!el.second)
                    cg.filter { (it.key != el.first) && (cg[it.key] == true) }.keys.forEach {
                        cg[it] = false
                        notifyItemChanged(cg.toList().map { e -> e.first }.indexOf(it))
                    }

                notifyItemChanged(position)
                true
            }

        }

        holder.textView.setOnClickListener { holder.checkBox.toggle() }
        holder.checkBox.setOnCheckedChangeListener{_,e->visited.setVisited(el.first,e)}
        holder.textView.setOnLongClickListener{ expandLambda(it) }
        holder.expand.setOnClickListener { expandLambda(it) }
    }

    override fun getItemCount(): Int {
        return cg.size
    }

    class FoldingListViewHolder(private val ctx: Context, itemView: View, private val visited: Visited) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView
        val expand: ImageView
        val checkBox: CheckBox
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

        fun bind(el: Pair<GeoLoc, Boolean>) {
            expand.rotation = if(el.second) 90f else 0f
            subItemView.visibility = if (el.second) View.VISIBLE else View.GONE


            textView.text = el.first.fullName
            checkBox.isChecked = visited.visited(el.first)
            if(el.first.type == LocType.STATE || el.first.children.isEmpty()){
                expand.visibility = View.GONE
                return
            }
            textView.parent.parent.requestChildFocus(textView,textView)
            list.adapter = FoldingListAdapter(ctx, el.first.children,visited)
        }

    }
}
