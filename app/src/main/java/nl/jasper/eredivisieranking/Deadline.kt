package nl.jasper.eredivisieranking

import java.util.Calendar
import java.util.TimeZone

/**
 * One-time deadline for submitting/changing predictions: 18:00 local time
 * in the Netherlands on the day this feature was requested. Europe/Amsterdam
 * is used explicitly so the cutoff is correct regardless of CET/CEST.
 */
object Deadline {

    private val ZONE: TimeZone = TimeZone.getTimeZone("Europe/Amsterdam")

    val DEADLINE_MILLIS: Long = Calendar.getInstance(ZONE).apply {
        set(2026, Calendar.AUGUST, 7, 18, 0, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    fun isPast(): Boolean = System.currentTimeMillis() >= DEADLINE_MILLIS

    fun remainingText(): String {
        val diffMs = DEADLINE_MILLIS - System.currentTimeMillis()
        if (diffMs <= 0) return "Deadline verstreken (18:00)."
        val totalMinutes = diffMs / 60000
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        return if (hours > 0) {
            "Nog te wijzigen tot 18:00 vandaag (nog ${hours}u ${minutes}min)."
        } else {
            "Nog te wijzigen tot 18:00 vandaag (nog ${minutes}min)."
        }
    }
}
