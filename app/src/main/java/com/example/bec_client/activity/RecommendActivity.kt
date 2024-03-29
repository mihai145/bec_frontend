package com.example.bec_client.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bec_client.R
import com.example.bec_client.adapter.RecyclerAdapter
import com.example.bec_client.manager.PreferencesManager
import com.example.restapi.home.data.model.CardModel
import com.example.restapi.home.viewmodel.GptViewModel
import com.example.restapi.home.viewmodel.SearchViewModel
import java.util.stream.Collectors.toList

class RecommendActivity : AppCompatActivity() {
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var gptViewModel: GptViewModel
    private lateinit var recyclerAdapter: RecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommend)
        val progressBar: ProgressBar = findViewById(R.id.progressBar)
        val loadingText : TextView = findViewById(R.id.textLoading)
        searchViewModel = ViewModelProvider(this)[SearchViewModel::class.java]
        gptViewModel = ViewModelProvider(this)[GptViewModel::class.java]
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerAdapter = RecyclerAdapter()
        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)

        Log.d("Preferences",PreferencesManager.getPreferences().toString())
        gptViewModel.askGpt(PreferencesManager.getPreferences().toList())
        gptViewModel.preferencesLiveData?.observe(this, Observer {
            if (it != null) {
                progressBar.visibility = View.GONE
                loadingText.visibility = View.GONE
                it.results.forEach{x -> loadMovie(x)}
                Log.d("Debug", it.toString())
            } else {
                Log.d("DEBUG: ", "a crapat")
            }
        })
    }

    private fun loadMovie(name: String)
    {
        searchViewModel.searchMoviesByName(name);
        searchViewModel.searchedMoviesLiveData?.observe(this, Observer {
            if (it != null) {
                recyclerAdapter.submitList(it.results.stream().limit(1).map { x -> CardModel(x) }.collect(toList()))
                Log.d("Debug", it.toString())
            } else {
                Log.d("DEBUG: ", "a crapat")
            }
        })
    }
}