package nl.jasper.eredivisieranking

object Scoring {

    /**
     * prediction and actual are ordered lists of team IDs (index 0 = position 1).
     * Exact position match = 3 points, off by exactly one place = 1 point, otherwise 0.
     */
    fun computeRoundScore(prediction: List<String>, actual: List<String>): Int {
        val predictedPosition = prediction.withIndex().associate { (idx, teamId) -> teamId to idx + 1 }
        val actualPosition = actual.withIndex().associate { (idx, teamId) -> teamId to idx + 1 }

        var total = 0
        for ((teamId, actualPos) in actualPosition) {
            val predPos = predictedPosition[teamId] ?: continue
            val diff = Math.abs(predPos - actualPos)
            total += when (diff) {
                0 -> 3
                1 -> 1
                else -> 0
            }
        }
        return total
    }

    /** roundNumber -> score, for a given player across all recorded rounds. */
    fun computeScoresPerRound(repository: Repository, playerId: String): Map<Int, Int> {
        val prediction = repository.getPrediction(playerId)
        val result = mutableMapOf<Int, Int>()
        for (round in repository.getRoundNumbers()) {
            val actual = repository.getRoundStanding(round) ?: continue
            result[round] = computeRoundScore(prediction, actual)
        }
        return result
    }

    fun computeTotalScore(repository: Repository, playerId: String): Int {
        return computeScoresPerRound(repository, playerId).values.sum()
    }
}
