package com.example.myapplication

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Point
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute
import com.mapbox.vision.VisionManager
import com.mapbox.vision.ar.VisionArManager
import com.mapbox.vision.ar.core.models.Route
import com.mapbox.vision.ar.core.models.RoutePoint
import com.mapbox.vision.mobile.core.interfaces.VisionEventsListener
import com.mapbox.vision.mobile.core.models.position.GeoCoordinate
import com.mapbox.vision.performance.ModelPerformance
import com.mapbox.vision.performance.ModelPerformanceConfig
import com.mapbox.vision.performance.ModelPerformanceMode
import com.mapbox.vision.performance.ModelPerformanceRate
import kotlinx.android.synthetic.main.activity_navigation.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class navigation : AppCompatActivity() {

    //origin and destination
    lateinit var mapboxNavigation: MapboxNavigation
    lateinit var directionRoute: DirectionsRoute
    //  lateinit var routeFetcher: RouteFetcher
    lateinit var locationObject: locationUpdate
    lateinit var origin: Point
    lateinit var destination: Point
    lateinit var arLocationEngine: LocationEngine
    val acess_token = "sk.eyJ1IjoidGVjaG5vdHV0IiwiYSI6ImNqdXFwbmpoOTBrMDE0MG8wbmhja3ZobjIifQ.PqLwMAjpoKNJ6mwaZd8brA"

    var visionListner: visionLisnter = visionLisnter()
    lateinit var route: Route

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
        val vision = VisionManager.init(
            application, acess_token
        )

        locationObject = locationUpdate(this)

        //mpabox Navigation
        mapboxNavigation = MapboxNavigation(
            this, "pk.eyJ1IjoidGVjaG5vdHV0IiwiYSI6ImNqdW1yMTdhYjA4cnQ0ZXBwcXFqOWl2YzUifQ.k4Kew7sdjpthA1Wd2BDW2A"
        )


    }

    override fun onResume() {
        super.onResume()

        getRoute()
        VisionManager.create()
        VisionManager.start(visionListner)
        VisionManager.setModelPerformanceConfig(
            ModelPerformanceConfig.Merged(ModelPerformance.On(ModelPerformanceMode.FIXED, ModelPerformanceRate.LOW))
        )
        VisionManager.start(object : VisionEventsListener {})
        VisionManager.setVideoSourceListener(vision_ar_view)
        VisionArManager.create(VisionManager, vision_ar_view)
    }


    override fun onPause() {
        super.onPause()
        VisionManager.destroy()
        VisionManager.stop()
        VisionManager.destroy()
        mapboxNavigation.stopNavigation()
    }

    fun getRoute() {
        origin = Point.fromLngLat(73.079, 19.0387)
        destination = Point.fromLngLat(73.040607, 19.023230)

        NavigationRoute.builder(applicationContext)
            .origin(origin)
            .destination(destination)
            .accessToken(getString(R.string.acess_token))
            .build()
            .getRoute(object : Callback<DirectionsResponse> {
                override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                    Log.i("unable to reach", "mapbox")
                }

                override fun onResponse(call: Call<DirectionsResponse>, response: Response<DirectionsResponse>) {
                    if (response.body() == null && response.body()!!.routes().isEmpty()) {
                        return
                    }
                    directionRoute = response.body()!!.routes()[0]
                    mapboxNavigation.startNavigation(directionRoute)
                    VisionArManager.setRoute(
                        Route(
                            directionRoute.getRoutePoints(),
                            directionRoute.duration()?.toFloat() ?: 0f,
                            "",
                            ""
                        )
                    )
                }
            })
    }


    private fun DirectionsRoute.getRoutePoints(): Array<RoutePoint> {
        val routePoints = arrayListOf<RoutePoint>()
        legs()?.forEach { it ->
            it.steps()?.forEach { step ->
                val maneuverPoint = RoutePoint(
                    GeoCoordinate(
                        latitude = step.maneuver().location().latitude(),
                        longitude = step.maneuver().location().longitude()
                    )
                )
                routePoints.add(maneuverPoint)

                step.intersections()
                    ?.map {
                        RoutePoint(
                            GeoCoordinate(
                                latitude = step.maneuver().location().latitude(),
                                longitude = step.maneuver().location().longitude()
                            )
                        )
                    }
                    ?.let { stepPoints ->
                        routePoints.addAll(stepPoints)
                    }
            }
        }

        return routePoints.toTypedArray()
    }
}



