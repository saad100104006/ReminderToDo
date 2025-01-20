package com.tanvir.reminder.util


enum class Recurrence(val displayName: String) {
    FifteenMinutes("15 Minutes"),
    ThirtyMinutes("30 Minutes"),
    FortyFiveMinutes("45 Minutes"),
    Hourly("Hourly"),
    Daily("Daily"),
    Weekly("Weekly"),
    Monthly("Monthly");
}

fun getRecurrenceList(): List<Recurrence> {
    return Recurrence.values().toList()
}
