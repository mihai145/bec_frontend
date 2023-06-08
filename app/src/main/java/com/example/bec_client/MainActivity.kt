package com.example.bec_client

import NotificationUpdateListener
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.util.Base64
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import com.auth0.android.jwt.JWT
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.auth0.android.result.UserProfile
import com.auth0.jwt.algorithms.Algorithm
import com.example.bec_client.activity.PostActivity
import com.example.bec_client.fragment.*
import com.example.restapi.home.data.model.NotificationModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.security.KeyFactory
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.security.interfaces.RSAPublicKey
import java.security.spec.X509EncodedKeySpec
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var account: Auth0
    private val profileFragment = ProfileFragment()
    private val homeFragment = HomeFragment()
    private val searchFragment = SearchFragment()
    private val trendingFragment = TrendingFragment()
    private val nearbyFragment = NearbyFragment()

    private lateinit  var Listener: NotificationUpdateListener
    private lateinit var notifManger : NotificationManagerCompat
    val CHANNEL_ID = "channelID"
    val CHANNEL_NAME = "channelName"
    private fun createNotifChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT).apply {
                lightColor = Color.BLUE
                enableLights(true)
            }
            val manager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }


    public fun sendNotification(notificationId: Int,notif: String,postId: Int)
    {
        intent = Intent(this, PostActivity::class.java)
        intent.putExtra("id", postId.toLong())

        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }
        val notif = NotificationCompat.Builder(this,CHANNEL_ID)
            .apply { setContentIntent(resultPendingIntent) }
            .setSmallIcon(R.drawable.baseline_trending_up_24)
            .setContentTitle("New BEC notification")
            .setContentText(notif)
            .build()

        notifManger.notify(notificationId,notif)
    }

    companion object {
        var pemCertificate: String? = null
        var cachedCredentials: Credentials? = null
        var cachedUserProfile: UserProfile? = null
        var userId: Int? = null
        var isAdmin: Boolean = false
    }

    private fun downloadCertificate() {
        if (pemCertificate != null) return

        val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
            throwable.printStackTrace()
        }

        GlobalScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://dev-jc1flmgwmyky8n0k.us.auth0.com/pem")
                .build()
            val response = client.newCall(request).execute()
            val pem = response.body?.string()
            response.close()

            if (pem != null) {
                Log.d("INFO", "Certificate download succeeded")
                pemCertificate = pem
            } else {
                Log.d("ERR", "Certificate download failed")
            }
        }
    }

    private fun verifyJwtToken(token: String, certificateString: String) {
        // Parse certificate
        val certificateFactory = CertificateFactory.getInstance("X.509")
        val certificateInputStream = certificateString.byteInputStream()
        val certificate = certificateFactory.generateCertificate(certificateInputStream) as X509Certificate
        val publicKey = certificate.publicKey as RSAPublicKey

        // Verify JWT token
        val algorithm = Algorithm.RSA256(publicKey, null)
        val verifier = com.auth0.jwt.JWT.require(algorithm)
            .ignoreIssuedAt()
            .withIssuer("https://dev-jc1flmgwmyky8n0k.us.auth0.com/")
            .build()
        val decodedToken = verifier.verify(token)

        val role = decodedToken.getClaim("https://example.com/role").asString()
        Log.d("USER ROLE", role)
        if (role == "admin") {
            isAdmin = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Listener = NotificationUpdateListener(this)
        createNotifChannel()
        notifManger= NotificationManagerCompat.from(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set up the account object with the Auth0 application details
        account = Auth0(
            "eYKlPaL9GKJSMVNEi59E6l18BnRB8ICk",
            "dev-jc1flmgwmyky8n0k.us.auth0.com"
        )

        setCurrentFragment(homeFragment)
        val bottomBar = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> setCurrentFragment(homeFragment)
                R.id.trending -> setCurrentFragment(trendingFragment)
                R.id.search -> setCurrentFragment(searchFragment)
                R.id.profile -> setCurrentFragment(profileFragment)
                R.id.nearby -> setCurrentFragment(nearbyFragment)
            }
            true
        }

        downloadCertificate()
    }

    private fun getUserId(idToken: String): Int {
        val jwt = JWT(idToken)
        val id = jwt.subject?.removePrefix("auth0|")
        Log.d("Decoded ID", id.toString())
        return id!!.toInt()
    }

    fun loginWithBrowser() {
        // Setup the WebAuthProvider, using the custom scheme and scope.

        WebAuthProvider.login(account)
            .withScheme("demo")
            .withScope("openid profile email")
            // Launch the authentication passing the callback where the results will be received
            .start(this, object : Callback<Credentials, AuthenticationException> {
                // Called when there is an authentication failure
                override fun onFailure(exception: AuthenticationException) {
                    Listener.stopListening()
                    // Something went wrong!
                }

                // Called when authentication completed successfully
                override fun onSuccess(credentials: Credentials) {
                    // Get the access token from the credentials object.
                    // This can be used to call APIs
                    cachedCredentials = credentials
                    userId = getUserId(cachedCredentials!!.idToken)

                    Log.d("ID TOKEN", credentials.idToken)

                    verifyJwtToken(
                        cachedCredentials!!.idToken, pemCertificate.toString())

                    showUserProfile(credentials.accessToken)

                    Listener.userId = userId!!.toLong()
                    Listener.startListening(10)
                }
            })
    }

    fun logout() {
        Listener.stopListening()
        WebAuthProvider.logout(account)
            .withScheme("demo")
            .start(this, object : Callback<Void?, AuthenticationException> {
                override fun onSuccess(payload: Void?) {
                    // The user has been logged out!
                    cachedUserProfile = null
                    cachedCredentials = null
                    userId = null
                    isAdmin = false
                }

                override fun onFailure(error: AuthenticationException) {
                    // Something went wrong!
                }
            })
    }

    fun showUserProfile(accessToken: String) {
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