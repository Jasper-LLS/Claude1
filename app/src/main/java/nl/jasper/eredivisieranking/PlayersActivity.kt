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
            onShare = { sharePrediction(it) },
            onDelete = { deletePlayer(it) }
        )
        binding.recyclerPlayers.layoutManager = LinearLayoutManager(this)
        binding.recyclerPlayers.adapter = adapter

        binding.buttonAddPlayer.setOnClickListener { addPlayer() }
        binding.buttonImport.setOnClickListener { importPrediction() }

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

    private fun sharePrediction(player: Player) {
        if (!repository.hasPrediction(player.id)) {
            android.widget.Toast.makeText(
                this, getString(R.string.share_no_prediction, player.name), android.widget.Toast.LENGTH_SHORT
            ).show()
            return
        }
        val teamsById = repository.getTeams().associateBy { it.id }
        val orderedNames = repository.getPrediction(player.id).mapNotNull { teamsById[it]?.name }
        val code = ShareCode.encode(player.name, orderedNames)

        val sendIntent = Intent(Intent.ACTION_SEND)
        sendIntent.type = "text/plain"
        sendIntent.putExtra(Intent.EXTRA_TEXT, code)
        startActivity(Intent.createChooser(sendIntent, getString(R.string.share_chooser_title, player.name)))
    }

    private fun importPrediction() {
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
        input.hint = getString(R.string.import_dialog_hint)
        input.minLines = 3

        AlertDialog.Builder(this)
            .setTitle(R.string.import_prediction)
            .setView(input)
            .setPositiveButton(R.string.ok) { _, _ ->
                val decoded = ShareCode.decode(input.text.toString())
                if (decoded == null) {
                    android.widget.Toast.makeText(this, R.string.import_failed, android.widget.Toast.LENGTH_LONG).show()
                } else {
                    repository.upsertPlayerPrediction(decoded.playerName, decoded.orderedTeamNames)
                    android.widget.Toast.makeText(
                        this, getString(R.string.import_success, decoded.playerName), android.widget.Toast.LENGTH_SHORT
                    ).show()
                    refresh()
                }
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
