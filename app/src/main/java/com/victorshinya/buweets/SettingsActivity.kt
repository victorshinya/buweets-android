package com.victorshinya.buweets

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog

class SettingsActivity : AppCompatActivity() {

    /**
     * Global variables
     */

    private lateinit var inputAPI: EditText

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sharedPreferences =
            getSharedPreferences(Constants.SHARED_PREFERENCES_REF_KEY, Context.MODE_PRIVATE)

        inputAPI = findViewById(R.id.inputAPI)
        inputAPI.setText(sharedPreferences.getString(Constants.SHARED_PREFERENCES_API_URL_KEY, ""))
    }

    fun saveData(view: View) {
        val editor = sharedPreferences.edit()
        editor.putString(Constants.SHARED_PREFERENCES_API_URL_KEY, inputAPI.text.toString())
        editor.apply()

        val alertDialog = AlertDialog.Builder(this@SettingsActivity)
        alertDialog.setTitle("Sucesso")
        alertDialog.setMessage("A sua API URL foi salva com sucesso")
        alertDialog.setNeutralButton("Ok") { dialog, _ ->
            this@SettingsActivity.finish()
            dialog.dismiss()
        }
        alertDialog.show()
    }
}
