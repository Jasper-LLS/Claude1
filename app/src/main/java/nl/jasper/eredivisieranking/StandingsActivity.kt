package nl.jasper.eredivisieranking

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import nl.jasper.eredivisieranking.databinding.ActivityStandingsBinding

class StandingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStandingsBinding
    private lateinit var repository: Repository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStandingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        repository = Repository(this)
        render()
    }

    override fun onResume() {
        super.onResume()
        render()
    }

    private fun render() {
        val container = binding.containerStandings
        container.removeAllViews()

        val players = repository.getPlayers()
        val rounds = repository.getRoundNumbers()

        if (rounds.isEmpty()) {
            container.addView(textView(getString(R.string.no_rounds_yet), 16f, false))
            return
        }

        val totals = players.associate { it.id to Scoring.computeTotalScore(repository, it.id) }
        val ranked = players.sortedByDescending { totals[it.id] ?: 0 }

        // Leaderboard
        for ((index, player) in ranked.withIndex()) {
            val row = LinearLayout(this)
            row.orientation = LinearLayout.HORIZONTAL
            row.setPadding(0, 12, 0, 12)

            val place = textView("${index + 1}. ", 18f, true)
            val name = TextView(this)
            name.text = player.name
            name.textSize = 18f
            name.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

            val total = textView(getString(R.string.total_points, totals[player.id] ?: 0), 18f, true)
            total.setTextColor(resources.getColor(R.color.orange_primary, null))

            row.addView(place)
            row.addView(name)
            row.addView(total)
            container.addView(row)
        }

        val divider = View(this)
        divider.setBackgroundColor(resources.getColor(R.color.black, null))
        divider.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 2).apply {
            topMargin = 16
            bottomMargin = 16
        }
        container.addView(divider)

        // Per-round breakdown table
        val table = TableLayout(this)
        table.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val header = TableRow(this)
        header.addView(tableCell("Ronde", true))
        for (player in ranked) header.addView(tableCell(player.name, true))
        table.addView(header)

        for (round in rounds) {
            val row = TableRow(this)
            row.addView(tableCell(round.toString(), false))
            for (player in ranked) {
                val scores = Scoring.computeScoresPerRound(repository, player.id)
                row.addView(tableCell((scores[round] ?: 0).toString(), false))
            }
            table.addView(row)
        }

        container.addView(table)
    }

    private fun textView(text: String, size: Float, bold: Boolean): TextView {
        val tv = TextView(this)
        tv.text = text
        tv.textSize = size
        if (bold) tv.setTypeface(tv.typeface, Typeface.BOLD)
        return tv
    }

    private fun tableCell(text: String, bold: Boolean): TextView {
        val tv = TextView(this)
        tv.text = text
        tv.textSize = 14f
        tv.setPadding(16, 8, 16, 8)
        tv.gravity = Gravity.CENTER
        if (bold) tv.setTypeface(tv.typeface, Typeface.BOLD)
        return tv
    }
}
