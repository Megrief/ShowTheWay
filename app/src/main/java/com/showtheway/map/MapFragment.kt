package com.showtheway.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.showtheway.BuildConfig
import com.showtheway.databinding.FragmentMapBinding
import com.showtheway.UiState
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.geometry.Geometry
import com.yandex.mapkit.map.Map
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MapFragment : Fragment() {


    private var _binding: FragmentMapBinding? = null
    private val binding: FragmentMapBinding
        get() = _binding!!

    private val viewModel: MapViewModel by lazy {
        ViewModelProvider(requireActivity())[MapViewModel::class.java]
    }

    private val map: Map by lazy {
        binding.root.mapWindow.map
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)  {
        super.onViewCreated(view, savedInstanceState)
        observeState()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        super.onStop()
        MapKitFactory.getInstance().onStop()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collectLatest { state ->
                    handleState(state)
                }
            }
        }
    }

    private fun handleState(state: UiState) {
        when (state) {
            is UiState.Init -> { }
            is UiState.Success<*> -> {
               (state.data as? DrivingRoute)?.geometry?.let { polyline ->
                    map.mapObjects.addPolyline(polyline)
                    val geometry = Geometry.fromPolyline(polyline)
                    val position = map.cameraPosition(geometry)
                    map.move(position)
               }
            }
            is UiState.Message -> {
                val bundle = bundleOf(BuildConfig.BUNDLE_KEY to state.message)
                parentFragmentManager.setFragmentResult(BuildConfig.RESULT_KEY, bundle)
                parentFragmentManager.popBackStack()
            }
        }
    }
}