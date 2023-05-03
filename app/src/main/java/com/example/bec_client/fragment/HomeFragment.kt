package com.example.bec_client.fragment


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bec_client.MainActivity
import com.example.bec_client.R
import com.example.bec_client.activity.PostFormActivity
import com.example.bec_client.adapter.RecyclerAdapter
import com.example.restapi.home.data.model.CardModel
import com.example.restapi.home.viewmodel.SearchViewModel


/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var recyclerAdapter: RecyclerAdapter

    ////
    private lateinit var reviewButton: Button
    ////

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchViewModel = ViewModelProvider(this)[SearchViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mView: View = inflater.inflate(R.layout.fragment_home, container, false)

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

        /////
        //val userId = if (MainActivity.userId == null) (-1) else MainActivity.userId!!
        reviewButton = mView.findViewById(R.id.review_button)
        reviewButton.setOnClickListener {
            if (reviewButton.text != "LOADING") {
                if (MainActivity.cachedCredentials == null) {
                    Toast.makeText(
                        mView.context,
                        "You need to be logged in to do that",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {
                    val intent = Intent(mView.context, PostFormActivity::class.java)
                    //intent.putExtra("authorId", userId)
                    //intent.putExtra("userId", userId)
                    intent.putExtra("newOrEdit", 0L)
                    intent.putExtra("movieId", -1L)
                    intent.putExtra("movieName", "Normal Post, does not reffer to any movie")
                    intent.putExtra("title", "")
                    intent.putExtra("content", "")
                    startActivity(intent)
                }
            }
        }
        /////

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