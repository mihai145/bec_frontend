package com.example.bec_client.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.bec_client.R
import com.example.bec_client.adapter.RecyclerAdapter
import com.example.restapi.home.data.*
import com.example.restapi.home.data.model.CardModel
import com.example.restapi.home.data.model.GenreModel
import com.example.restapi.home.viewmodel.TrendingViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [TrendingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TrendingFragment : Fragment() {
    private lateinit var trendingViewModel: TrendingViewModel
    private lateinit var recyclerAdapter: RecyclerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        trendingViewModel = ViewModelProvider(this).get(TrendingViewModel::class.java)
    }

    private fun doTheTrending(genreId: Int) {
        recyclerAdapter.resetAdapter()
        trendingViewModel.getTrending(genreId)
        trendingViewModel.trendingMoviesLiveData?.observe(this, Observer {
            if (it != null) {
                it.results?.let { it1 -> recyclerAdapter.submitList(it1.map { x -> CardModel(x) }) }
                Log.d("Debug", it.toString())
            } else {
                Log.d("DEBUG: ", "a crapat")
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_trending, container, false)
        val recyclerView =
            view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.trendingRecyclerView)
        recyclerAdapter = RecyclerAdapter()
        recyclerView.adapter = recyclerAdapter
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)

        var genreList = listOf<GenreModel>()
        trendingViewModel.getGenres()
        trendingViewModel.genresLiveData?.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                genreList = it.results!!

                var genreDict = mutableMapOf<String, Long>()
                for (genre in genreList) {
                    genreDict[genre.name] = genre.id
                }

                Log.d("Debug", genreList.toString())
                val genreSpinner = view.findViewById<Spinner>(R.id.genreSpinner)
                val genreAdapter = ArrayAdapter<String>(
                    requireContext(),
                    android.R.layout.simple_spinner_item,
                    genreList.map { x -> x.name })

                genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                genreSpinner.adapter = genreAdapter

                genreSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View,
                        position: Int,
                        id: Long
                    ) {

                        val genreId = genreDict[parent.getItemAtPosition(position).toString()]
                        doTheTrending(genreId!!.toInt())
                    }

                    override fun onNothingSelected(parent: AdapterView<*>) {
                        // Code to perform some action when nothing is selected
                    }
                }

            } else {
                Log.d("DEBUG: ", "a crapat")
            }
        })

        return view
    }
}