package nl.jasper.eredivisieranking

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import nl.jasper.eredivisieranking.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonPlayers.setOnClickListener {
            startActivity(Intent(this, PlayersActivity::class.java))
        }
        binding.buttonRound.setOnClickListener {
            startActivity(Intent(this, RoundEntryActivity::class.java))
        }
        binding.buttonStandings.setOnClickListener {
            startActivity(Intent(this, StandingsActivity::class.java))
        }
        binding.buttonTeams.setOnClickListener {
            startActivity(Intent(this, TeamsActivity::class.java))
        }
    }
}
