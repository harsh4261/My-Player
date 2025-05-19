package com.example.videoplayer.presentation_layer.Screens.Videos

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.navigation.NavHostController
import com.example.videoplayer.presentation_layer.Navigation.V_Page1
import com.example.videoplayer.presentation_layer.ViewModels.ViewModel
import kotlinx.coroutines.delay


@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerUI2(
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

    if (allVideo.isNotEmpty()) {
        val context = LocalContext.current

        // Create the ExoPlayer instance
        val exoPlayer = remember {
            ExoPlayer.Builder(context).build().apply {
                // Set initial playWhenReady flag
                playWhenReady = true
            }
        }

        val videoPaths = allVideo.map { it.path }

        // Find the initial index based on the provided videoPath
        val initialIndex = remember {
            videoPaths.indexOf(videoPath).takeIf { it >= 0 } ?: 0
        }

        // State for tracking current video index
        var currentIndex by remember { mutableIntStateOf(initialIndex) }

        // State for tracking play/pause
        var isPlaying by remember { mutableStateOf(true) }

        val configuration = LocalConfiguration.current
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val activity = (context as? ComponentActivity)

        // Initialize player with the current video
        LaunchedEffect(Unit) {
            if (videoPaths.isNotEmpty() && currentIndex in videoPaths.indices) {
                try {
                    exoPlayer.setMediaItem(MediaItem.fromUri(videoPaths[currentIndex].toUri()))
                    exoPlayer.prepare()
                } catch (e: Exception) {
                    Log.e("VideoPlayerUI", "Error initializing video: ${e.message}")
                }
            }
        }

        // Handle video changes
        LaunchedEffect(currentIndex) {
            if (videoPaths.isNotEmpty() && currentIndex in videoPaths.indices) {
                try {
                    // Stop and clear before loading new video
                    exoPlayer.stop()
                    exoPlayer.clearMediaItems()
                    exoPlayer.setMediaItem(MediaItem.fromUri(videoPaths[currentIndex].toUri()))
                    exoPlayer.prepare()
                    exoPlayer.play()
                    isPlaying = true
                } catch (e: Exception) {
                    Log.e("VideoPlayerUI", "Error changing video: ${e.message}")
                    Toast.makeText(context, "Error loading video", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Clean up player when leaving the composable
        DisposableEffect(Unit) {
            onDispose {
                exoPlayer.release()
            }
        }

        var showOption by rememberSaveable { mutableStateOf(false) }

        // Auto-hide controls after 3 seconds
        LaunchedEffect(showOption) {
            if (showOption) {
                delay(3000)
                showOption = false
            }
        }

        // Sync player state with our UI state
        DisposableEffect(exoPlayer) {
            val listener = object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    super.onPlaybackStateChanged(state)
                    isPlaying = exoPlayer.isPlaying

                    // Auto advance to next video when current one completes
                    if (state == Player.STATE_ENDED) {
                        if (currentIndex < videoPaths.size - 1) {
                            currentIndex++
                        }
                    }
                }

                override fun onIsPlayingChanged(playing: Boolean) {
                    super.onIsPlayingChanged(playing)
                    isPlaying = playing
                }
            }

            exoPlayer.addListener(listener)

            onDispose {
                exoPlayer.removeListener(listener)
            }
        }

        // Track video progress
        val playerPosition = remember { mutableStateOf(0L) }
        val playerDuration = remember { mutableStateOf(0L) }
        var isBuffering by remember { mutableStateOf(false) }

        // Update progress and buffer state periodically
        LaunchedEffect(exoPlayer) {
            while (true) {
                delay(500)  // Update more frequently for smoother UI
                playerPosition.value = exoPlayer.currentPosition
                playerDuration.value = exoPlayer.duration.coerceAtLeast(1)
                isBuffering = exoPlayer.playbackState == Player.STATE_BUFFERING
            }
        }

        // Animation values
        val controlsAlpha by animateFloatAsState(
            targetValue = if (showOption) 1f else 0f,
            animationSpec = tween(durationMillis = 300),
            label = "controlsAlpha"
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Video Player
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { showOption = !showOption },
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = false
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    }
                }
            )

            // Buffering indicator
            if (isBuffering) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF2196F3),
                        strokeWidth = 4.dp
                    )
                }
            }

            // Controls overlay with animation
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f * controlsAlpha))
                    .alpha(controlsAlpha)
            ) {
                // Back button at top
                AnimatedVisibility(
                    modifier = Modifier.align(Alignment.TopStart),
                    visible = showOption,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically()
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = 16.dp, start = 16.dp)
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                            .clickable {
                                exoPlayer.pause()
                                navController.navigate(V_Page1)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Video info at top
                AnimatedVisibility(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp, start = 30.dp),
                    visible = showOption,
                    enter = fadeIn() + slideInVertically(),
                    exit = fadeOut() + slideOutVertically(),
                ) {
                    val videoTitle = remember(currentIndex) {
                        allVideo.getOrNull(currentIndex)?.title ?: "Unknown Video"
                    }

                    Text(
                        text = videoTitle,
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.75f),
                                offset = Offset(1f, 1f),
                                blurRadius = 3f
                            )
                        ),
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 16.dp)
                    )
                }

                // Middle controls
                AnimatedVisibility(
                    modifier = Modifier.align(Alignment.Center),
                    visible = showOption,
                    enter = scaleIn(),
                    exit = scaleOut()
                ) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Previous button
                        var prevButtonPressed by remember { mutableStateOf(false) }
                        val prevButtonScale by animateFloatAsState(
                            targetValue = if (prevButtonPressed) 1.2f else 1f,
                            animationSpec = spring(stiffness = Spring.StiffnessLow),
                            label = "prevButtonScale"
                        )

                        val canGoPrevious = currentIndex > 0

                        IconButton(
                            onClick = {
                                if (canGoPrevious) {
                                    currentIndex--
                                } else {
                                    Toast.makeText(context, "This is the first video", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .size(60.dp)
                                .scale(prevButtonScale)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.6f))
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onPress = {
                                            prevButtonPressed = true
                                            try {
                                                awaitRelease()
                                            } finally {
                                                prevButtonPressed = false
                                            }
                                        }
                                    )
                                }
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipPrevious,
                                contentDescription = "Previous",
                                tint = if (canGoPrevious) Color.White else Color.Gray,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        // Play/Pause button
                        var playPausePressed by remember { mutableStateOf(false) }
                        val playPauseScale by animateFloatAsState(
                            targetValue = if (playPausePressed) 1.2f else 1f,
                            animationSpec = spring(stiffness = Spring.StiffnessLow),
                            label = "playPauseScale"
                        )

                        IconButton(
                            onClick = {
                                if (isPlaying) {
                                    exoPlayer.pause()
                                } else {
                                    exoPlayer.play()
                                }
                            },
                            modifier = Modifier
                                .size(80.dp)
                                .scale(playPauseScale)
                                .clip(CircleShape)
                                .background(Color(0xFF2196F3).copy(alpha = 0.8f))
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onPress = {
                                            playPausePressed = true
                                            try {
                                                awaitRelease()
                                            } finally {
                                                playPausePressed = false
                                            }
                                        }
                                    )
                                }
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color.White,
                                modifier = Modifier.size(48.dp)
                            )
                        }

                        // Next button
                        var nextButtonPressed by remember { mutableStateOf(false) }
                        val nextButtonScale by animateFloatAsState(
                            targetValue = if (nextButtonPressed) 1.2f else 1f,
                            animationSpec = spring(stiffness = Spring.StiffnessLow),
                            label = "nextButtonScale"
                        )

                        val canGoNext = currentIndex < videoPaths.size - 1

                        IconButton(
                            onClick = {
                                if (canGoNext) {
                                    currentIndex++
                                } else {
                                    Toast.makeText(context, "This is the last video", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .size(60.dp)
                                .scale(nextButtonScale)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.6f))
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onPress = {
                                            nextButtonPressed = true
                                            try {
                                                awaitRelease()
                                            } finally {
                                                nextButtonPressed = false
                                            }
                                        }
                                    )
                                }
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipNext,
                                contentDescription = "Next",
                                tint = if (canGoNext) Color.White else Color.Gray,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }

                // Progress bar and duration at bottom
                AnimatedVisibility(
                    visible = showOption,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Progress Bar with buffer indicator
                        Slider(
                            value = playerPosition.value.toFloat(),
                            onValueChange = { newPosition ->
                                exoPlayer.seekTo(newPosition.toLong())
                                playerPosition.value = newPosition.toLong()
                            },
                            valueRange = 0f..playerDuration.value.toFloat().coerceAtLeast(1f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(24.dp),
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFF2196F3),
                                activeTrackColor = Color(0xFF2196F3),
                                inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                            )
                        )

                        // Time display
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = formatDuration(playerPosition.value),
                                color = Color.White,
                                fontSize = 14.sp
                            )

                            Text(
                                text = formatDuration(playerDuration.value),
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // Additional controls at bottom right
                AnimatedVisibility(
                    visible = showOption,
                    enter = fadeIn() + slideInHorizontally(initialOffsetX = { it }),
                    exit = fadeOut() + slideOutHorizontally(targetOffsetX = { it }),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 16.dp, bottom = 80.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Fullscreen toggle
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.6f))
                                .clickable {
                                    activity?.let { act ->
                                        if (isLandscape) {
                                            act.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                                        } else {
                                            act.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isLandscape) Icons.Filled.FullscreenExit else Icons.Filled.Fullscreen,
                                contentDescription = "Toggle Fullscreen",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // Video quality/settings
                        var showQualityDialog by remember { mutableStateOf(false) }

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.6f))
                                .clickable {
                                    showQualityDialog = true
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = "Video Settings",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // Quality settings dialog
                        if (showQualityDialog) {
                            val qualities = listOf("Auto", "1080p", "720p", "480p", "360p")
                            var selectedQuality by remember { mutableStateOf(qualities[0]) }

                            Dialog(onDismissRequest = { showQualityDialog = false }) {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth(0.8f)
                                        .wrapContentHeight(),
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.DarkGray
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            "Select Quality",
                                            style = TextStyle(
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            ),
                                            modifier = Modifier.padding(bottom = 16.dp)
                                        )

                                        qualities.forEach { quality ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        selectedQuality = quality
                                                        showQualityDialog = false
                                                        // You would typically change the track selection here
                                                    }
                                                    .padding(vertical = 12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                RadioButton(
                                                    selected = selectedQuality == quality,
                                                    onClick = null,
                                                    colors = RadioButtonDefaults.colors(
                                                        selectedColor = Color(0xFF2196F3)
                                                    )
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = quality,
                                                    color = Color.White
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Large play/pause indicator that appears briefly on state change
            var showPlayPauseIndicator by remember { mutableStateOf(false) }

            LaunchedEffect(isPlaying) {
                showPlayPauseIndicator = true
                delay(500)
                showPlayPauseIndicator = false
            }

            AnimatedVisibility(
                visible = showPlayPauseIndicator && !showOption,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(60.dp)
                    )
                }
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Loading animation
            CircularProgressIndicator(
                modifier = Modifier.size(80.dp),
                color = Color(0xFF2196F3),
                strokeWidth = 8.dp
            )
        }
    }
}

// Helper function to format duration from milliseconds to MM:SS
fun formatDuration(durationMs: Long): String {
    val totalSeconds = durationMs / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}