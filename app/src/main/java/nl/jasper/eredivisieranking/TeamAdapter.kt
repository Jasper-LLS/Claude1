package nl.jasper.eredivisieranking

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TeamAdapter(
    private val items: MutableList<Team>,
    private val onEdit: (Team) -> Unit,
    private val onDelete: (Team) -> Unit
) : RecyclerView.Adapter<TeamAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.textTeamName)
        val edit: Button = view.findViewById(R.id.buttonEdit)
        val delete: Button = view.findViewById(R.id.buttonDelete)
    }

    fun setItems(newItems: List<Team>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_team, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val team = items[position]
        holder.name.text = team.name
        holder.edit.setOnClickListener { onEdit(team) }
        holder.delete.setOnClickListener { onDelete(team) }
    }

    override fun getItemCount(): Int = items.size
}
