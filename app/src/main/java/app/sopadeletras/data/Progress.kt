package app.sopadeletras.data

fun progressStarsKey(language: String, level: Int): String = "cp_${language}_${level}_stars"

fun progressSecondsKey(language: String, level: Int): String = "cp_${language}_${level}_seconds"

fun campaignStars(entries: Map<String, Int>, language: String): Map<Int, Int> {
    val prefix = "cp_${language}_"
    val result = mutableMapOf<Int, Int>()
    for ((key, stars) in entries) {
        if (!key.startsWith(prefix) || !key.endsWith("_stars")) continue
        val level = key.removePrefix(prefix).removeSuffix("_stars").toIntOrNull() ?: continue
        result[level] = stars
    }
    return result
}

fun encodeFound(words: List<String>): String = words.joinToString(",")

fun decodeFound(value: String): List<String> =
    if (value.isEmpty()) emptyList() else value.split(",")

data class StoredResult(
    val mode: String,
    val language: String,
    val difficulty: String,
    val level: Int?,
    val n: Int,
    val category: String,
    val seconds: Int,
    val hints: Int,
    val stars: Int,
    val score: Int,
    val words: Int,
    val finishedAt: Long,
)

fun encodeResult(row: StoredResult): String = listOf(
    row.mode, row.language, row.difficulty, (row.level ?: 0).toString(),
    row.n.toString(), row.category, row.seconds.toString(), row.hints.toString(),
    row.stars.toString(), row.score.toString(), row.words.toString(), row.finishedAt.toString(),
).joinToString("|")

fun decodeResult(value: String): StoredResult {
    val parts = value.split("|")
    return StoredResult(
        mode = parts[0],
        language = parts[1],
        difficulty = parts[2],
        level = parts[3].toInt().takeIf { it != 0 },
        n = parts[4].toInt(),
        category = parts[5],
        seconds = parts[6].toInt(),
        hints = parts[7].toInt(),
        stars = parts[8].toInt(),
        score = parts[9].toInt(),
        words = parts[10].toInt(),
        finishedAt = parts[11].toLong(),
    )
}
