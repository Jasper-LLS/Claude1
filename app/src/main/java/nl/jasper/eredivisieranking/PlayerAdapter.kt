package nl.jasper.eredivisieranking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PlayerAdapter(
    private val items: MutableList<Player>,
    private val hasPrediction: (Player) -> Boolean,
    private val onRank: (Player) -> Unit,
    private val onShare: (Player) -> Unit,
    private val onDelete: (Player) -> Unit
) : RecyclerView.Adapter<PlayerAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.textPlayerName)
        val status: TextView = view.findViewById(R.id.textStatus)
        val rank: Button = view.findViewById(R.id.buttonRank)
        val share: Button = view.findViewById(R.id.buttonShare)
        val delete: Button = view.findViewById(R.id.buttonDelete)
    }

    fun setItems(newItems: List<Player>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_player, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val player = items[position]
        holder.name.text = player.name
        val done = hasPrediction(player)
        holder.status.text = if (done) "✓" else "—"
        holder.status.setTextColor(
            holder.itemView.resources.getColor(
                if (done) R.color.green_ok else R.color.black, null
            )
        )
        holder.rank.setOnClickListener { onRank(player) }
        holder.share.setOnClickListener { onShare(player) }
        holder.delete.setOnClickListener { onDelete(player) }
    }

    override fun getItemCount(): Int = items.size
}
