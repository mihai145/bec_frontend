package com.example.bec_client

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import java.net.URL
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mView : View = inflater.inflate(R.layout.fragment_home, container,false)
        val btnApi : Button = mView.findViewById(R.id.btnApiCall)

        btnApi.setOnClickListener {
            val executor = Executors.newSingleThreadExecutor()
            var res : String = "Eroare"
            executor.execute {
                try {
                    res = URL("https://teambec.live").readText()
                    Log.d("DEBUG: ", res)
                } catch (e: java.lang.Exception) {
                    Log.d("DEBUG: ", e.toString())
                }
            }
            executor.shutdown()
            executor.awaitTermination(1, TimeUnit.SECONDS)
            val textView = mView.findViewById<TextView>(R.id.TextViewHome)
            textView.text = res
        }
        return mView
    }
}