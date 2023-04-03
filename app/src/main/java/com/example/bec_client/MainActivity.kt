package com.example.bec_client

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.auth0.android.result.UserProfile

class MainActivity : AppCompatActivity() {
    public lateinit var account: Auth0
    public var cachedCredentials: Credentials? = null
    public var cachedUserProfile: UserProfile? = null
    private val profileFragment = ProfileFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set up the account object with the Auth0 application details
        account = Auth0(
            "eYKlPaL9GKJSMVNEi59E6l18BnRB8ICk",
            "dev-jc1flmgwmyky8n0k.us.auth0.com"
        )

        // profile fragment is a data member
        val homeFragment = HomeFragment()
        val searchFragment = SearchFragment()
        val trendingFragment = TrendingFragment()
        val nearbyFragment = NearbyFragment()

        setCurrentFragment(homeFragment)
        val bottomBar = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomBar.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.home -> setCurrentFragment(homeFragment)
                R.id.trending -> setCurrentFragment(trendingFragment)
                R.id.search -> setCurrentFragment(searchFragment)
                R.id.profile -> setCurrentFragment(profileFragment)
                R.id.nearby -> setCurrentFragment(nearbyFragment)
            }
            true
        }
    }

    public fun loginWithBrowser() {
        // Setup the WebAuthProvider, using the custom scheme and scope.

        WebAuthProvider.login(account)
            .withScheme("demo")
            .withScope("openid profile email")
            // Launch the authentication passing the callback where the results will be received
            .start(this, object : Callback<Credentials, AuthenticationException> {
                // Called when there is an authentication failure
                override fun onFailure(exception: AuthenticationException) {
                    // Something went wrong!
                }

                // Called when authentication completed successfully
                override fun onSuccess(credentials: Credentials) {
                    // Get the access token from the credentials object.
                    // This can be used to call APIs
                    cachedCredentials = credentials

                    Log.d("ID TOKEN", credentials.idToken)
                    showUserProfile(credentials.accessToken)
                }
            })
    }

    public fun logout() {
        WebAuthProvider.logout(account)
            .withScheme("demo")
            .start(this, object: Callback<Void?, AuthenticationException> {
                override fun onSuccess(payload: Void?) {
                    // The user has been logged out!
                    cachedUserProfile = null
                    cachedCredentials = null
                }

                override fun onFailure(error: AuthenticationException) {
                    // Something went wrong!
                }
            })
    }

    public fun showUserProfile(accessToken: String) {
        val client = AuthenticationAPIClient(account)

        // With the access token, call `userInfo` and get the profile from Auth0.
        client.userInfo(accessToken)
            .start(object : Callback<UserProfile, AuthenticationException> {
                override fun onFailure(error: AuthenticationException) {
                    // Something went wrong!
                }

                override fun onSuccess(result: UserProfile) {
                    // We have the user's profile!
                    cachedUserProfile = result

                    Log.d("PROFILE", result.email.toString())
                    Log.d("PROFILE", result.name.toString())

                    profileFragment.setupProfileFragment()
                }
            })
    }

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame, fragment)
            commit()
        }
    }
}