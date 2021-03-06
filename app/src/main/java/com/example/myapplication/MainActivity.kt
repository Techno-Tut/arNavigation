package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.os.PersistableBundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.View
import android.widget.Button
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity(), OnMapReadyCallback {



    private lateinit var mapview: MapView
    private lateinit var map: MapboxMap
    private lateinit var locationObject: locationUpdate

    private var navigationMapRoute: NavigationMapRoute? = null
    private lateinit var origin: Point
    private lateinit var destination: Point

    //main on create class
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        //permissionshandler
        managePermissions()

        //init and validating mapbox
        Mapbox.getInstance(
            this,
            "pk.eyJ1IjoidGVjaG5vdHV0IiwiYSI6ImNqdW1yMTdhYjA4cnQ0ZXBwcXFqOWl2YzUifQ.k4Kew7sdjpthA1Wd2BDW2A"
        )

        setContentView(R.layout.activity_main)

        //mapbox target - init - location object init
        mapview = findViewById(R.id.map)
        mapview.onCreate(savedInstanceState)
        locationObject = locationUpdate(this)
        mapview.getMapAsync(this)

        //button listners
        fab_user_location.setOnClickListener {
            setCameraView(locationObject.accurateLocation)
        }

        but_navigation.setOnClickListener {
            destination = Point.fromLngLat(editText_Longitude.text.toString().toDouble(), editText_Latitude.text.toString().toDouble())
            getRoute()
            findViewById<Button>(R.id.but_launch).visibility = View.VISIBLE
        }

        but_launch.setOnClickListener {
            startActivityForResult(Intent(this, navigation::class.java),1)
        }

    }

    // MapBox map functions
    override fun onMapReady(mapboxMap: MapboxMap) {
        try {
            map = mapboxMap
            setCameraView(locationObject.accurateLocation)
            map.setStyle(Style.MAPBOX_STREETS, Style.OnStyleLoaded {
                enableUserLocationView(it)
            })
            origin = Point.fromLngLat(
                locationObject.accurateLocation!!.longitude,
                locationObject.accurateLocation!!.latitude
            )
        } catch (e:Exception) {
            Log.i("app","enable location services")
            enableLocation();

        }
    }

    /*     Mmodular Functions */
    private fun setCameraView(location: Location?) {
        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(location!!.latitude, location.longitude), 16.0
            )
        )
    }

    fun enableUserLocationView(style: Style) {
        val customLocationComponentOptions = LocationComponentOptions.builder(applicationContext).build()

        val locationComponentActivationOptions = LocationComponentActivationOptions.builder(this, style)
            .locationComponentOptions(customLocationComponentOptions)
            .build()

        map.locationComponent.apply {
            activateLocationComponent(locationComponentActivationOptions)
            isLocationComponentEnabled = true
            cameraMode = CameraMode.TRACKING
            renderMode = RenderMode.NORMAL
        }
    }

    fun getRoute() {
        NavigationRoute.builder(applicationContext)
            .accessToken("pk.eyJ1IjoidGVjaG5vdHV0IiwiYSI6ImNqdW1yMTdhYjA4cnQ0ZXBwcXFqOWl2YzUifQ.k4Kew7sdjpthA1Wd2BDW2A")
            .origin(origin)
            .destination(destination)
            .build()
            .getRoute(object : Callback<DirectionsResponse> {
                override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                }

                override fun onResponse(call: Call<DirectionsResponse>, response: Response<DirectionsResponse>) {
                    var routeResponse = response
                    var body = routeResponse.body() ?: return
                    if (body.routes().count() == 0) {
                        return
                    }

                    if (navigationMapRoute != null) {
                        navigationMapRoute?.removeRoute()
                    } else {
                        navigationMapRoute = NavigationMapRoute(null, mapview, map)
                    }
                    navigationMapRoute?.addRoute(body.routes().first())

                }
            })
    }


    private fun managePermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.CAMERA
            ),
            1
        )
    }

    // lifecycle states
    override fun onStart() {
        super.onStart()
        mapview.onStart()
    }

    override fun onPause() {
        super.onPause()
        mapview.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapview.onResume()
    }

    override fun onStop() {
        super.onStop()
        mapview.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapview.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapview.onLowMemory()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
        if (outState != null) {
            mapview.onSaveInstanceState(outState)
        }
    }

    private fun enableLocation() {
      val request = LocationRequest.create().apply {
          interval = 1000
          fastestInterval = 5000
          priority = LocationRequest.PRIORITY_HIGH_ACCURACY
      }

        val builder = LocationSettingsRequest.Builder().addAllLocationRequests(mutableListOf(request))
        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        task.addOnFailureListener() {
            if(it is ResolvableApiException) {
                try {
                    it.startResolutionForResult(this@MainActivity,7000)
                } catch (e:Exception) {

                }
            }
        }
    }


}