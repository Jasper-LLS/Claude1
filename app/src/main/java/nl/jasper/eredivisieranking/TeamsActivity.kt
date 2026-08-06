package nl.jasper.eredivisieranking

import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import java.util.UUID
import nl.jasper.eredivisieranking.databinding.ActivityTeamsBinding

class TeamsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTeamsBinding
    private lateinit var repository: Repository
    private lateinit var adapter: TeamAdapter
    private val teams = mutableListOf<Team>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeamsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        repository = Repository(this)

        adapter = TeamAdapter(teams, onEdit = { editTeam(it) }, onDelete = { deleteTeam(it) })
        binding.recyclerTeams.layoutManager = LinearLayoutManager(this)
        binding.recyclerTeams.adapter = adapter

        binding.buttonAddTeam.setOnClickListener { addTeam() }
        binding.buttonResetTeams.setOnClickListener {
            repository.resetDefaultTeams()
            refresh()
        }

        refresh()
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun refresh() {
        val current = repository.getTeams()
        adapter.setItems(current)
        binding.textTitle.text = getString(R.string.title_teams, current.size)
        if (current.size != 18) {
            binding.textWarning.visibility = android.view.View.VISIBLE
            binding.textWarning.text = getString(R.string.warning_team_count, current.size)
        } else {
            binding.textWarning.visibility = android.view.View.GONE
        }
    }

    private fun addTeam() {
        showNameDialog(existing = null) { name ->
            val updated = repository.getTeams().toMutableList()
            updated.add(Team(id = UUID.randomUUID().toString(), name = name))
            repository.saveTeams(updated)
            refresh()
        }
    }

    private fun editTeam(team: Team) {
        showNameDialog(existing = team.name) { newName ->
            val updated = repository.getTeams().map {
                if (it.id == team.id) it.copy(name = newName) else it
            }
            repository.saveTeams(updated)
            refresh()
        }
    }

    private fun deleteTeam(team: Team) {
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.delete) + ": " + team.name + "?")
            .setPositiveButton(R.string.ok) { _, _ ->
                val updated = repository.getTeams().filter { it.id != team.id }
                repository.saveTeams(updated)
                refresh()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showNameDialog(existing: String?, onConfirm: (String) -> Unit) {
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        input.hint = getString(R.string.team_name_hint)
        if (existing != null) input.setText(existing)

        AlertDialog.Builder(this)
            .setTitle(R.string.team_name_hint)
            .setView(input)
            .setPositiveButton(R.string.ok) { _, _ ->
                val name = input.text.toString().trim()
                if (name.isNotEmpty()) onConfirm(name)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}
