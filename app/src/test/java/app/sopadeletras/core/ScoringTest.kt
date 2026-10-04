package app.sopadeletras.core

import org.junit.Assert.assertEquals
import org.junit.Test

class ScoringTest {
    @Test
    fun `classic stars exact par is 3`() {
        // 8x8: k=20s, 6 words -> par=120
        assertEquals(3, starsFor(120, hints = 0, words = 6, n = 8))
    }

    @Test
    fun `classic stars exact 1-5x par is 2`() {
        assertEquals(2, starsFor(180, hints = 0, words = 6, n = 8))
    }

    @Test
    fun `classic stars above 1-5x par is 1`() {
        assertEquals(1, starsFor(181, hints = 0, words = 6, n = 8))
    }

    @Test
    fun `classic stars hints add 10s each`() {
        // effective 120 + 10 = 130 -> still 2 stars here would be 3 without hint
        assertEquals(3, starsFor(120, hints = 0, words = 6, n = 8))
        assertEquals(2, starsFor(120, hints = 6, words = 6, n = 8))
    }

    @Test
    fun `classic stars par scales with board`() {
        // 6x6 k=15, 10x10 k=25, 12x12 k=30
        assertEquals(3, starsFor(60, hints = 0, words = 4, n = 6))
        assertEquals(3, starsFor(200, hints = 0, words = 8, n = 10))
        assertEquals(3, starsFor(300, hints = 0, words = 10, n = 12))
    }

    @Test
    fun `color uses first free slot`() {
        assertEquals(0, nextColorIndex(emptySet(), 0))
        assertEquals(1, nextColorIndex(setOf(0), 1))
        assertEquals(2, nextColorIndex(setOf(0, 1), 5))
    }

    @Test
    fun `color wraps with count mod 5 when all used`() {
        assertEquals(0, nextColorIndex(setOf(0, 1, 2, 3, 4), 5))
        assertEquals(1, nextColorIndex(setOf(0, 1, 2, 3, 4), 6))
    }

    @Test
    fun `time attack word points`() {
        assertEquals(30, wordPoints(letters = 3, secondsSincePrevious = null))
        assertEquals(30, wordPoints(letters = 3, secondsSincePrevious = 6.0))
        assertEquals(35, wordPoints(letters = 3, secondsSincePrevious = 5.9))
    }

    @Test
    fun `time attack hint never drops below 1s`() {
        assertEquals(1, applyHintPenalty(remainingSeconds = 3, penalty = 5))
        assertEquals(10, applyHintPenalty(remainingSeconds = 15, penalty = 5))
    }

    @Test
    fun `time attack record only rises when beaten`() {
        assertEquals(true, recordBeaten(score = 101, record = 100))
        assertEquals(false, recordBeaten(score = 100, record = 100))
        assertEquals(false, recordBeaten(score = 50, record = 100))
    }

    @Test
    fun `mystery guess ignores case and accents`() {
        assertEquals(true, isMysteryGuessCorrect("zenite", "ZÉNITE"))
        assertEquals(true, isMysteryGuessCorrect("CAPSULA", "cápsula"))
        assertEquals(false, isMysteryGuessCorrect("NAVE", "TERRA"))
    }

    @Test
    fun `mystery stars guess-before-reveal is 3`() {
        assertEquals(3, mysteryStars(solvedByGuess = true, hintsUsed = 0))
    }

    @Test
    fun `mystery stars reveal-by-finding-all is 2`() {
        assertEquals(2, mysteryStars(solvedByGuess = false, hintsUsed = 0))
    }

    @Test
    fun `mystery stars each hint takes one minimum 1`() {
        assertEquals(2, mysteryStars(solvedByGuess = true, hintsUsed = 1))
        assertEquals(1, mysteryStars(solvedByGuess = true, hintsUsed = 5))
        assertEquals(1, mysteryStars(solvedByGuess = false, hintsUsed = 2))
    }
}
