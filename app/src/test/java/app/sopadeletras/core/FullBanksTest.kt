package app.sopadeletras.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * M8 gate: every campaign level in both languages generates a valid,
 * deterministic board from the real shipped banks, with strict uniqueness
 * and working mystery levels (no classic fallback).
 */
class FullBanksTest {
    private data class Bank(val words: List<String>, val mysteries: Map<Int, MysteryEntry>)

    private fun loadBank(lang: String, id: String): Bank {
        val file = File("src/main/assets/words/$lang/$id.json")
        assertTrue("missing bank file: ${file.path} (run from the app/ module dir)", file.exists())
        val text = file.readText(Charsets.UTF_8)
        val wordsBlock = text.substringAfter("\"words\":").trim().removePrefix("[").substringBeforeLast("]")
        val words = Regex("\"([A-Z]+)\"").findAll(wordsBlock).map { it.groupValues[1] }.toList()
        val mysteries = mutableMapOf<Int, MysteryEntry>()
        val entryRe = Regex("\\{\\s*\"clue\"\\s*:\\s*\"([^\"]*)\"\\s*,\\s*\"word\"\\s*:\\s*\"([A-Z]+)\"\\s*\\}")
        val entryRe2 = Regex("\\{\\s*\"word\"\\s*:\\s*\"([A-Z]+)\"\\s*,\\s*\"clue\"\\s*:\\s*\"([^\"]*)\"\\s*\\}")
        for (match in entryRe.findAll(text)) {
            mysteries[match.groupValues[2].length] = MysteryEntry(match.groupValues[2], match.groupValues[1])
        }
        for (match in entryRe2.findAll(text)) {
            mysteries[match.groupValues[1].length] = MysteryEntry(match.groupValues[1], match.groupValues[2])
        }
        assertTrue("$lang/$id: no words parsed", words.isNotEmpty())
        assertTrue("$lang/$id: no mysteries parsed", mysteries.isNotEmpty())
        return Bank(words, mysteries)
    }

    @Test
    fun `all 300 levels generate unique deterministic boards in both languages`() {
        val ptBanks = (0..9).map { loadBank("pt-PT", CAMPAIGN_IDS[it]) }
        val enBanks = (0..9).map { loadBank("en-US", CAMPAIGN_IDS[it]) }
        for ((language, banks, fill) in listOf(
            Triple("pt-PT", ptBanks, Language.PT_FILL),
            Triple("en-US", enBanks, Language.EN_FILL),
        )) {
            for (level in 1..300) {
                val info = levelInfo(level)
                val seed = campaignSeed(level, language)
                val bank = banks[info.category]
                val puzzle = if (info.kind == LevelKind.MYSTERY) {
                    PuzzleGenerator.genMystery(minOf(info.n, 10), bank.words, bank.mysteries, seed, fill)
                } else {
                    PuzzleGenerator.generate(levelParams(info), bank.words, seed, fill)
                }
                val again = if (info.kind == LevelKind.MYSTERY) {
                    PuzzleGenerator.genMystery(minOf(info.n, 10), bank.words, bank.mysteries, seed, fill)
                } else {
                    PuzzleGenerator.generate(levelParams(info), bank.words, seed, fill)
                }
                assertEquals("L$level $language deterministic", puzzle.letters, again.letters)
                if (info.kind == LevelKind.MYSTERY) {
                    val mystery = puzzle.mystery
                    assertTrue("L$level $language mystery fell back to classic", mystery != null)
                    requireNotNull(mystery)
                    val free = mystery.cells
                    assertEquals(
                        "L$level $language mystery reads back",
                        mystery.word,
                        free.map { puzzle.letters[it] }.joinToString(""),
                    )
                } else {
                    assertEquals("L$level $language words", info.words, puzzle.words.size)
                    for (w in puzzle.words) {
                        assertEquals(
                            "L$level $language single ${w.word}",
                            1,
                            PuzzleGenerator.countOccurrences(puzzle.letters, puzzle.n, w.word),
                        )
                    }
                }
            }
        }
    }

    companion object {
        private val CAMPAIGN_IDS = listOf(
            "espaco", "oceano", "comida", "animais", "paises",
            "natureza", "cidade", "desporto", "casa", "corpo",
        )
    }
}
