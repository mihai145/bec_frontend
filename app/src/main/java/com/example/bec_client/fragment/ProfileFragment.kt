package com.example.bec_client.fragment

import NotificationUpdateListener
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bec_client.MainActivity
import com.example.bec_client.R
import com.example.bec_client.adapter.LeaderboardAdapter
import com.example.bec_client.adapter.RecyclerAdapter
import com.example.restapi.home.data.model.CardModel
import com.example.restapi.home.viewmodel.SearchViewModel
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    private lateinit var searchViewModel: SearchViewModel
    private lateinit var recyclerAdapter: LeaderboardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchViewModel = ViewModelProvider(this)[SearchViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mView: View = inflater.inflate(R.layout.fragment_profile, container, false)
        val recyclerView: RecyclerView = mView.findViewById(R.id.recyclerView)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL, false
            )
            recyclerAdapter = LeaderboardAdapter()
            adapter = recyclerAdapter
        }

        return mView
    }

    fun feedData() {
        // populate recycler
        Log.d("FeedData", "FeedData")
        recyclerAdapter.resetAdapter()
        searchViewModel.getLeaderboard()
        searchViewModel.leaderboardLiveData?.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                recyclerAdapter.submitList(it.users.map { x -> CardModel(x) })
                Log.d("DebugLeaderboard", it.toString())
            } else {
                Log.d("DEBUG POSTS:", "a crapat")
            }
        })
    }

    fun setupProfileFragment() {
        val profileInfo: TextView? = view?.findViewById(R.id.profileTextView)
        val button: Button? = view?.findViewById(R.id.loginLogoutButton)

        val mainActivity = activity as? MainActivity

        if (MainActivity.cachedCredentials == null) {
            profileInfo?.text = "You are not logged in"
            button?.text = "LOGIN"
            button?.setOnClickListener {
                if (mainActivity != null) {
                    mainActivity.loginWithBrowser()
                }
            }

        } else {
            profileInfo?.text = "Hello " + MainActivity.cachedUserProfile?.name
            button?.text = "LOGOUT"
            button?.setOnClickListener {
                if (mainActivity != null) {
                    mainActivity.logout()
                }
            }

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupProfileFragment()

    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        feedData()
        setupProfileFragment()
    }
}