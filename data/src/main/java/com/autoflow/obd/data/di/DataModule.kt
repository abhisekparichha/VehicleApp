package com.autoflow.obd.data.di

import android.content.Context
import androidx.room.Room
import com.autoflow.obd.data.db.DriveSafeDao
import com.autoflow.obd.data.db.DriveSafeDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): DriveSafeDatabase =
        Room.databaseBuilder(
            context,
            DriveSafeDatabase::class.java,
            "drive-safe-obd.db"
        ).fallbackToDestructiveMigration().build()

    @Provides
    fun provideDao(db: DriveSafeDatabase): DriveSafeDao = db.driveSafeDao()
}
