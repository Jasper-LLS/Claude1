package nl.jasper.eredivisieranking

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import java.util.UUID
import nl.jasper.eredivisieranking.databinding.ActivityPlayersBinding

class PlayersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayersBinding
    private lateinit var repository: Repository
    private lateinit var adapter: PlayerAdapter
    private val players = mutableListOf<Player>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        repository = Repository(this)

        adapter = PlayerAdapter(
            players,
            hasPrediction = { repository.hasPrediction(it.id) },
            onRank = { openRanking(it) },
            onDelete = { deletePlayer(it) }
        )
        binding.recyclerPlayers.layoutManager = LinearLayoutManager(this)
        binding.recyclerPlayers.adapter = adapter

        binding.buttonAddPlayer.setOnClickListener { addPlayer() }

        refresh()
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    private fun refresh() {
        adapter.setItems(repository.getPlayers())
    }

    private fun openRanking(player: Player) {
        val intent = Intent(this, RankingActivity::class.java)
        intent.putExtra(RankingActivity.EXTRA_PLAYER_ID, player.id)
        intent.putExtra(RankingActivity.EXTRA_PLAYER_NAME, player.name)
        startActivity(intent)
    }

    private fun deletePlayer(player: Player) {
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.delete) + ": " + player.name + "?")
            .setPositiveButton(R.string.ok) { _, _ ->
                val updated = repository.getPlayers().filter { it.id != player.id }
                repository.savePlayers(updated)
                refresh()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun addPlayer() {
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        input.hint = getString(R.string.player_name_hint)

        AlertDialog.Builder(this)
            .setTitle(R.string.add_player)
            .setView(input)
            .setPositiveButton(R.string.ok) { _, _ ->
                val name = input.text.toString().trim()
                if (name.isNotEmpty()) {
                    val updated = repository.getPlayers().toMutableList()
                    updated.add(Player(id = UUID.randomUUID().toString(), name = name))
                    repository.savePlayers(updated)
                    refresh()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}
