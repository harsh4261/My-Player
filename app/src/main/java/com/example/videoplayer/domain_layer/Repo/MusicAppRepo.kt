package com.example.videoplayer.domain_layer.Repo

import android.content.Context
import com.example.videoplayer.data_layer.models.Song
import kotlinx.coroutines.flow.Flow

interface MusicAppRepo {

    fun fetchSongsFromDevice(context: Context): Flow<List<Song>>
}