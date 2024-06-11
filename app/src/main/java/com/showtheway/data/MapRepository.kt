package com.showtheway.data

import android.accounts.NetworkErrorException
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingRouterType
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.directions.driving.DrivingSession.DrivingRouteListener
import com.yandex.mapkit.directions.driving.VehicleOptions
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.location.Location
import com.yandex.mapkit.location.LocationListener
import com.yandex.mapkit.location.LocationStatus
import com.yandex.runtime.Error
import com.yandex.runtime.network.NetworkError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow

private const val LATITUDE = 56.833742
private const val LONGITUDE = 60.635716

class MapRepository {
    private val locationManager = MapKitFactory.getInstance().createLocationManager()
    private val drivingRouter = DirectionsFactory.getInstance().createDrivingRouter(DrivingRouterType.ONLINE)
    private var drivingSession: DrivingSession? = null

    private val endPoint = Point(LATITUDE, LONGITUDE)
    private var startPoint: Point? = null
        set(value) {
            field = value
            buildRoute(value!!, endPoint)
        }

    var route: Flow<Result<DrivingRoute>> = emptyFlow()
        set(value) {
            field = value
            drivingSession?.cancel()
        }

    private val locationListener = object : LocationListener {
        override fun onLocationUpdated(p0: Location) {
            with(p0) {
                val latitude = position.latitude
                val longitude  = position.longitude
                startPoint = Point(latitude, longitude)
            }
        }

        override fun onLocationStatusUpdated(p0: LocationStatus) { }
    }

    private val drivingRouteListener = object : DrivingRouteListener {
        override fun onDrivingRoutes(p0: MutableList<DrivingRoute>) {
            route = flow {
                emit(Result.success(p0.first()))
            }
        }

        override fun onDrivingRoutesError(p0: Error) {
            route = flow {
                if (p0 is NetworkError) emit(Result.failure(NetworkErrorException()))
                else emit(Result.failure(UnknownError()))
            }
        }
    }

    fun updateRoute() {
        locationManager.requestSingleUpdate(locationListener)
    }

    private fun buildRoute(startPoint: Point, endPoint: Point)  {
        val points = buildList {
            add(
                RequestPoint(
                    startPoint,
                    RequestPointType.WAYPOINT,
                    null,
                    null
                )
            )
            add(
                RequestPoint(
                    endPoint,
                    RequestPointType.WAYPOINT,
                    null,
                    null
                )
            )
        }

        drivingSession = drivingRouter.requestRoutes(
            points,
            DrivingOptions().setRoutesCount(1),
            VehicleOptions(),
            drivingRouteListener
        )
    }
}