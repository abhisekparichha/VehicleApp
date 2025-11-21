package com.autoflow.obd.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.autoflow.obd.core.utils.DefaultDispatcherProvider
import com.autoflow.obd.core.utils.DispatcherProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.plus
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CoreBindsModule {
    @Binds
    abstract fun bindDispatcherProvider(default: DefaultDispatcherProvider): DispatcherProvider
}

@Module
@InstallIn(SingletonComponent::class)
object CoreProvidesModule {
    private const val DATA_STORE = "drive_safe_obd"

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        prettyPrint = false
        encodeDefaults = true
    }

    @Provides
    @Singleton
    fun provideAppScope(dispatcherProvider: DispatcherProvider): CoroutineScope =
        CoroutineScope(dispatcherProvider.default + SupervisorJob())

    @Provides
    @Singleton
    fun providePreferenceDataStore(
        @ApplicationContext context: Context,
        appScope: CoroutineScope
    ): DataStore<Preferences> = PreferenceDataStoreFactory.create(
        scope = appScope,
        produceFile = { context.preferencesDataStoreFile(DATA_STORE) }
    )
}
