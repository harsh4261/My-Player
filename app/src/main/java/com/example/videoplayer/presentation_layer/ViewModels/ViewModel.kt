package com.example.videoplayer.presentation_layer.ViewModels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videoplayer.data_layer.Repo.MusicAppRepoImpl
import com.example.videoplayer.data_layer.Repo.VideoAppRepoImpl
import com.example.videoplayer.data_layer.models.Song
import com.example.videoplayer.data_layer.models.VideoFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModel @Inject constructor(
    val videoRepo: VideoAppRepoImpl,
    val musicRepo: MusicAppRepoImpl,
    val application: Application
) : ViewModel() {

    val showUi = MutableStateFlow(false)

    val videoList = MutableStateFlow(emptyList<VideoFile>())

    val isLoadingVideo = MutableStateFlow(false)

    fun loadVideos() {
        isLoadingVideo.value = true
        viewModelScope.launch {
            videoRepo.getAllVideos(application).collectLatest {
                videoList.value = it
            }
            Log.d("All Video at viewModel", "loadVideos: ${videoList.value}")
        }
        isLoadingVideo.value = false
    }

    val songList = MutableStateFlow(emptyList<Song>())
    fun fetchSongs(context: Context){
        viewModelScope.launch {
            musicRepo.fetchSongsFromDevice(context).collectLatest {
                songList.value = it
            }
        }
    }

}