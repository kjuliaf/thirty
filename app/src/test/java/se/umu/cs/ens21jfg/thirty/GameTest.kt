package se.umu.cs.ens21jfg.thirty

import org.junit.Test
import org.junit.Assert.*

class GameTest {
    @Test
    fun shouldCountCorrectLowResult() {
        val game = Game()
        game.dices[0].value = 1
        game.dices[1].value = 2
        game.dices[2].value = 3
        game.dices[3].value = 4
        game.dices[4].value = 5
        game.dices[5].value = 6
        game.saveResult()
        assertEquals(6, game.results.sumOf { it.score })
    }

    @Test
    fun shouldCountCorrectTargetResult() {
        val game = Game()
        game.dices[0].value = 1
        game.dices[1].value = 1
        game.dices[2].value = 1
        game.dices[3].value = 2
        game.dices[4].value = 4
        game.dices[5].value = 4
        game.selectedModeIndex = 2
        game.saveResult()
        assertEquals(10, game.results.sumOf { it.score })
    }
}