package com.android.dicodingeventapp.di

import com.android.dicodingeventapp.data.api.ApiConfig
import com.android.dicodingeventapp.data.api.ApiService
import com.android.dicodingeventapp.data.repository.EventRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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


}