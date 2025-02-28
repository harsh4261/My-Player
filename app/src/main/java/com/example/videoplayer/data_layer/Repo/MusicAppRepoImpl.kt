package com.example.videoplayer.data_layer.Repo

import android.content.Context
import android.provider.MediaStore
import com.example.videoplayer.data_layer.models.Song
import com.example.videoplayer.domain_layer.Repo.MusicAppRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MusicAppRepoImpl @Inject constructor() : MusicAppRepo {

    override fun fetchSongsFromDevice(context: Context): Flow<List<Song>> {
        val songList = mutableListOf<Song>()
        val contentResolver = context.contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA, // File path for the song
            MediaStore.Audio.Media.DURATION // Duration of the song

        )

        val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"
        val cursor = contentResolver.query(uri, projection, selection, null, null)

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val dataColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val durationColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

            while (it.moveToNext()) {
                val id = it.getString(idColumn)
                val title = it.getString(titleColumn)
                val artist = it.getString(artistColumn)
                val data = it.getString(dataColumn)
                val duration = it.getLong(durationColumn)


                songList.add(Song(id, title, artist, data, duration = duration.toString()))
            }
        }

        return flow {
            emit(songList)
        }
    }

}