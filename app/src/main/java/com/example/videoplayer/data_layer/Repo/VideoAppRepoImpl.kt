package com.example.videoplayer.data_layer.Repo

import android.app.Application
import android.provider.MediaStore
import com.example.videoplayer.data_layer.models.VideoFile
import com.example.videoplayer.domain_layer.Repo.VideoAppRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class   VideoAppRepoImpl @Inject constructor() : VideoAppRepo {

    override suspend fun getAllVideos(application: Application): Flow<ArrayList<VideoFile>> {

        val allVideo =  ArrayList<VideoFile>()
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.DATE_ADDED,

            )

        val uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val memoryCursor = application.contentResolver.query(
            uri,
            projection,
            null,
            null,
        )
        if (memoryCursor != null){
            while (memoryCursor.moveToNext()){
                val id = memoryCursor.getString(0)
                val path = memoryCursor.getString(1)
                val title = memoryCursor.getString(2)
                val fileName = memoryCursor.getString(3)
                val size = memoryCursor.getString(4)
                val duration = memoryCursor.getString(5)
                val dateAdded = memoryCursor.getString(6)

                val videoFile = VideoFile(id, path, title, fileName, size, duration,  dateAdded)
                allVideo.add(videoFile)

                if (memoryCursor.isLast){
                    break
                }

            }

            memoryCursor.close()

        }

        return flow {
            emit(allVideo)
        }

    }

}