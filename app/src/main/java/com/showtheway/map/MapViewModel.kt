package com.showtheway.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.showtheway.R
import com.showtheway.UiState
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingRouterType
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.directions.driving.VehicleOptions
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.location.Location
import com.yandex.mapkit.location.LocationListener
import com.yandex.mapkit.location.LocationStatus
import com.yandex.runtime.Error
import com.yandex.runtime.network.NetworkError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {

    private val locationManager = MapKitFactory.getInstance().createLocationManager()
    private val drivingRouter = DirectionsFactory.getInstance().createDrivingRouter(DrivingRouterType.ONLINE)
    private var drivingSession: DrivingSession? = null
    private val endPoint = Point(LATITUDE, LONGITUDE)
    private var startPoint: Point? = null
        set(value) {
            value?.let {
                field = it
                buildRoute(it, endPoint)
            }
        }

    private var _state: MutableStateFlow<UiState> = MutableStateFlow(UiState.Init)
    val state: StateFlow<UiState> = _state

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

    private val drivingRouteListener = object : DrivingSession.DrivingRouteListener {
        override fun onDrivingRoutes(p0: MutableList<DrivingRoute>) {
            viewModelScope.launch {
                _state.emit(UiState.Success(p0.first()))
            }
            drivingSession?.cancel()
        }

        override fun onDrivingRoutesError(p0: Error) {
            viewModelScope.launch {
                val message = if (p0 is NetworkError) {
                    R.string.network_error_message
                } else {
                    R.string.unknown_error_message
                }

                _state.emit(UiState.Message(message))
            }
            drivingSession?.cancel()
        }
    }

    init {
        locationManager.requestSingleUpdate(locationListener)
    }

    private fun buildRoute(startPoint: Point, endPoint: Point)  {
        val points = buildList {
            add(RequestPoint(startPoint, RequestPointType.WAYPOINT, null, null))
            add(RequestPoint(endPoint, RequestPointType.WAYPOINT, null, null))
        }

        drivingSession = drivingRouter.requestRoutes(
            points,
            DrivingOptions().setRoutesCount(1),
            VehicleOptions(),
            drivingRouteListener
        )
    }

    companion object {
        private const val LATITUDE = 56.833742
        private const val LONGITUDE = 60.635716
    }
}