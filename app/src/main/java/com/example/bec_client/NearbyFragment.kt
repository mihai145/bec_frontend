package com.example.bec_client

import android.Manifest
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import java.net.HttpURLConnection
import java.net.URL
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*
import java.util.concurrent.Executors

/**
 * A simple [Fragment] subclass.
 * Use the [NearbyFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

// class with fields for each field in the response from places api

data class TheatersResponse(
    val results: MutableList<Theater>
)

data class Theater(
    val name: String,
    val vicinity: String,
    val geometry: Geometry
)
data class Geometry(
    val location: Location
)
data class Location(
    val lat: Double,
    val lng: Double
)

class NearbyFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_nearby, container, false)

        var theaters = TheatersResponse(mutableListOf())
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        val loc = fusedLocationClient.lastLocation
        loc.addOnSuccessListener {
            if (it != null) {
                // get list of movie theaters
                val executor = Executors.newSingleThreadExecutor()
                executor.execute{
                    try{
                        // get api key from config.properties
                        val resources= resources
                        val inputStream: InputStream = resources.openRawResource(R.raw.config)
                        val properties = Properties()
                        properties.load(inputStream)

                        val apiKey = properties.getProperty("GOOGLE_MAPS_API_KEY")
                        Log.d(TAG, "APIKEY: $apiKey")
                        val str = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?keyword=cinema&location=${it.latitude},${it.longitude}&radius=5000&key=${apiKey}"
                        val connection = URL(str).openConnection() as HttpURLConnection
                        connection.requestMethod = "GET"
                        connection.doOutput = true
                        connection.connectTimeout = 5000
                        connection.readTimeout = 5000
                        val responseCode = connection.responseCode
                        if(responseCode == HttpURLConnection.HTTP_OK) {
                            val br = BufferedReader(InputStreamReader(connection.inputStream))
                            var wholeResponse = ""
                            var line = br.readLine()
                            while (line != null) {
                                wholeResponse += line
                                line = br.readLine()
                            }
                            // parse response into list of movie theaters
                            val gson = Gson()
                            theaters = gson.fromJson(wholeResponse, TheatersResponse::class.java)
//                            Log.d(TAG, "Theaters: $theaters")
                        } else {
                            Log.d(TAG, "Error: $responseCode")
                        }
                    }
                    catch(e: Exception){
                        Log.d(TAG, "Error: $e")
                    }
                }
                executor.shutdown()
                executor.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS)
            }
        }
        // unused movie icon
        val supportMapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        supportMapFragment.getMapAsync {
            // center map on current location and add markers for each cinema
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                it.isMyLocationEnabled = true
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {

                        val latLng = LatLng(location.latitude, location.longitude)
                        it.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f))
                        Log.d(TAG, "Theaters: $theaters")
                        theaters.results.forEach{theater ->
                            val theaterLatLng = LatLng(theater.geometry.location.lat, theater.geometry.location.lng)
//                            Log.d(TAG, "Theater: $theater")
                            val marker = it.addMarker(MarkerOptions().position(theaterLatLng).title(theater.name).snippet(theater.vicinity))
                            marker?.tag = theater
                        }
                    }
                    else {
                        val text = "Location is probably disabled"
                        val duration = Toast.LENGTH_SHORT
                        val toast = Toast.makeText(context, text, duration)
                        toast.show()
                    }
                }
            } else {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }
        }

        // Inflate the layout for this fragment
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }
}