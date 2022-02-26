package com.example.fitme.di

import com.example.fitme.core.network.networkModule
import com.example.fitme.data.local.prefModule
import com.example.fitme.repo.databaseModule

val koinModules = listOf(
    prefModule,
    networkModule,
    repoModules,
    viewModules,
    databaseModule
)