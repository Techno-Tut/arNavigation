package com.example.myapplication

import com.mapbox.vision.mobile.core.interfaces.VisionEventsListener
import com.mapbox.vision.mobile.core.models.AuthorizationStatus
import com.mapbox.vision.mobile.core.models.Camera
import com.mapbox.vision.mobile.core.models.Country
import com.mapbox.vision.mobile.core.models.FrameSegmentation
import com.mapbox.vision.mobile.core.models.classification.FrameSignClassifications
import com.mapbox.vision.mobile.core.models.detection.FrameDetections
import com.mapbox.vision.mobile.core.models.position.VehicleState
import com.mapbox.vision.mobile.core.models.road.RoadDescription
import com.mapbox.vision.mobile.core.models.world.WorldDescription

class visionLisnter : VisionEventsListener {
    override fun onAuthorizationStatusUpdated(authorizationStatus: AuthorizationStatus) {
        super.onAuthorizationStatusUpdated(authorizationStatus)
    }

    override fun onCameraUpdated(camera: Camera) {
        super.onCameraUpdated(camera)
    }

    override fun onCountryUpdated(country: Country) {
        super.onCountryUpdated(country)
    }

    override fun onFrameDetectionsUpdated(frameDetections: FrameDetections) {
        super.onFrameDetectionsUpdated(frameDetections)
    }

    override fun onFrameSegmentationUpdated(frameSegmentation: FrameSegmentation) {
        super.onFrameSegmentationUpdated(frameSegmentation)
    }

    override fun onFrameSignClassificationsUpdated(frameSignClassifications: FrameSignClassifications) {
        super.onFrameSignClassificationsUpdated(frameSignClassifications)
    }

    override fun onRoadDescriptionUpdated(roadDescription: RoadDescription) {
        super.onRoadDescriptionUpdated(roadDescription)
    }

    override fun onUpdateCompleted() {
        super.onUpdateCompleted()
    }

    override fun onVehicleStateUpdated(vehicleState: VehicleState) {
        super.onVehicleStateUpdated(vehicleState)
    }

    override fun onWorldDescriptionUpdated(worldDescription: WorldDescription) {
        super.onWorldDescriptionUpdated(worldDescription)
    }
}