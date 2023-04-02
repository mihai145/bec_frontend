package com.example.bec_client

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bec_client.adapter.MovieRecyclerAdapter
import com.example.restapi.home.viewmodel.SearchViewModel


/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {
    private lateinit var searchViewModel:SearchViewModel
    private lateinit var movieAdapter: MovieRecyclerAdapter

    private fun search(query: String) {
        searchViewModel.searchMoviesByName(query)
        searchViewModel.searchedMoviesLiveData?.observe(this, Observer {
            if (it!=null){
                movieAdapter.submitList(it.results)
                Log.d("Debug",it.toString())
            }else{
                Log.d("DEBUG: ", "a crapat")
            }
        })
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchViewModel = ViewModelProvider(this)[SearchViewModel::class.java]
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mView : View = inflater.inflate(R.layout.fragment_search, container,false)
        val searchView : SearchView = mView.findViewById(R.id.SearchView);
        val recyclerView : RecyclerView = mView.findViewById(R.id.recycler_view)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL ,false)
            movieAdapter = MovieRecyclerAdapter()
            adapter = movieAdapter
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(query == null)
                    return false;
                Log.d("DEBUG: ", query)
                search(query);
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        return mView
    }
}