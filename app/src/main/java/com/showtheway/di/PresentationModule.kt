package com.showtheway.di

import com.showtheway.main.MainViewModel
import com.showtheway.map.MapViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {

    viewModel {
        MainViewModel(
            connectionChecker = get()
        )
    }

    viewModel {
        MapViewModel()
    }
}