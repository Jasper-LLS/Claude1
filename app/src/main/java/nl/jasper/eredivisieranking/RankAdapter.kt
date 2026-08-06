package nl.jasper.eredivisieranking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Reorderable list of teams using up/down buttons. Used both for entering a
 * player's predicted ranking and for entering the actual round standings.
 */
class RankAdapter(
    private val items: MutableList<Team>
) : RecyclerView.Adapter<RankAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val position: TextView = view.findViewById(R.id.textPosition)
        val name: TextView = view.findViewById(R.id.textTeamName)
        val up: Button = view.findViewById(R.id.buttonUp)
        val down: Button = view.findViewById(R.id.buttonDown)
    }

    fun getItems(): List<Team> = items

    fun setItems(newItems: List<Team>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rank_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val team = items[position]
        holder.position.text = "${position + 1}."
        holder.name.text = team.name

        holder.up.isEnabled = position > 0
        holder.down.isEnabled = position < items.size - 1

        holder.up.setOnClickListener {
            val idx = holder.bindingAdapterPosition
            if (idx > 0) {
                moveItem(idx, idx - 1)
            }
        }
        holder.down.setOnClickListener {
            val idx = holder.bindingAdapterPosition
            if (idx < items.size - 1) {
                moveItem(idx, idx + 1)
            }
        }
    }

    private fun moveItem(from: Int, to: Int) {
        val item = items.removeAt(from)
        items.add(to, item)
        notifyItemMoved(from, to)
        notifyItemRangeChanged(minOf(from, to), Math.abs(from - to) + 1)
    }

    override fun getItemCount(): Int = items.size
}
