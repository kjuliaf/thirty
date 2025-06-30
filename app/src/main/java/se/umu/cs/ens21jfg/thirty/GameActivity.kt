package se.umu.cs.ens21jfg.thirty

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class GameActivity : AppCompatActivity() {
    private lateinit var game: Game
    private lateinit var throwButton: Button
    private lateinit var nextRoundButton: Button
    private lateinit var gameModeText: TextView
    private lateinit var diceImages: List<ImageView>

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

    private fun initializeGameModes() {
        val gameModePicker = findViewById<CardView>(R.id.gameModePicker)
        gameModePicker.setOnClickListener { showGameModeDialog() }

        supportFragmentManager.setFragmentResultListener(ListAlertDialogFragment.ITEM_REQUEST_KEY, this) { _, bundle ->
            val selectedIndex = bundle.getInt("item", 0)
            game.selectedModeIndex = selectedIndex
            gameModeText.text = GAME_MODES[selectedIndex]
        }
    }

    private fun setThrowButtonClickListener() {
        throwButton.setOnClickListener {
            game.rollDices()
            updateUI()
        }
    }

    private fun setNextRoundButtonClickListener() {
        nextRoundButton.setOnClickListener {
            if (game.round == 10) {
                Log.d("Results", game.results.toString())
            } else {
                game.nextRound()
                updateUI()
            }
        }
    }

    private fun setDiceSelectionListeners() {
        diceImages.forEachIndexed { index, image ->
            image.setOnClickListener {
                if (game.throwCount == 0 || !throwButton.isEnabled) return@setOnClickListener

                if (!game.hasSelectedDice) {
                    game.dices.forEach { it.isSelected = false }
                    game.hasSelectedDice = true
                }

                game.dices[index].toggleSelected()
                updateUI()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val orientation = resources.configuration.orientation
        val linearLayout = findViewById<LinearLayout>(R.id.linearLayout)
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            supportActionBar?.hide()
            linearLayout.orientation = LinearLayout.HORIZONTAL
        } else {
            supportActionBar?.show()
            linearLayout.orientation = LinearLayout.VERTICAL
        }
    }

    private fun showGameModeDialog() {
        val newFragment1 = ListAlertDialogFragment.newInstance(1, "Pick a game mode", GAME_MODES, game.playedModes)
        newFragment1.show(supportFragmentManager, "dialog")
    }

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
            val resId = DICE_DRAWABLES[dice.value - 1]
            val imageView = diceImages[index]
            imageView.setImageResource(resId)
            imageView.alpha = if (dice.isSelected) 1.0f else 0.4f
        }
        // Update throw/finish buttons
        if (game.throwCount > 0) {
            nextRoundButton.isEnabled = true
        } else {
            nextRoundButton.isEnabled = false
        }
        if (game.throwCount >= 3) {
            throwButton.isEnabled = false
        } else {
            throwButton.isEnabled = true
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("game", game)
    }

    companion object {
        val DICE_DRAWABLES = listOf(
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