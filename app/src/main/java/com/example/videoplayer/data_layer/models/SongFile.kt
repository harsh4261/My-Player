package com.example.videoplayer.data_layer.models


data class Song(
    val id: String?,
    val title: String?,
    val artist: String?,
    val filePath: String,
    val albumArt: String? = null,  // Optional field for album art URI
    val duration: String?,
)