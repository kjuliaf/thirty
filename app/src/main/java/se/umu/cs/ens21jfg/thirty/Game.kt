package se.umu.cs.ens21jfg.thirty

import android.os.Parcel
import android.os.Parcelable
import se.umu.cs.ens21jfg.thirty.GameActivity.Companion.GAME_MODES

/**
 * @author Julia Forsberg, ens21jfg
 * @version 1.0
 *
 * Game represent a game of thirty.
 */
class Game() : Parcelable {
    val dices = MutableList(6) { Dice() }

    val playedModes = BooleanArray(13) { false }

    val results = mutableListOf<Result>()

    var round = 1
        private set

    var throwCount = 0
        private set

    var selectedModeIndex = 0

    var hasSelectedDice = false

    /**
     * Constructor used for creating a game from a parcel.
     * @param parcel The parcel to read the game from.
     */
    constructor(parcel: Parcel) : this() {
        parcel.readTypedList(dices, Dice.CREATOR)
        parcel.readBooleanArray(playedModes)
        parcel.readTypedList(results, Result.CREATOR)
        round = parcel.readInt()
        throwCount = parcel.readInt()
        selectedModeIndex = parcel.readInt()
        hasSelectedDice = parcel.readByte().toInt() != 0

    }

    /**
     * Rolls the dices. Rolls all on first throw, then rolls only selected dice.
     */
    fun rollDices() {
        if (throwCount == 0) {
            rollAllDices()
        } else {
            rollSelectedDices()
        }
        throwCount++
    }

    /**
     * Rolls all dices.
     */
    private fun rollAllDices() {
        dices.forEach {
            it.roll()
            it.isSelected = true
        }
        hasSelectedDice = false
    }

    /**
     * Rolls the selected dices.
     */
    private fun rollSelectedDices() {
        dices.forEach {
            if (it.isSelected) {
                it.roll()
            }
        }
        // Cannot make any more throws, preselection unnecessary
        if (throwCount >= 2) {
            dices.forEach {
                it.isSelected = true
            }
        }
    }

    /**
     * Saves the result of the game.
     */
    fun saveResult() {
        val calculatedScore: Int
        if (GAME_MODES[selectedModeIndex] == "Low") {
            calculatedScore = calcLowResult()
        } else {
            calculatedScore = calcTargetSumResult(GAME_MODES[selectedModeIndex].toInt())
        }
        results.add(Result(gameMode = GAME_MODES[selectedModeIndex], score = calculatedScore))
    }

    /**
     * Goes to the next round of the game.
     */
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

    /**
     * Calculates the score for the low mode.
     */
    private fun calcLowResult(): Int {
        val result = dices.filter { it.value <= 3 }.sumOf { it.value }
        return result
    }

    /**
     * Calculates the score for the target mode.
     * @param targetSum The target sum for a combination of dice(s).
     */
    private fun calcTargetSumResult(targetSum: Int): Int {
        // Sorts descending for more effective counting
        val diceValues = dices.map { it.value }.sortedDescending()
        val usedIndices = mutableSetOf<Int>()
        var totalScore = 0

        // Calculates score of combinations
        do {
            // Gets dices that have not been already used in combinations
            val availableDices = diceValues.withIndex()
                .filter { it.index !in usedIndices }
                .map { it.value to it.index }

            // Finds new combination and adds its score to total
            val newCombination = findCombination(availableDices, targetSum)
            if (newCombination != null) {
                usedIndices.addAll(newCombination)
                totalScore += targetSum
            }
        } while (newCombination != null)

        return totalScore
    }

    /**
     * Finds a combination of dice that adds up to the target.
     * @param availableDices The list of available dices.
     * @param targetSum The target sum for a combination of dice(s).
     */
    private fun findCombination(availableDices: List<Pair<Int, Int>>, targetSum: Int): List<Int>? {
        /**
         * Backtracking algorithm for finding a combination of dice that adds up to the target.
         * @param index The index of the current dice.
         * @param currentSum The current sum of the dice(s).
         * @param combination The current combination of dice.
         */
        fun backtrack(index: Int, currentSum: Int, combination: MutableList<Int>): List<Int>? {
            // Found combination
            if (currentSum == targetSum) {
                return combination
            }
            // Found no combination
            if (currentSum > targetSum || index >= availableDices.size) {
                return null
            }
            // Recursively tries to find combination
            val (value, originalIndex) = availableDices[index]
            combination.add(originalIndex)
            backtrack(index + 1, currentSum + value, combination)?.let { return it }
            combination.removeAt(combination.size - 1)

            return backtrack(index + 1, currentSum, combination)
        }

        return backtrack(0, 0, mutableListOf())
    }

    /**
     * Describes the contents of the parcel.
     */
    override fun describeContents(): Int {
        return 0
    }

    /**
     * Writes the game to a parcel.
     * @param dest The parcel to write the game to.
     * @param flags Additional flags about how the object should be written.
     */
    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(round)
        dest.writeInt(selectedModeIndex)
        dest.writeInt(throwCount)
        dest.writeByte(if (hasSelectedDice) 1 else 0)
        dest.writeTypedList(dices)
        dest.writeBooleanArray(playedModes)
        dest.writeTypedList(results)
    }

    /**
     * Companion object for creating a game from a parcel.
     */
    companion object CREATOR : Parcelable.Creator<Game> {
        override fun createFromParcel(parcel: Parcel): Game {
            return Game(parcel)
        }

        override fun newArray(size: Int): Array<Game?> {
            return arrayOfNulls(size)
        }
    }
}