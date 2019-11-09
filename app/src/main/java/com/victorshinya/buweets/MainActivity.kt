package com.victorshinya.buweets

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlin.collections.ArrayList
import okhttp3.*
import java.io.IOException
import java.util.*


class MainActivity : AppCompatActivity() {

    /**
     * Global variables
     */

    private lateinit var inputText: EditText
    private lateinit var textInstruction: TextView

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private lateinit var progressDialog: ProgressDialog

    private var modelList: MutableList<NaturalLanguageUnderstandingModel> = ArrayList()

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var apiURL: String

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
                if (inputText.text.trim().length >= 15) {
                    if (apiURL == "") {
                        Log.e("[ analyze ]", "No API provided")
                        Toast.makeText(
                            this@MainActivity,
                            getString(R.string.err_msg_no_api),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        inputText.isEnabled = false
                        progressDialog.show()
                        analyze(inputText.text.toString())
                    }
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.err_msg_blank_space),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                inputText.setText("")
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

        progressDialog = ProgressDialog(this@MainActivity)
        progressDialog.setTitle(getString(R.string.progress_title))
        progressDialog.setCancelable(false)

        sharedPreferences =
            getSharedPreferences(Constants.SHARED_PREFERENCES_REF_KEY, Context.MODE_PRIVATE)
        apiURL = sharedPreferences.getString(Constants.SHARED_PREFERENCES_API_URL_KEY, "")!!

        textInstruction = findViewById(R.id.textInstruction)
    }

    override fun onResume() {
        super.onResume()

        sharedPreferences =
            getSharedPreferences(Constants.SHARED_PREFERENCES_REF_KEY, Context.MODE_PRIVATE)
        apiURL = sharedPreferences.getString(Constants.SHARED_PREFERENCES_API_URL_KEY, "")!!
    }

    /**
     * Menu
     */

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.menu_settings -> {
            val newIntent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(newIntent)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    /**
     * Natural Language
     */

    private fun analyze(text: String) {
        Log.d("[ analyze ]", "API URL -> ${apiURL}?text=${text}")
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("${apiURL}?text=${text}")
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(
                    "[ Main ] onFailure",
                    e.message ?: getString(R.string.err_msg_found)
                )
                this@MainActivity.runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.err_msg_try_again),
                        Toast.LENGTH_SHORT
                    ).show()
                    inputText.isEnabled = true
                    progressDialog.dismiss()
                }
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                response.body()?.string().let {
                    this@MainActivity.runOnUiThread {
                        val nlu = Gson().fromJson(
                            it,
                            NaturalLanguageUnderstandingModel::class.java
                        )
                        modelList.add(
                            0,
                            NaturalLanguageUnderstandingModel(
                                text,
                                nlu.emotion ?: Emotion(
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
                                nlu.language ?: "en",
                                nlu.sentiment ?: Sentiment(SentimentDocument("neutral", 0.0)),
                                Date()
                            )
                        )
                        if (!recyclerView.isVisible) {
                            recyclerView.visibility = View.VISIBLE
                            textInstruction.visibility = View.GONE
                        }
                        viewAdapter.notifyDataSetChanged()
                        inputText.isEnabled = true
                        progressDialog.dismiss()
                    }
                }


            }
        })
    }
}
