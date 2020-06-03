package com.example.Dublin_MyMaps

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.Dublin_MyMaps.models.BikeStation
import com.example.Dublin_MyMaps.models.Stations
import com.example.william20268.Adapters.myAdapter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException

class RecyclerActivity: AppCompatActivity(), OnMapReadyCallback {

    private lateinit var bikesStations:ArrayList<BikeStation>
    private lateinit var myadapter: myAdapter
    private lateinit var mMap: GoogleMap



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //recyclerView.adapter = (bikesStations, this)
    }


/*
        myadapter = myAdapter( bikesStations)
        my_recycler.layoutManager = LinearLayoutManager(this)
        my_recycler.addItemDecoration(DividerItemDecoration(this, OrientationHelper.VERTICAL))
        my_recycler.adapter = myadapter
    }
*/

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
                myadapter.notifyDataSetChanged()
                Log.i(getString(R.string.DEBUG_MAINACTIVITY), "JSON HTTP CALL SUCCEEDED")

                val body = response?.body?.string()
                //  println("json loading" + body)
                Log.i(getString(R.string.DEBUG_MAINACTIVITY), body)
                var jsonBody = "{\"stations\": " + body + "}"

                val gson = GsonBuilder().create()
                var  bikesStations = gson.fromJson(jsonBody, Stations::class.java).stations
/*
                myadapter = myAdapter( bikesStations)
                rv_active.layoutManager = LinearLayoutManager(this)
                rv_active.addItemDecoration(DividerItemDecoration(this, OrientationHelper.VERTICAL))
                rv_active.adapter = myadapter
                */

                myadapter.notifyDataSetChanged()


                renderMarkers()
            }


        })


    }

    fun renderMarkers() {

        runOnUiThread {

            bikesStations.forEach {
                val position = LatLng(it.position.lat, it.position.lng)
                var marker = mMap.addMarker(
                    MarkerOptions().position(position).title("Marker in ${it.address}")
                )
                marker.setTag(it.number)
                Log.i(
                    getString(R.string.DEBUG_MAINACTIVITY),
                    "${it.address} : ${it.position.lat} : ${it.position.lng}"
                )
            }


            val centreLocation = LatLng(53.349562, -6.278198)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centreLocation, 16.0f))

        }
    }

    // Add a marker in Sydney and move the camera

    override fun onMapReady(googleMap: GoogleMap) {

        Log.i(getString(R.string.DEBUG_MAINACTIVITY), "renderMarkers called")


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
                "Marker id (tag) is " + marker.getTag().toString()
            )
            Log.i(getString(R.string.DEBUG_MAINACTIVITY), "Marker address is  " + marker.title)

            true
        }

    }
}

