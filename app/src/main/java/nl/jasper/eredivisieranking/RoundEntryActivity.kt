package nl.jasper.eredivisieranking

import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import nl.jasper.eredivisieranking.databinding.ActivityRoundEntryBinding

class RoundEntryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoundEntryBinding
    private lateinit var repository: Repository
    private lateinit var adapter: RankAdapter
    private var currentRound: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoundEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        repository = Repository(this)

        adapter = RankAdapter(repository.getTeams().toMutableList())
        binding.recyclerRound.layoutManager = LinearLayoutManager(this)
        binding.recyclerRound.adapter = adapter

        binding.buttonLoadRound.setOnClickListener { loadRound() }
        binding.buttonSaveRound.setOnClickListener { saveRound() }
        binding.buttonDeleteRound.setOnClickListener { deleteRound() }
        binding.buttonPasteList.setOnClickListener { pasteList() }

        refreshExistingRoundsText()

        val rounds = repository.getRoundNumbers()
        if (rounds.isNotEmpty()) {
            binding.editRoundNumber.setText((rounds.max() + 1).toString())
        } else {
            binding.editRoundNumber.setText("1")
        }
    }

    private fun refreshExistingRoundsText() {
        val rounds = repository.getRoundNumbers()
        binding.textExistingRounds.text = if (rounds.isEmpty()) {
            "Ingevoerde speelrondes: geen"
        } else {
            "Ingevoerde speelrondes: " + rounds.joinToString(", ")
        }
    }

    private fun loadRound() {
        val roundNumber = binding.editRoundNumber.text.toString().toIntOrNull()
        if (roundNumber == null) {
            Toast.makeText(this, R.string.round_number, Toast.LENGTH_SHORT).show()
            return
        }
        currentRound = roundNumber

        val teams = repository.getTeams()
        val teamsById = teams.associateBy { it.id }
        val savedOrder = repository.getRoundStanding(roundNumber)
        val orderedTeams = if (savedOrder != null) {
            val list = savedOrder.mapNotNull { teamsById[it] }.toMutableList()
            for (t in teams) if (list.none { it.id == t.id }) list.add(t)
            list
        } else {
            teams.toMutableList()
        }
        adapter.setItems(orderedTeams)
    }

    private fun saveRound() {
        val roundNumber = binding.editRoundNumber.text.toString().toIntOrNull()
        if (roundNumber == null) {
            Toast.makeText(this, R.string.round_number, Toast.LENGTH_SHORT).show()
            return
        }
        repository.saveRoundStanding(roundNumber, adapter.getItems().map { it.id })
        currentRound = roundNumber
        Toast.makeText(this, getString(R.string.round_saved, roundNumber), Toast.LENGTH_SHORT).show()
        refreshExistingRoundsText()
    }

    private fun pasteList() {
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
        input.hint = getString(R.string.paste_dialog_hint)
        input.minLines = 6

        AlertDialog.Builder(this)
            .setTitle(R.string.paste_dialog_title)
            .setView(input)
            .setPositiveButton(R.string.ok) { _, _ ->
                when (val result = StandingsParser.parse(input.text.toString(), repository.getTeams())) {
                    is ParseResult.Success -> {
                        adapter.setItems(result.orderedTeams)
                        Toast.makeText(this, R.string.paste_success, Toast.LENGTH_SHORT).show()
                    }
                    is ParseResult.Failure -> {
                        AlertDialog.Builder(this)
                            .setMessage(result.message)
                            .setPositiveButton(R.string.ok, null)
                            .show()
                    }
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun deleteRound() {
        val roundNumber = currentRound ?: binding.editRoundNumber.text.toString().toIntOrNull()
        if (roundNumber == null) return
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.delete_round) + " " + roundNumber + "?")
            .setPositiveButton(R.string.ok) { _, _ ->
                repository.deleteRound(roundNumber)
                refreshExistingRoundsText()
                adapter.setItems(repository.getTeams().toMutableList())
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}
