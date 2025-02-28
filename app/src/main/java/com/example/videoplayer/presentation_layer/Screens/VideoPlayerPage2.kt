package com.example.videoplayer.presentation_layer.Screens

import android.content.res.Configuration
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavHostController
import com.example.videoplayer.presentation_layer.Navigation.V_Page1
import com.example.videoplayer.presentation_layer.ViewModels.ViewModel
import kotlinx.coroutines.delay


@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerPage2(
    navController: NavHostController,
    modifier: Modifier,
    videoPath: String,
    viewModel: ViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.loadVideos()
    }


    // Fetch video paths from ViewModel
    val allVideo = viewModel.videoList.collectAsState().value
//    Log.d("allVideo1", "$allVideo")
    if (allVideo.isNotEmpty()) {
//        Log.d("allVideo12", "$allVideo")

        val context = LocalContext.current
        val exoPlayer = remember { ExoPlayer.Builder(context).build() }


        val videoPaths by remember { derivedStateOf { allVideo.map { it.path } } }


        var currentIndex by remember {
            mutableIntStateOf(
                videoPaths.indexOf(videoPath).takeIf { it >= 0 } ?: 0)
        }

        val configuration = LocalConfiguration.current
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        // Load and play video based on current index
        LaunchedEffect(currentIndex) {
            exoPlayer.setMediaItem(MediaItem.fromUri(videoPaths[currentIndex].toUri()))
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
        }

        DisposableEffect(Unit) {
            onDispose {
                exoPlayer.release()
            }
        }

        var showOption by rememberSaveable {
            mutableStateOf(false)
        }

        LaunchedEffect(showOption) {
            delay(3000)
            showOption = false

        }

        Column(modifier = modifier.fillMaxSize()) {
            // Video Player Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()

                    .background(Color.Black)
            ) {
                AndroidView(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            showOption = !showOption
                        },
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            player = exoPlayer
                            useController = false
                        }
                    }
                )

                if (showOption) {
                    // Control Buttons (Previous, Play/Pause, Next)
                    Box(
                        modifier = modifier
                            .padding(top = 10.dp)
                            .fillMaxWidth()
                            .height(50.dp),
                        contentAlignment = Alignment.TopStart
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back to Page 1",
                            tint = Color.White,
                            modifier = Modifier
                                .padding(horizontal = 15.dp)
                                .size(30.dp)
                                .clickable {
                                    navController.navigate(V_Page1)
                                }

                        )

                    }
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        // Previous button
                        IconButton(
                            onClick = {
                                if (currentIndex > 0) {
                                    currentIndex--
                                }
                            },
                            modifier = Modifier.size(60.dp)
                        ) {

                            Icon(
                                Icons.Default.SkipPrevious,
                                contentDescription = "Previous",
                                tint = Color.White
                            )
                        }

                        // Play/Pause button

                        var myIconState by rememberSaveable { mutableStateOf(IconState.Pause) }

                        val icon: ImageVector = when (myIconState) {
                            IconState.Pause -> Icons.Filled.Pause
                            IconState.Play -> Icons.Filled.PlayArrow
                        }


                        IconButton(
                            onClick = {
                                exoPlayer.playWhenReady = !exoPlayer.playWhenReady
                                myIconState =
                                    if (myIconState == IconState.Pause) IconState.Play else IconState.Pause
                            },
                            modifier = Modifier.size(60.dp)
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = "Play/Pause",
                                tint = Color.White
                            )
                        }

                        // Next button

                        IconButton(
                            onClick = {
                                if (currentIndex < videoPaths.size - 1) {
                                    currentIndex++
                                }
                            },
                            modifier = Modifier.size(60.dp)
                        ) {
                            Icon(
                                Icons.Default.SkipNext,
                                contentDescription = "Next",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Log.d("allVideo123", "$allVideo")

            Text("Something wrong")
        }
    }


}

//enum class for play/pause button
enum class IconState {
    Play, Pause
}

