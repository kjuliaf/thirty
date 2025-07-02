package se.umu.cs.ens21jfg.thirty

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import java.util.ArrayList

/**
 * @author Julia Forsberg
 * @version 1.0
 *
 * ResultActivity is the activity that is used to display the result of the game.
 */
class ResultActivity : AppCompatActivity() {
    private lateinit var results: ArrayList<Result>

    /**
     * Sets up the result activity.
     * @param savedInstanceState The saved instance state.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        // Gets the results from saved state or the previous activity
        if (savedInstanceState != null) {
            results = savedInstanceState.getParcelableArrayList("results")!!
        } else {
            results = intent.getParcelableArrayListExtra("results")!!
        }

        // Displays the results
        val tableLayout = findViewById<TableLayout>(R.id.resultTable)
        displayRoundResults(tableLayout)
        displayTotalResult(tableLayout)

        setPlayAgainClickListener()
        setGoBackAction()
    }

    /**
     * Displays the round results.
     * @param tableLayout The table layout to display the results in.
     */
    private fun displayRoundResults(tableLayout: TableLayout) {
        results.forEachIndexed { index, result ->
            val row = TableRow(this).apply {
                addView(createResultRowText((index + 1).toString()))
                addView(result.gameMode?.let { createResultRowText(it) })
                addView(createResultRowText("${result.score} p"))
            }
            tableLayout.addView(row)
        }
    }

    /**
     * Displays the total result.
     * @param tableLayout The table layout to display the result in.
     */
    private fun displayTotalResult(tableLayout: TableLayout) {
        val total = results.sumOf { it.score }
        val totalRow = TableRow(this).apply {
            addView(createTotalResultRowText("Total"))
            addView(createTotalResultRowText(""))
            addView(createTotalResultRowText("$total p"))
        }
        tableLayout.addView(totalRow)
    }

    /**
     * Sets up the play again button click listener.
     */
    private fun setPlayAgainClickListener() {
        val playAgainButton = findViewById<Button>(R.id.playAgainButton)
        playAgainButton.setOnClickListener {
            startNewGame()
        }
    }

    /**
     * Sets up the go back functionality to start a new game.
     */
    private fun setGoBackAction() {
        val onBackPressedCallback = object : OnBackPressedCallback(true /* enabled by default */) {
            override fun handleOnBackPressed() {
                startNewGame()
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    /**
     * Starts a new game activity.
     */
    private fun startNewGame() {
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * Hides the action bar if the screen is in landscape mode.
     */
    override fun onResume() {
        super.onResume()
        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            supportActionBar?.hide()
        } else {
            supportActionBar?.show()
        }
    }

    /**
     * Creates a result text view.
     * @param result The result to display.
     */
    private fun createResultRowText(result: String): TextView {
        val style = ContextThemeWrapper(this, R.style.ResultTableRowStyle)
        val textView = TextView(style)
        textView.text = result
        return textView
    }

    /**
     * Creates a total result text view.
     * @param result The result to display.
     */
    private fun createTotalResultRowText(result: String): TextView {
        val style = ContextThemeWrapper(this, R.style.ResultTableTotalRowStyle)
        val textView = TextView(style)
        textView.text = result
        return textView
    }

    /**
     * Saves the results to the saved instance state.
     * @param outState The saved instance state.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList("results", results)
    }
}
