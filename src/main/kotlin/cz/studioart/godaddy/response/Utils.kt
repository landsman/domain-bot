package cz.studioart.godaddy.response

import com.jakewharton.picnic.Table
import com.jakewharton.picnic.TextAlignment
import com.jakewharton.picnic.table

fun <T> measureDuration(block: () -> T): Pair<T, Double> {
    val startTime = System.currentTimeMillis()
    val result = block()
    val endTime = System.currentTimeMillis()
    val duration = (endTime - startTime) / 1000.0

    return Pair(result, duration)
}

fun formatSeconds(seconds: Double): String {
    return "%.3f".format(seconds) + "s"
}

fun yesOrNo(state: Boolean?, yes: String = "YES", no: String = "NO"): String {
    if (null == state) {
        return "???"
    }
    return if (state) yes else no
}

data class DomainCheckResult(val domain: String, val isAvailable: Boolean?, val timeTaken: Double)

fun buildTable(rows: List<DomainCheckResult>): Table {
    return table {
        cellStyle {
            border = true
            alignment = TextAlignment.MiddleCenter
        }
        header {
            row("Domain Name", "Available", "Time Taken (s)")
        }
        rows.forEach { result ->
            row(
                result.domain,
                yesOrNo(result.isAvailable),
                formatSeconds(result.timeTaken)
            )
        }
    }
}