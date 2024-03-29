package com.example.fitme.di

import com.example.fitme.repo.MainRepository
import org.koin.core.module.Module
import org.koin.dsl.module

val repoModules : Module = module {
    single { MainRepository(get(), get()) }
}