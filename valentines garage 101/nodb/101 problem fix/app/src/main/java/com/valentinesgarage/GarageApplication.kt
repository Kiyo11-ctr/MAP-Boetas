package com.valentinesgarage

import android.app.Application
import com.valentinesgarage.di.DatabaseSeeder
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class GarageApplication : Application() {

    @Inject lateinit var seeder: DatabaseSeeder

    override fun onCreate() {
        super.onCreate()
        seeder.seedIfEmpty()
    }
}
