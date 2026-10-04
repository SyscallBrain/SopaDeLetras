package app.sopadeletras.ui.stats

data class ResultRow(
    val mode: String,
    val n: Int,
    val seconds: Int,
    val stars: Int,
    val score: Int,
    val words: Int,
)

fun wins(results: List<ResultRow>): Int = results.size

fun wordsFound(results: List<ResultRow>): Int = results.sumOf { it.words }

fun bestClassicTimes(results: List<ResultRow>): Map<Int, Int?> {
    val classic = results.filter { it.mode == "CAMPAIGN" || it.mode == "FREE" }
    return mapOf(
        6 to classic.filter { it.n == 6 }.minOfOrNull { it.seconds },
        8 to classic.filter { it.n == 8 }.minOfOrNull { it.seconds },
        10 to classic.filter { it.n == 10 }.minOfOrNull { it.seconds },
        12 to classic.filter { it.n == 12 }.minOfOrNull { it.seconds },
    )
}

fun bestTimeAttack(results: List<ResultRow>): Int? =
    results.filter { it.mode == "TIME" }.maxOfOrNull { it.score }

fun mysteriesSolved(results: List<ResultRow>): Int =
    results.count { it.mode == "MYSTERY" }

fun campaignTotals(progress: Map<Int, Int>): Pair<Int, Int> =
    progress.size to progress.values.sum()
