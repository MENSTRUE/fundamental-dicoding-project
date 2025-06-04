package com.android.dicodingeventapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.android.dicodingeventapp.data.api.ApiConfig
import com.android.dicodingeventapp.data.api.ApiService
import com.android.dicodingeventapp.data.repository.EventRepository
import com.android.dicodingeventapp.utils.SettingPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideApiService() : ApiService {
        return ApiConfig.getApiService()
    }

    @Provides
    fun provideEventRepository(apiService: ApiService) : EventRepository {
        return EventRepository(apiService)
    }

    @Provides
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { context.dataStoreFile("settings") }
        )
    }

    @Provides
    fun provideSettingPreferences(dataStore: DataStore<Preferences>): SettingPreferences {
        return SettingPreferences(dataStore)
    }


}