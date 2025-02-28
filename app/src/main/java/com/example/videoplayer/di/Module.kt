package com.example.videoplayer.di

import com.example.videoplayer.data_layer.Repo.MusicAppRepoImpl
import com.example.videoplayer.data_layer.Repo.VideoAppRepoImpl
import com.example.videoplayer.domain_layer.Repo.MusicAppRepo
import com.example.videoplayer.domain_layer.Repo.VideoAppRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DiModule{

    @Provides
    @Singleton
    fun provideVideoAppRepo() : VideoAppRepo {
        return VideoAppRepoImpl()
    }

    @Provides
    @Singleton
    fun provideMusicAppRepo() : MusicAppRepo {
        return MusicAppRepoImpl()
    }

}