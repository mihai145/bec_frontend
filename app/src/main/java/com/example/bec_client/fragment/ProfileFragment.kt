package com.example.bec_client.fragment

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
    private lateinit var recyclerAdapter: RecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchViewModel = ViewModelProvider(this)[SearchViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mView: View = inflater.inflate(R.layout.fragment_profile, container, false)
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
        searchViewModel.leaderboardLiveData?.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                recyclerAdapter.submitList(it.users.map { x -> CardModel(x) })
                Log.d("DebugLeaderboard", it.toString())
            } else {
                Log.d("DEBUG POSTS:", "a crapat")
            }
        })

        return mView
    }

    fun feedData() {
        // populate recycler
        recyclerAdapter.resetAdapter()
        searchViewModel.getLeaderboard()
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

        // this will have to change later... we will add the id token to all requests to the backend
        val amIAuthenticatedButton: Button? =
            getView()?.findViewById(R.id.amIAuthenticatedButton)
        amIAuthenticatedButton?.setOnClickListener {
            val executor = Executors.newSingleThreadExecutor()
            executor.execute {
                try {
                    val myURL = URL("https://teambec.live/amILoggedIn")
                    val myURLConnection: HttpURLConnection =
                        myURL.openConnection() as HttpURLConnection

                    var bearer = "null"
                    if (MainActivity.cachedCredentials != null) {
                        bearer = MainActivity.cachedCredentials?.idToken.toString()
                    }

                    Log.d("BEARER", bearer)
                    myURLConnection.setRequestProperty("bearer", bearer)
                    myURLConnection.requestMethod = "GET"
                    myURLConnection.useCaches = false
                    myURLConnection.doInput = true
                    myURLConnection.doOutput = false

                    val res = myURLConnection.inputStream.bufferedReader().readText()

                    (activity as MainActivity).runOnUiThread {
                        Toast.makeText(activity, res, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: java.lang.Exception) {
                    /// >=400 status codes are FileNotFound exceptions...
                    (activity as MainActivity).runOnUiThread {
                        Toast.makeText(activity, "Not authenticated", Toast.LENGTH_SHORT).show()
                    }
                    Log.d("DEBUG: ", e.toString())
                }
            }
            executor.shutdown()
            executor.awaitTermination(1, TimeUnit.SECONDS)
        }
    }

    override fun onStart() {
        super.onStart()
        feedData()
        setupProfileFragment()
    }

    override fun onResume() {
        super.onResume()
        feedData()
        setupProfileFragment()
    }
}