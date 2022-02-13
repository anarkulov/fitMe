package com.example.fitme.di

import com.example.fitme.core.network.networkModule
import com.example.fitme.data.local.prefModule

val koinModules = listOf(
    prefModule,
    networkModule,
    repoModules,
    viewModules
)