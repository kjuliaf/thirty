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
        if (throwCount >= 2) {
            updateDiceSelection()
        }
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
            it.reset()
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
        val diceValues = dices.map { it.value }.sortedDescending()
        val usedIndices = mutableSetOf<Int>()
        var totalScore = 0

        do {
            val available = diceValues.withIndex()
                .filter { it.index !in usedIndices }
                .map { it.value to it.index }

            val newCombination = findCombinationThatSumsToTarget(available, target)
            if (newCombination != null) {
                usedIndices.addAll(newCombination)
                totalScore += target
            }
        } while (newCombination != null)

        return totalScore
    }

    private fun findCombinationThatSumsToTarget(available: List<Pair<Int, Int>>, target: Int): List<Int>? {
        fun backtrack(index: Int, currentSum: Int, path: MutableList<Int>): List<Int>? {
            if (currentSum == target) return path.toList()
            if (currentSum > target || index >= available.size) return null

            val (value, originalIndex) = available[index]

            path.add(originalIndex)
            backtrack(index + 1, currentSum + value, path)?.let { return it }
            path.removeAt(path.size - 1)

            return backtrack(index + 1, currentSum, path)
        }

        return backtrack(0, 0, mutableListOf())
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