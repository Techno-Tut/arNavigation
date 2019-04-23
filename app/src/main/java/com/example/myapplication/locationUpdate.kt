package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import com.mapbox.api.directions.v5.models.DirectionsRoute

class locationUpdate(context: Context) {
    val mContext = context


    private lateinit var locationManager: LocationManager
    private var hasNetwork = false
    private var hasGps = false
    private var gpsLocation: Location? = null
    private var networkLocation: Location? = null

    var routes: DirectionsRoute? = null

    var accurateLocation: Location? = null

    init {
        getLocation()
    }

    private fun getLocation() {
        locationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (hasNetwork || hasGps) {
            if (hasGps) {
                gpsLocation()
            }

            if (hasNetwork) {
                networkLocation()
            }

            if (gpsLocation != null && networkLocation != null) {
                if (gpsLocation!!.accuracy > networkLocation!!.accuracy) {
                    accurateLocation = networkLocation
                } else {
                    accurateLocation = gpsLocation
                }
            }

        } else {
            //place where gps is not enabled
        }
    }

    @SuppressLint("MissingPermission")
    private fun gpsLocation() {
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            5000,
            0F,
            object : LocationListener {
                override fun onLocationChanged(location: Location?) {
                    if (location != null)
                        gpsLocation = location
                    accurateLocation = gpsLocation
                }

                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

                }

                override fun onProviderEnabled(provider: String?) {

                }

                override fun onProviderDisabled(provider: String?) {

                }
            })

        val localGpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

        if (localGpsLocation != null)
            gpsLocation = localGpsLocation
        accurateLocation = gpsLocation
    }

    @SuppressLint("MissingPermission")
    private fun networkLocation() {
        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            5000,
            0F,
            object : LocationListener {
                override fun onLocationChanged(location: Location?) {
                    if (location != null)
                        networkLocation = location
                    accurateLocation = networkLocation
                }

                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

                }

                override fun onProviderEnabled(provider: String?) {

                }

                override fun onProviderDisabled(provider: String?) {

                }
            })
        val localNetworkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        if (localNetworkLocation != null)
            networkLocation = localNetworkLocation
        accurateLocation = networkLocation
    }
}