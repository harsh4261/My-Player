package com.example.videoplayer.presentation_layer.Navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.videoplayer.presentation_layer.Screens.Videos.VideoPlayerUI
import com.example.videoplayer.presentation_layer.Screens.Videos.VideoPlayerUI2
import kotlinx.serialization.Serializable

@Composable
fun VideoPlayerNavigation(modifier: Modifier, showBottomBar: MutableState<Boolean>) {
    val videoPlayerNavController = rememberNavController()

    NavHost(
        navController = videoPlayerNavController,
        startDestination = V_Page1
    ) {
        composable<V_Page1> {
            showBottomBar.value = true
            VideoPlayerUI(navController = videoPlayerNavController,  modifier=modifier)

        }
        composable<V_Page2> {
            showBottomBar.value = false
            val videoDetail : V_Page2 = it.toRoute()
//            Log.d("VideoPath", "VideoPlayerPage: ${ videoDetail.path}")
            VideoPlayerUI2(navController = videoPlayerNavController, modifier = modifier,videoPath =videoDetail.path)

        }
    }
}

@Serializable
object V_Page1

@Serializable
data class V_Page2(val path : String)