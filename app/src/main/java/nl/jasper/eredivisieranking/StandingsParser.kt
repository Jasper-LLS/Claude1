package nl.jasper.eredivisieranking

sealed class ParseResult {
    data class Success(val orderedTeams: List<Team>) : ParseResult()
    data class Failure(val message: String) : ParseResult()
}

/**
 * Parses a pasted list of team names (one per line, or comma-separated) in
 * ranking order into the matching local Team objects. Lets you type/paste a
 * standings list -- e.g. one composed from a screenshot read out in chat --
 * instead of reordering 18 rows by hand.
 */
object StandingsParser {

    fun parse(rawText: String, catalog: List<Team>): ParseResult {
        val lines = rawText
            .split("\n", ",")
            .map { cleanLine(it) }
            .filter { it.isNotBlank() }

        if (lines.isEmpty()) {
            return ParseResult.Failure("Geen teamnamen gevonden in de geplakte tekst.")
        }

        val matched = mutableListOf<Team>()
        val usedIds = mutableSetOf<String>()
        val unmatched = mutableListOf<String>()

        for (line in lines) {
            val team = findTeam(line, catalog, usedIds)
            if (team == null) {
                unmatched.add(line)
            } else {
                matched.add(team)
                usedIds.add(team.id)
            }
        }

        if (unmatched.isNotEmpty()) {
            return ParseResult.Failure(
                "Kon deze regel(s) niet koppelen aan een team: " + unmatched.joinToString("; ")
            )
        }

        if (matched.size != catalog.size) {
            val missing = catalog.filter { it.id !in usedIds }.map { it.name }
            return ParseResult.Failure(
                "Niet alle ${catalog.size} teams staan in de lijst. Ontbreekt: " + missing.joinToString(", ")
            )
        }

        return ParseResult.Success(matched)
    }

    private fun cleanLine(raw: String): String {
        return raw.trim()
            // strip leading numbering like "1.", "1)", "1 -", "-", "*"
            .replace(Regex("^\\s*\\d{1,2}[.).\\-]?\\s*"), "")
            .replace(Regex("^[-*]\\s*"), "")
            .trim()
    }

    private fun findTeam(name: String, catalog: List<Team>, usedIds: Set<String>): Team? {
        val normalized = name.trim().lowercase()
        val available = catalog.filter { it.id !in usedIds }

        available.firstOrNull { it.name.trim().lowercase() == normalized }?.let { return it }

        val contains = available.filter {
            val teamName = it.name.trim().lowercase()
            teamName.contains(normalized) || normalized.contains(teamName)
        }
        return if (contains.size == 1) contains[0] else null
    }
}
