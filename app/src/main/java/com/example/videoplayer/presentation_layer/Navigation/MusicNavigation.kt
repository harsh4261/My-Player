package com.example.videoplayer.presentation_layer.Navigation

import android.media.MediaPlayer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.videoplayer.presentation_layer.Screens.Songs.PlayerScreenUi
import com.example.videoplayer.presentation_layer.Screens.Songs.SongListScreenUi

@Composable
fun MusicNavigation(
    modifier: Modifier,
    mediaPlayer: MediaPlayer,
    showBottomBar: MutableState<Boolean>
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "song_list") {
        composable("song_list") {
            showBottomBar.value = true
            SongListScreenUi(navController=navController, modifier = modifier )
        }
        composable("player/{title}/{artist}/{duration}/{filePath}/{index}") { backStackEntry ->
            showBottomBar.value = false
            val title = backStackEntry.arguments?.getString("title") ?: ""
            val artist = backStackEntry.arguments?.getString("artist") ?: ""
            val duration = backStackEntry.arguments?.getString("duration") ?: ""
            val filePath = backStackEntry.arguments?.getString("filePath") ?: ""
            val index = backStackEntry.arguments?.getString("index")?.toInt() ?: 0
            PlayerScreenUi(
                navController = navController,
                mediaPlayer = mediaPlayer,
                title = title,
                artist = artist,
                duration = duration,
                filePath = filePath,
                currentIndex = index,
            )
        }
    }
}
