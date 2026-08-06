package nl.jasper.eredivisieranking

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class Repository(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("eredivisie_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TEAMS = "teams"
        private const val KEY_PLAYERS = "players"
        private const val KEY_PREDICTIONS = "predictions"
        private const val KEY_ROUNDS = "rounds"

        val DEFAULT_TEAM_NAMES = listOf(
            "Ajax", "PSV", "Feyenoord", "AZ", "FC Twente", "FC Utrecht",
            "sc Heerenveen", "Go Ahead Eagles", "Sparta Rotterdam", "NEC Nijmegen",
            "FC Groningen", "Fortuna Sittard", "Heracles Almelo", "PEC Zwolle",
            "NAC Breda", "FC Volendam", "Excelsior", "Telstar"
        )

        val DEFAULT_PLAYER_NAMES = listOf("Jasper", "Zoon")
    }

    // ---------- Teams ----------

    fun getTeams(): List<Team> {
        val raw = prefs.getString(KEY_TEAMS, null)
        if (raw == null) {
            val defaults = DEFAULT_TEAM_NAMES.map { Team(id = slug(it), name = it) }
            saveTeams(defaults)
            return defaults
        }
        return parseTeams(raw)
    }

    fun saveTeams(teams: List<Team>) {
        val arr = JSONArray()
        for (t in teams) {
            val o = JSONObject()
            o.put("id", t.id)
            o.put("name", t.name)
            arr.put(o)
        }
        prefs.edit().putString(KEY_TEAMS, arr.toString()).apply()
    }

    fun resetDefaultTeams() {
        val defaults = DEFAULT_TEAM_NAMES.map { Team(id = slug(it), name = it) }
        saveTeams(defaults)
    }

    private fun parseTeams(raw: String): List<Team> {
        val arr = JSONArray(raw)
        val result = mutableListOf<Team>()
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            result.add(Team(o.getString("id"), o.getString("name")))
        }
        return result
    }

    // ---------- Players ----------

    fun getPlayers(): List<Player> {
        val raw = prefs.getString(KEY_PLAYERS, null)
        if (raw == null) {
            val defaults = DEFAULT_PLAYER_NAMES.map { Player(id = UUID.randomUUID().toString(), name = it) }
            savePlayers(defaults)
            return defaults
        }
        val arr = JSONArray(raw)
        val result = mutableListOf<Player>()
        for (i in 0 until arr.length()) {
            val o = arr.getJSONObject(i)
            result.add(Player(o.getString("id"), o.getString("name")))
        }
        return result
    }

    fun savePlayers(players: List<Player>) {
        val arr = JSONArray()
        for (p in players) {
            val o = JSONObject()
            o.put("id", p.id)
            o.put("name", p.name)
            arr.put(o)
        }
        prefs.edit().putString(KEY_PLAYERS, arr.toString()).apply()
    }

    // ---------- Predictions ----------

    /** Returns ordered list of team IDs, index 0 = position 1. */
    fun getPrediction(playerId: String): List<String> {
        val raw = prefs.getString(KEY_PREDICTIONS, null) ?: return getTeams().map { it.id }
        val obj = JSONObject(raw)
        if (!obj.has(playerId)) return getTeams().map { it.id }
        val arr = obj.getJSONArray(playerId)
        val result = mutableListOf<String>()
        for (i in 0 until arr.length()) result.add(arr.getString(i))
        return result
    }

    fun savePrediction(playerId: String, teamIds: List<String>) {
        val raw = prefs.getString(KEY_PREDICTIONS, null)
        val obj = if (raw == null) JSONObject() else JSONObject(raw)
        val arr = JSONArray()
        for (id in teamIds) arr.put(id)
        obj.put(playerId, arr)
        prefs.edit().putString(KEY_PREDICTIONS, obj.toString()).apply()
    }

    fun hasPrediction(playerId: String): Boolean {
        val raw = prefs.getString(KEY_PREDICTIONS, null) ?: return false
        return JSONObject(raw).has(playerId)
    }

    // ---------- Rounds (actual standings) ----------

    fun getRoundNumbers(): List<Int> {
        val raw = prefs.getString(KEY_ROUNDS, null) ?: return emptyList()
        val obj = JSONObject(raw)
        return obj.keys().asSequence().map { it.toInt() }.sorted().toList()
    }

    fun getRoundStanding(roundNumber: Int): List<String>? {
        val raw = prefs.getString(KEY_ROUNDS, null) ?: return null
        val obj = JSONObject(raw)
        val key = roundNumber.toString()
        if (!obj.has(key)) return null
        val arr = obj.getJSONArray(key)
        val result = mutableListOf<String>()
        for (i in 0 until arr.length()) result.add(arr.getString(i))
        return result
    }

    fun saveRoundStanding(roundNumber: Int, teamIds: List<String>) {
        val raw = prefs.getString(KEY_ROUNDS, null)
        val obj = if (raw == null) JSONObject() else JSONObject(raw)
        val arr = JSONArray()
        for (id in teamIds) arr.put(id)
        obj.put(roundNumber.toString(), arr)
        prefs.edit().putString(KEY_ROUNDS, obj.toString()).apply()
    }

    fun deleteRound(roundNumber: Int) {
        val raw = prefs.getString(KEY_ROUNDS, null) ?: return
        val obj = JSONObject(raw)
        obj.remove(roundNumber.toString())
        prefs.edit().putString(KEY_ROUNDS, obj.toString()).apply()
    }

    private fun slug(name: String): String {
        return name.lowercase()
            .replace(Regex("[^a-z0-9]+"), "_")
            .trim('_')
    }
}
