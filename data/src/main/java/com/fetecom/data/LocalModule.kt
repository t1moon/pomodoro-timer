package com.fetecom.data

import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val localModule = module {

    single {
        Room.databaseBuilder(androidContext(), TaskDatabase::class.java, "tasks")
            .fallbackToDestructiveMigration()
            .build()
    }


    single {
        get<TaskDatabase>().taskDao()
    }
}