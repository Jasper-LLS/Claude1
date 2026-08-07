package nl.jasper.eredivisieranking

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import nl.jasper.eredivisieranking.databinding.ActivityRankingBinding

class RankingActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_PLAYER_ID = "player_id"
        const val EXTRA_PLAYER_NAME = "player_name"
    }

    private lateinit var binding: ActivityRankingBinding
    private lateinit var repository: Repository
    private lateinit var adapter: RankAdapter
    private lateinit var playerId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRankingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        repository = Repository(this)

        playerId = intent.getStringExtra(EXTRA_PLAYER_ID) ?: run { finish(); return }
        val playerName = intent.getStringExtra(EXTRA_PLAYER_NAME) ?: ""
        binding.textTitle.text = getString(R.string.title_ranking, playerName)

        val teams = repository.getTeams()
        val teamsById = teams.associateBy { it.id }
        val savedOrder = repository.getPrediction(playerId)
        val orderedTeams = savedOrder.mapNotNull { teamsById[it] }.toMutableList()
        // Append any teams not present in the saved order yet (e.g. newly added teams)
        for (t in teams) if (orderedTeams.none { it.id == t.id }) orderedTeams.add(t)

        val locked = Deadline.isPast()
        adapter = RankAdapter(orderedTeams, locked = locked)
        binding.recyclerRanking.layoutManager = LinearLayoutManager(this)
        binding.recyclerRanking.adapter = adapter

        if (locked) {
            binding.textDeadline.text = getString(R.string.deadline_passed_message)
            binding.textDeadline.setTextColor(getColor(R.color.orange_dark))
            binding.buttonSave.isEnabled = false
            binding.buttonSave.alpha = 0.4f
        } else {
            binding.textDeadline.text = Deadline.remainingText()
            binding.textDeadline.setTextColor(getColor(R.color.green_ok))
        }

        binding.buttonSave.setOnClickListener {
            if (Deadline.isPast()) {
                adapter.applyLocked(true)
                binding.textDeadline.text = getString(R.string.deadline_passed_message)
                binding.textDeadline.setTextColor(getColor(R.color.orange_dark))
                binding.buttonSave.isEnabled = false
                binding.buttonSave.alpha = 0.4f
                Toast.makeText(this, R.string.deadline_passed_title, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            repository.savePrediction(playerId, adapter.getItems().map { it.id })
            Toast.makeText(this, getString(R.string.ranking_saved, playerName), Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
