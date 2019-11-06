package com.victorshinya.buweets

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.util.*

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Timer().schedule(object : TimerTask() {
            override fun run() {
                val newIntent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                startActivity(newIntent)
                finish()
            }
        }, 3000)
    }
}
