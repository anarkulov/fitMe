package com.example.fitme.di

import com.example.fitme.ui.auth.AuthViewModel
import com.example.fitme.ui.home.HomeViewModel
import com.example.fitme.ui.main.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val viewModules: Module = module {
    viewModel { AuthViewModel(get()) }
    viewModel { MainViewModel() }
    viewModel { HomeViewModel(get()) }
}