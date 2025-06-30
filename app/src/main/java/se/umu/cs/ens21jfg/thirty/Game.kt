package se.umu.cs.ens21jfg.thirty

import android.os.Parcel
import android.os.Parcelable
import se.umu.cs.ens21jfg.thirty.GameActivity.Companion.GAME_MODES
import java.io.Serializable

data class RoundResult(
    val mode: String,
    val score: Int
) : Serializable

class Game() : Parcelable {
    var round = 1
        private set

    var selectedModeIndex = 0

    var throwCount = 0
        private set

    var hasSelectedDice = false

    var dices = MutableList(6) { Dice() }
        private set

    var playedModes = BooleanArray(13) { false }
        private set

    val results = mutableListOf<RoundResult>()

    constructor(parcel: Parcel) : this() {
        round = parcel.readInt()
        selectedModeIndex = parcel.readInt()
        throwCount = parcel.readInt()
        hasSelectedDice = parcel.readByte().toInt() != 0
        parcel.readTypedList(dices, Dice.CREATOR)
        playedModes = parcel.createBooleanArray() ?: BooleanArray(13) { false }
    }

    fun rollDices() {
        if (throwCount == 0) {
            rollAllDices()
        } else {
            rollSelectedDices()
        }
        throwCount++
    }

    private fun rollAllDices() {
        dices.forEach {
            it.roll()
        }
        updateDiceSelection()
    }

    private fun rollSelectedDices() {
        dices.forEach {
            if (it.isSelected) {
                it.roll()
            }
        }
        updateDiceSelection()
    }

    private fun updateDiceSelection() {
        dices.forEach {
            it.isSelected = true
        }
        hasSelectedDice = false
    }

    fun saveResult() {
        val calculatedScore: Int
        if (GAME_MODES[selectedModeIndex] == "Low") {
            calculatedScore = calcLowResult()
        } else {
            calculatedScore = calcPointsForTarget(GAME_MODES[selectedModeIndex].toInt())
        }
        results.add(RoundResult(mode = GAME_MODES[selectedModeIndex], score = calculatedScore))
    }

    fun nextRound() {
        // Update game
        round++
        throwCount = 0
        playedModes[selectedModeIndex] = true
        dices.forEach {
            it.isSelected = false
        }
        // Get next not already played mode
        val newSelectedIndex = playedModes.indexOfFirst { !it }
        selectedModeIndex = newSelectedIndex
    }

    private fun calcLowResult(): Int {
        val result = dices.filter { it.value <= 3 }.sumOf { it.value }
        return result
    }

    private fun calcPointsForTarget(target: Int): Int {
        val values = dices.map { it.value }.toMutableList()
        return target * countCombinations(values, target)
    }

    private fun countCombinations(values: MutableList<Int>, target: Int): Int {
        val usedIndices = mutableListOf<Int>()
        val found = findCombination(values, target, 0, usedIndices)
        if (!found) return 0

        usedIndices.sortedDescending().forEach { values.removeAt(it) }

        return 1 + countCombinations(values, target)
    }

    private fun findCombination(values: List<Int>, target: Int, start: Int, usedIndices: MutableList<Int>): Boolean {
        if (target == 0) return true
        if (target < 0 || start >= values.size) return false

        usedIndices.add(start)
        if (findCombination(values, target - values[start], start + 1, usedIndices)) {
            return true
        }
        usedIndices.removeAt(usedIndices.size - 1)

        return findCombination(values, target, start + 1, usedIndices)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(round)
        dest.writeInt(selectedModeIndex)
        dest.writeInt(throwCount)
        dest.writeByte(if (hasSelectedDice) 1 else 0)
        dest.writeTypedList(dices)
        dest.writeBooleanArray(playedModes)
    }

    companion object CREATOR : Parcelable.Creator<Game> {
        override fun createFromParcel(parcel: Parcel): Game {
            return Game(parcel)
        }

        override fun newArray(size: Int): Array<Game?> {
            return arrayOfNulls(size)
        }
    }
}