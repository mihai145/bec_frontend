package com.example.bec_client

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import java.net.URL
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn_api_call = findViewById(R.id.btnApiCall) as Button
        btn_api_call.setOnClickListener {
            val executor = Executors.newSingleThreadExecutor()
            executor.execute {
                try {
                    val res = URL("https://teambec.live").readText()
                    Log.d("DEBUG: ", res)
                    runOnUiThread {
                        Toast.makeText(this, res, Toast.LENGTH_LONG).show()
                    }
                } catch (e: java.lang.Exception) {
                    Log.d("DEBUG: ", e.toString())
                }
            }
        }
    }
}