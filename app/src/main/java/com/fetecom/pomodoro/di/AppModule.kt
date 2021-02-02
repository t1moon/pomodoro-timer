package com.fetecom.pomodoro.di

import com.fetecom.data.TasksRepositoryImpl
import com.fetecom.domain.TasksRepository
import com.fetecom.pomodoro.ui.main.TasksViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module


val appModule = module {

    single<TasksRepository> { TasksRepositoryImpl( get()) }

    viewModel { TasksViewModel(get()) }

}