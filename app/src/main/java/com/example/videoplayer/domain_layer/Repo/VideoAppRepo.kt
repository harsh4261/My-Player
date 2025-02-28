package com.example.videoplayer.domain_layer.Repo

import android.app.Application
import com.example.videoplayer.data_layer.models.VideoFile
import kotlinx.coroutines.flow.Flow

interface VideoAppRepo {

       suspend fun getAllVideos(application : Application) : Flow<ArrayList<VideoFile>>
}