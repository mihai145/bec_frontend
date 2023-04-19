package com.example.bec_client.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bec_client.MainActivity
import com.example.bec_client.R
import com.example.bec_client.adapter.RecyclerAdapter
import com.example.restapi.home.data.model.CardModel
import com.example.restapi.home.viewmodel.SearchViewModel
import java.net.URL
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var recyclerAdapter: RecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchViewModel = ViewModelProvider(this)[SearchViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mView: View = inflater.inflate(R.layout.fragment_home, container, false)
        val btnApi: Button = mView.findViewById(R.id.btnApiCall)

        btnApi.setOnClickListener {
            val executor = Executors.newSingleThreadExecutor()
            var res: String = "Eroare"
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

        val recyclerView: RecyclerView = mView.findViewById(R.id.recycler_view)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL, false
            )
            recyclerAdapter = RecyclerAdapter()
            adapter = recyclerAdapter
        }

        feedData()
        searchViewModel.postsLiveData?.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                recyclerAdapter.submitList(it.posts.map { x -> CardModel(x) })
                Log.d("DebugPosts", it.toString())
            } else {
                Log.d("DEBUG POSTS:", "a crapat")
            }
        })

        return mView
    }

    override fun onResume() {
        feedData()
        super.onResume()
    }

    fun feedData() {
        // populate recycler
        if (MainActivity.cachedCredentials != null) {
            recyclerAdapter.resetAdapter()
            searchViewModel.getPosts()
        }
    }
}