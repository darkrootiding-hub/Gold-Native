package com.example.util

import java.text.SimpleDateFormat
import java.util.*
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

data class FormattedDateTime(val date: String, val time: String)

object DateUtils {
    private val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
    private val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

    /**
     * Formats an ISO 8601 timestamp into a human-readable date and time.
     * Expected format: "2026-08-21T11:06:30.711649+05:45"
     */
    fun formatTimestamp(timestamp: String?): FormattedDateTime {
        if (timestamp.isNullOrBlank()) {
            return FormattedDateTime("उपलब्ध छैन", "उपलब्ध छैन")
        }

        return try {
            val odt = OffsetDateTime.parse(timestamp)
            val date = odt.format(dateFormatter)
            val time = odt.format(timeFormatter)

            // Append (BS) as placeholder as per requirements
            FormattedDateTime("$date (BS)", time)
        } catch (e: DateTimeParseException) {
            // Fallback for non-ISO formats or errors
            try {
                // Try to handle "YYYY-MM-DD HH:mm"
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                val date = sdf.parse(timestamp)
                val dateStr = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date)
                val timeStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date)
                FormattedDateTime("$dateStr (BS)", timeStr)
            } catch (e2: Exception) {
                FormattedDateTime("उपलब्ध छैन", "उपलब्ध छैन")
            }
        }
    }

    /**
     * Legacy support for splitDateTime to avoid breaking existing calls
     * if they aren't updated immediately.
     */
    fun splitDateTime(lastUpdated: String): Pair<String, String> {
        val formatted = formatTimestamp(lastUpdated)
        return formatted.date to formatted.time
    }

    /**
     * Legacy support for toNepalDate.
     */
    fun toNepalDate(gregorianDate: String): String {
        if (gregorianDate == "उपलब्ध छैन" || gregorianDate == "N/A") return "उपलब्ध छैन"
        // If it's already formatted with (BS), return it
        if (gregorianDate.contains("(BS)")) return gregorianDate

        return try {
            // Try to parse and format
            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val date = sdf.parse(gregorianDate)
            val formatted = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date)
            "$formatted (BS)"
        } catch (e: Exception) {
            gregorianDate
        }
    }
}
