package com.valentinesgarage.di

import android.content.Context
import com.valentinesgarage.data.local.EmployeeDao
import com.valentinesgarage.data.local.GarageDatabase
import com.valentinesgarage.data.local.ServiceTaskDao
import com.valentinesgarage.data.local.UserDao
import com.valentinesgarage.data.local.VehicleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module — provides the database and DAO instances to the dependency graph.
 * Using [SingletonComponent] ensures one database instance per application lifetime.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): GarageDatabase =
        GarageDatabase.create(context)

    @Provides
    fun provideVehicleDao(db: GarageDatabase): VehicleDao = db.vehicleDao()

    @Provides
    fun provideServiceTaskDao(db: GarageDatabase): ServiceTaskDao = db.serviceTaskDao()

    @Provides
    fun provideEmployeeDao(db: GarageDatabase): EmployeeDao = db.employeeDao()

    @Provides
    fun provideUserDao(db: GarageDatabase): UserDao = db.userDao()
}
