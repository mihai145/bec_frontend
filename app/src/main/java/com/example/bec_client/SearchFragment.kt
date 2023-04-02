package com.example.bec_client

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.restapi.home.viewmodel.SearchViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {
    private lateinit var searchViewModel:SearchViewModel
    private fun search(query: String) {
        searchViewModel.searchMoviesByName(query)
        searchViewModel.searchedMoviesLiveData?.observe(this, Observer {
            if (it!=null){
                it.results.forEach{x -> Log.d("Debug",x.toString())}

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