package com.example.myapplication

import android.content.Context
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Routes(private val context: Context, private val origin: Point, private val destination: Point) {

    lateinit var routes: List<DirectionsRoute>

    init {
        getRoute()
    }

    private fun getRoute() {
        NavigationRoute.builder(context)
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

                    routes = body.routes()
                }
            })
    }
}