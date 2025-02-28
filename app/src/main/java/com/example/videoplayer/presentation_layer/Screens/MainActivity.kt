package com.example.videoplayer.presentation_layer.Screens

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.videoplayer.ui.theme.VideoPlayerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mediaPlayer = MediaPlayer()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VideoPlayerTheme {
                App(mediaPlayer) // This function takes some run time permissions to work.
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()  // Release the MediaPlayer when the activity is destroyed
    }
}



