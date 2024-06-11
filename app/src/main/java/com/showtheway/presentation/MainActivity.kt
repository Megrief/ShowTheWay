package com.showtheway.presentation

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.showtheway.BuildConfig
import com.showtheway.databinding.ActivityMainBinding
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.map.MapObjectCollection
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        val inflater = LayoutInflater.from(this)
        ActivityMainBinding.inflate(inflater)
    }

    private val viewModel: MainViewModel by viewModels()

    private val permissionsHandler: PermissionsHandler = PermissionsHandler()

    private val mapKit : MapKit by lazy {
        MapKitFactory.getInstance()
    }
    private val routesCollection: MapObjectCollection by lazy {
        binding.mapView.mapWindow.map.mapObjects.addCollection()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey(BuildConfig.MAP_KIT_API_KEY)

        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        MapKitFactory.initialize(this)

        observeState()
        binding.showTheWayButton.setOnClickListener {
            lifecycleScope.launch {
                permissionsHandler.requestPermissions(this@MainActivity)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mapKit.onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        mapKit.onStop()
        binding.mapView.onStop()
        super.onStop()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PermissionsHandler.REQUEST_CODE) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED))
                viewModel.onRequestPermissionsResult(true)
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest { state ->
                    handleState(state)
                }
            }
        }
    }

    private fun handleState(state: UiState) {
        when (state) {
            is UiState.Success -> {
                updateVisibility(mapViewIsVisible = true)
                routesCollection.addPolyline(state.route.geometry)
                // navigate to mapFragment
            }
            is UiState.Init -> {
                updateVisibility(showTheWayButtonIsVisible = true)
                // show InitialFragment with single button
            }
            is UiState.Message -> {
                updateVisibility(showTheWayButtonIsVisible = true, messageIsVisible  = true)
                binding.negativeMessageView.setText(state.message)
                //
            }
        }
    }

    private fun updateVisibility(
        mapViewIsVisible: Boolean = false,
        messageIsVisible: Boolean = false,
        showTheWayButtonIsVisible: Boolean = false
    ) = with(binding) {
        mapView.isVisible  = mapViewIsVisible
        negativeMessageView.isVisible  = messageIsVisible
        showTheWayButton.isVisible  = showTheWayButtonIsVisible
    }
    // permission for access to location
    // check for internet connection

}