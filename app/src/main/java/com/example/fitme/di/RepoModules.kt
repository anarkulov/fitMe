package com.example.fitme.di

import com.example.fitme.repo.MainRepository
import com.example.fitme.ui.activity.WorkoutRepository
import com.example.fitme.ui.alarm.AlarmRepository
import com.example.fitme.ui.auth.AuthRepository
import org.koin.core.module.Module
import org.koin.dsl.module

val repoModules : Module = module {
    single { MainRepository(get(), get(), get()) }
    single { AuthRepository(get(), get(), get()) }
    single { AlarmRepository(get()) }
    single { WorkoutRepository(get()) }
}