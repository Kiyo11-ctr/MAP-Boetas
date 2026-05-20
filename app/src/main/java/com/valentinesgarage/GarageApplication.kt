package com.valentinesgarage

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class — entry point for Hilt dependency injection.
 * Annotating with @HiltAndroidApp triggers Hilt's code generation
 * and sets up the application-level dependency container.
 */
@HiltAndroidApp
class GarageApplication : Application()
