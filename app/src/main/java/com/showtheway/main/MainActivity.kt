package com.showtheway.main

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
import com.showtheway.UiState
import com.showtheway.databinding.ActivityMainBinding
import com.showtheway.map.MapFragment
import com.yandex.mapkit.MapKitFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        val inflater = LayoutInflater.from(this)
        ActivityMainBinding.inflate(inflater)
    }

    private val viewModel: MainViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        MapKitFactory.initialize(this)

        observeState()
        registerForFragmentResult()

        binding.showTheWayButton.setOnClickListener{
            onButtonClick()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MainViewModel.REQUEST_CODE) {
            lifecycleScope.launch {
                val permissionGranted = grantResults.contains(PackageManager.PERMISSION_GRANTED)
                viewModel.onPermissionRequest(permissionGranted, this@MainActivity)
            }
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
            is UiState.Success<*> -> {
                updateVisibility(mapContainerIsVisible = true)
                goToMap()
            }
            is UiState.Init -> {
                updateVisibility(showTheWayButtonIsVisible = true)
            }
            is UiState.Message -> {
                updateVisibility(showTheWayButtonIsVisible = true, messageIsVisible  = true)
                binding.negativeMessageView.setText(state.message)
            }
        }
    }

    private fun updateVisibility(
        messageIsVisible: Boolean = false,
        showTheWayButtonIsVisible: Boolean = false,
        mapContainerIsVisible: Boolean = false
    ) = with(binding) {
        negativeMessageView.isVisible = messageIsVisible
        showTheWayButton.isVisible = showTheWayButtonIsVisible
        mapContainer.isVisible = mapContainerIsVisible
    }

    private fun goToMap() = with(supportFragmentManager.beginTransaction()) {
        setReorderingAllowed(true)
        add(binding.mapContainer.id, MapFragment::class.java, null)
        commit()
    }

    private fun onButtonClick() {
        lifecycleScope.launch {
            if (viewModel.permissionGranted(this@MainActivity)) {
                viewModel.onPermissionRequest(true, this@MainActivity)
            } else {
                viewModel.requestPermissions(this@MainActivity)
            }
        }
    }

    private fun registerForFragmentResult() {
        supportFragmentManager.setFragmentResultListener(
            BuildConfig.RESULT_KEY,
            this@MainActivity
        ) { _, bundle ->
            val result = bundle.getInt(BuildConfig.BUNDLE_KEY)
            lifecycleScope.launch {
                viewModel.onFragmentResult(result)
            }
        }
    }
}