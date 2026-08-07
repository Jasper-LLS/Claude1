package nl.jasper.eredivisieranking

import android.util.Base64
import org.json.JSONArray
import org.json.JSONObject

data class DecodedPrediction(val playerName: String, val orderedTeamNames: List<String>)

/**
 * Encodes/decodes a player's prediction as a short text code that can be
 * shared once between two installs of the app (WhatsApp, SMS, copy-paste, ...)
 * so both phones can locally compute a combined leaderboard.
 */
object ShareCode {

    private const val PREFIX = "ERV1:"

    fun encode(playerName: String, orderedTeamNames: List<String>): String {
        val obj = JSONObject()
        obj.put("player", playerName)
        val arr = JSONArray()
        for (name in orderedTeamNames) arr.put(name)
        obj.put("teams", arr)
        val json = obj.toString()
        val encoded = Base64.encodeToString(json.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
        return PREFIX + encoded
    }

    fun decode(text: String): DecodedPrediction? {
        val trimmed = text.trim()
        val payload = when {
            trimmed.startsWith(PREFIX) -> trimmed.removePrefix(PREFIX)
            else -> trimmed
        }
        return try {
            val json = String(Base64.decode(payload, Base64.NO_WRAP), Charsets.UTF_8)
            val obj = JSONObject(json)
            val playerName = obj.getString("player")
            val arr = obj.getJSONArray("teams")
            val names = mutableListOf<String>()
            for (i in 0 until arr.length()) names.add(arr.getString(i))
            if (playerName.isBlank() || names.isEmpty()) null else DecodedPrediction(playerName, names)
        } catch (e: Exception) {
            null
        }
    }
}
