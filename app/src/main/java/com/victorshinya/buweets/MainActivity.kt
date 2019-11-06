package com.victorshinya.buweets

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    /**
     * Global variables
     */

    private lateinit var inputText: EditText

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private lateinit var progressDialog: ProgressDialog

    private var modelList: MutableList<NaturalLanguageUnderstandingModel> = ArrayList()

    /**
     * Activity lifecycle events
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewManager = LinearLayoutManager(this)
        viewAdapter = TweetsAdapter(modelList)

        recyclerView = findViewById<RecyclerView>(R.id.recyclerView).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        inputText = findViewById(R.id.inputText)
        inputText.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                if (inputText.text.trim().length >= 0) {
                    inputText.isEnabled = false
                    progressDialog.show()
                    analyze(inputText.text.toString())
                    inputText.setText("")
                    return@setOnKeyListener true
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.err_msg_blank_space),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            return@setOnKeyListener false
        }

        progressDialog = ProgressDialog(this@MainActivity)
        progressDialog.setTitle(getString(R.string.progress_title))
        progressDialog.setCancelable(false)
    }

    /**
     * Menu
     */

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.menu, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
//        R.id.menu_settings -> {
//            val newIntent = Intent(this@MainActivity, SettingsActivity::class.java)
//            startActivity(newIntent)
//            true
//        }
//        else -> super.onOptionsItemSelected(item)
//    }

    /**
     * Natural Language
     */

    private fun analyze(text: String) {
        val call = RetrofitInitializer().apiService.analyze(text)
        call.enqueue(object : Callback<NaturalLanguageUnderstandingModel> {
            override fun onResponse(
                call: Call<NaturalLanguageUnderstandingModel>,
                response: Response<NaturalLanguageUnderstandingModel>
            ) {
                response.body()?.let {
                    modelList.add(
                        0,
                        NaturalLanguageUnderstandingModel(
                            text,
                            it.emotion ?: Emotion(
                                EmotionDocument(
                                    EmotionContent(
                                        0.0,
                                        0.0,
                                        0.0,
                                        0.0,
                                        0.0
                                    )
                                )
                            ),
                            it.language ?: "en",
                            it.sentiment ?: Sentiment(SentimentDocument("neutral", 0.0)),
                            Date()
                        )
                    )
                    viewAdapter.notifyDataSetChanged()
                    inputText.isEnabled = true
                    progressDialog.dismiss()
                }
            }

            override fun onFailure(call: Call<NaturalLanguageUnderstandingModel>, t: Throwable) {
                Log.e(
                    "[ Main ] onFailure",
                    t.message ?: getString(R.string.err_msg_found)
                )
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.err_msg_try_again),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
