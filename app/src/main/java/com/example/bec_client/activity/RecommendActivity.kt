package com.example.bec_client.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bec_client.R
import com.example.bec_client.adapter.RecyclerAdapter
import com.example.restapi.home.data.model.CardModel
import com.example.restapi.home.viewmodel.SearchViewModel
import java.util.stream.Collectors.toList

class RecommendActivity : AppCompatActivity() {
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var recyclerAdapter: RecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommend)
        searchViewModel = ViewModelProvider(this)[SearchViewModel::class.java]
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerAdapter = RecyclerAdapter()
        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
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