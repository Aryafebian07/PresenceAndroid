package com.niveon.tugasakhir.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.lifecycle.ViewModelProvider
import com.niveon.tugasakhir.R
import com.niveon.tugasakhir.ui.viewmodel.SplashViewModel

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val viewModel = ViewModelProvider(this).get(SplashViewModel::class.java)

        viewModel.splashFinished.observe(this, { splashFinished ->
            if (splashFinished) {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                finish()
            }
        })

        Handler().postDelayed({
            viewModel.setSplashFinished(true)
        }, 2000) // Simulating a 2-second delay
    }
}