package app.sopadeletras.core

import java.time.LocalDate

fun dayNumber(year: Int, month: Int, day: Int): Long =
    LocalDate.of(year, month, day).toEpochDay()

fun daysSinceEpoch(year: Int, month: Int, day: Int): Long = dayNumber(year, month, day)

fun dailyCategoryIndex(year: Int, month: Int, day: Int): Int {
    val days = daysSinceEpoch(year, month, day)
    return ((days % 5) + 5).toInt() % 5
}

fun dailySeedNumber(year: Int, month: Int, day: Int): Long =
    year * 10000L + month * 100L + day

fun updateStreak(lastDay: Long, today: Long, streak: Int): Int = when {
    lastDay == today -> streak
    lastDay == today - 1 -> streak + 1
    else -> 1
}

fun displayedStreak(lastDay: Long, today: Long, streak: Int): Int =
    if (lastDay == today || lastDay == today - 1) streak else 0
