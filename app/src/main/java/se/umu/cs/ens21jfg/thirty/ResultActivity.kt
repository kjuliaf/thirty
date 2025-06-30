package se.umu.cs.ens21jfg.thirty

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val results = intent.getSerializableExtra("results") as? ArrayList<RoundResult>
        val tableLayout = findViewById<TableLayout>(R.id.resultTable)

        val headerRow = TableRow(this).apply {
            addView(createTextView("Round", bold = true))
            addView(createTextView("Mode", bold = true))
            addView(createTextView("Score", bold = true))
        }
        tableLayout.addView(headerRow)

        results?.forEachIndexed { index, result ->
            val row = TableRow(this).apply {
                addView(createTextView((index + 1).toString()))
                addView(createTextView(result.mode))
                addView(createTextView("${result.score}p"))
            }
            tableLayout.addView(row)
        }

        val total = results?.sumOf { it.score }
        val totalRow = TableRow(this).apply {
            addView(createTextView(""))
            addView(createTextView("Total", bold = true))
            addView(createTextView("$total p", bold = true))
        }
        tableLayout.addView(totalRow)

        val playAgainButton = findViewById<Button>(R.id.playAgainButton)
        playAgainButton.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            supportActionBar?.hide()
        } else {
            supportActionBar?.show()
        }
    }

    private fun createTextView(text: String, bold: Boolean = false): TextView {
        return TextView(this).apply {
            this.text = text
            textSize = 16f
            setPadding(12, 8, 12, 8)
            if (bold) setTypeface(typeface, android.graphics.Typeface.BOLD)
        }
    }
}
