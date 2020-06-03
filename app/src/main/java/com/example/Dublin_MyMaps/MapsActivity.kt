package com.example.Dublin_MyMaps

import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.Dublin_MyMaps.models.BikeStation
import com.example.Dublin_MyMaps.models.Position
import com.example.Dublin_MyMaps.models.Stations

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException


    class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var listOfBikeStations: List<BikeStation>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.maps_layout)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        getBikeStationJsonData()

    }


    fun getBikeStationJsonData() {

        Log.i(getString(R.string.DEBUG_MAINACTIVITY), "Loading JSON data")

        var url =
            "https://api.jcdecaux.com/vls/v1/stations?contract=dublin&apiKey=163597812dfb8e11bcdaa6297a730b46529a5bcd"

        Log.i(getString(R.string.DEBUG_MAINACTIVITY), url)

        //Create a request object

        val request = Request.Builder().url(url).build()

        //Create a client

        val client = OkHttpClient()


        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                //   TODO("Not yet implemented")
                Log.i(getString(R.string.DEBUG_MAINACTIVITY), "JSON HTTP CALL FAILED")
            }

            override fun onResponse(call: Call, response: Response) {
                // TODO("Not yet implemented")
                Log.i(getString(R.string.DEBUG_MAINACTIVITY), "JSON HTTP CALL SUCCEEDED")

                val body = response.body?.string()
                //  println("json loading" + body)
                Log.i(getString(R.string.DEBUG_MAINACTIVITY), body)
                var jsonBody = "{\"stations\": " + body + "}"

                val gson = GsonBuilder().create()
                listOfBikeStations = gson.fromJson(jsonBody, Stations::class.java).stations


                renderMarkers()

            }


        })


    }

    fun renderMarkers() {

        runOnUiThread {

            listOfBikeStations.forEach {
                val position = LatLng(it.position.lat, it.position.lng)
                var marker = mMap.addMarker(
                    MarkerOptions().position(position).title("Marker in ${it.address}")
                )
                marker.tag = it.address
                Log.i(
                    getString(R.string.DEBUG_MAINACTIVITY),
                    "${it.address} : ${it.position.lat} : ${it.position.lng}"
                )
            }

           // mMap.setOnMarkerClickListener(this)

            // setUpMap()
            val centreLocation = LatLng(53.349562, -6.278198)
            //val myPlace = LatLng(40.73, -73.99)  // this is New York
            mMap.addMarker(MarkerOptions().position(centreLocation).title("My Favorite City"))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(centreLocation))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(centreLocation, 12f))



           // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centreLocation, 16.0f))

        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        getBikeStationJsonData()

        mMap.setOnMarkerClickListener { marker ->
                 if (marker.isInfoWindowShown) {

                marker.hideInfoWindow()
            } else {

                marker.showInfoWindow()
            }


            Log.i(getString(R.string.DEBUG_MAINACTIVITY), "Marker is clicked")
            Log.i(
                getString(R.string.DEBUG_MAINACTIVITY),
                "Marker id (tag) is " + marker.tag.toString()
            )
            Log.i(getString(R.string.DEBUG_MAINACTIVITY), "Marker address is  " + marker.title)


            true
        }

  // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        /*
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1

        // 3
        private const val REQUEST_CHECK_SETTINGS = 2
    }
*/


        mMap.uiSettings.isZoomControlsEnabled = true


    }






    }




