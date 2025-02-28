package com.example.videoplayer.data_layer.models

data class VideoFile(
    val id: String?,
    val path: String,
    val title: String?,
    val fileName: String?,
    var size: String?,
    val duration: String?,
    var dateAdded: String?,
)