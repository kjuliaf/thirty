package se.umu.cs.ens21jfg.thirty

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar

/**
 * @author Julia Forsberg, ens21jfg
 * @version 1.0
 *
 * GameActivity is the main activity of the application. It is responsible for the game.
 */
class GameActivity : AppCompatActivity() {
    private lateinit var game: Game
    private lateinit var throwButton: Button
    private lateinit var nextRoundButton: Button
    private lateinit var gameModeText: TextView
    private lateinit var diceImages: List<ImageView>

    /**
     * Sets up the games initial state. Is built on previous game state if one exists.
     * @param savedInstanceState the previous game state
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set up game, buttons, images, game modes
        game = savedInstanceState?.getParcelable("game") ?: Game()
        throwButton = findViewById(R.id.throwButton)
        nextRoundButton = findViewById(R.id.nextRoundButton)
        gameModeText = findViewById(R.id.gameModeText)
        initializeDiceImages()
        initializeGameModes()

        // Click listeners for throw, next round and dice selection
        setThrowButtonClickListener()
        setNextRoundButtonClickListener()
        setDiceSelectionListeners()

        updateUI()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    /**
     * Initializes the dice images.
     */
    private fun initializeDiceImages() {
        diceImages = listOf(
            findViewById(R.id.dice1),
            findViewById(R.id.dice2),
            findViewById(R.id.dice3),
            findViewById(R.id.dice4),
            findViewById(R.id.dice5),
            findViewById(R.id.dice6)
        )
    }

    /**
     * Initializes the game modes.
     */
    private fun initializeGameModes() {
        val gameModePicker = findViewById<CardView>(R.id.gameModePicker)
        gameModePicker.setOnClickListener { showGameModeDialog() }

        supportFragmentManager.setFragmentResultListener(GameModeDialog.ITEM_REQUEST_KEY, this) { _, bundle ->
            val selectedIndex = bundle.getInt("item", 0)
            game.selectedModeIndex = selectedIndex
            gameModeText.text = GAME_MODES[selectedIndex]
        }
    }

    /**
     * Sets the click listener for the throw button. Does not throw if no dices are selected.
     */
    private fun setThrowButtonClickListener() {
        throwButton.setOnClickListener {
            if (!(game.dices.any { it.isSelected }) && game.throwCount != 0) {
                return@setOnClickListener
            }
            game.rollDices()
            updateUI()
        }
    }

    /**
     * Sets the click listener for the next round button.
     */
    private fun setNextRoundButtonClickListener() {
        nextRoundButton.setOnClickListener {
            game.saveResult()
            val lastResult = game.results.lastOrNull()
            // Shows snackbar with result
            lastResult?.let {
                Snackbar.make(findViewById(R.id.main), "+${it.score} points in ${it.gameMode}'s", Snackbar.LENGTH_SHORT)
                    .setAnchorView(R.id.nextRoundButton)
                    .show()
            }
            // Goes to result activity if game is finished
            if (game.round == 10) {
                val intent = Intent(this, ResultActivity::class.java)
                intent.putParcelableArrayListExtra("results", ArrayList(game.results))
                startActivity(intent)
                finish()
            } else {
                game.nextRound()
                updateUI()
            }
        }
    }

    /**
     * Sets the click listeners for the dice images, for selecting dices on rethrow.
     */
    private fun setDiceSelectionListeners() {
        diceImages.forEachIndexed { index, image ->
            image.setOnClickListener {
                if (game.throwCount == 0 || !throwButton.isEnabled) return@setOnClickListener

                // All dice are preselected unless one is clicked - then the selections are reset and
                // the chosen one is selected instead
                if (!game.hasSelectedDice) {
                    game.dices.forEach { it.isSelected = false }
                    game.hasSelectedDice = true
                }

                game.dices[index].toggleSelected()
                updateUI()
            }
        }
    }

    /**
     * Changes how the dices are displayed and hides activity bar on rotation of screen.
     */
    override fun onResume() {
        super.onResume()

        val orientation = resources.configuration.orientation
        val diceLayout = findViewById<LinearLayout>(R.id.diceLayout)

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            supportActionBar?.hide()
            diceLayout.orientation = LinearLayout.HORIZONTAL
        } else {
            supportActionBar?.show()
            diceLayout.orientation = LinearLayout.VERTICAL
        }
    }

    /**
     * Shows dialog for choosing game mode.
     */
    private fun showGameModeDialog() {
        val gameModeDialogFragment = GameModeDialog.newInstance(1, "Pick a game mode", GAME_MODES, game.playedModes)
        gameModeDialogFragment.show(supportFragmentManager, "dialog")
    }

    /**
     * Updates the UI based on the current game state.
     */
    private fun updateUI() {
        // Update round count
        val roundCount = findViewById<TextView>(R.id.roundCount)
        "Round ${game.round}/10".also { roundCount.text = it }
        if (game.round == 10) {
            "Finish".also { nextRoundButton.text = it }
        }
        // Update game mode
        gameModeText.text = GAME_MODES[game.selectedModeIndex]

        // Update dices displayed
        game.dices.forEachIndexed { index, dice ->
            val resId = DICE_DRAWABLES[dice.value]
            val imageView = diceImages[index]
            imageView.setImageResource(resId)
            imageView.alpha = if (dice.isSelected) 1.0f else 0.4f
        }
        // Update throw/finish buttons
        if (game.throwCount > 0) {
            nextRoundButton.isEnabled = true
            "Rethrow ${game.throwCount}/2".also { throwButton.text = it }
        } else {
            nextRoundButton.isEnabled = false
            "Throw".also { throwButton.text = it }
        }
        if (game.throwCount >= 3) {
            throwButton.isEnabled = false
            "Rethrow 2/2".also { throwButton.text = it }
        } else {
            throwButton.isEnabled = true
        }
    }

    /**
     * Saves the current game state.
     * @param outState the current game state
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("game", game)
    }

    /**
     * Companion object containing constant related to the game.
     */
    companion object {
        val DICE_DRAWABLES = listOf(
            R.drawable.dice_0,
            R.drawable.dice_1,
            R.drawable.dice_2,
            R.drawable.dice_3,
            R.drawable.dice_4,
            R.drawable.dice_5,
            R.drawable.dice_6
        )

        val GAME_MODES = arrayOf("Low", "4", "5", "6", "7", "8", "9", "10", "11", "12")
    }
}