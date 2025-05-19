package com.example.videoplayer.presentation_layer.Screens.Songs

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import coil3.compose.rememberAsyncImagePainter
import com.example.videoplayer.data_layer.models.Song
import com.example.videoplayer.presentation_layer.ViewModels.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import kotlin.math.sin

@Composable
fun SongListScreenUi(
    navController: NavHostController,
    viewModel: ViewModel = hiltViewModel(),
    modifier: Modifier
) {
    val context = LocalContext.current
    var songs by remember { mutableStateOf(emptyList<Song>()) }

    // Adding animation for content appearance
    val contentAlpha = remember { Animatable(0f) }
    LaunchedEffect(true) {
        viewModel.fetchSongs(context)
        // Fade in animation when content loads
        contentAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800)
        )
    }

    songs = viewModel.songList.collectAsState().value

    Box(
        modifier =  modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = if (isSystemInDarkTheme()) {
                        listOf(Color(20, 20, 40), Color(10, 10, 20))
                    } else {
                        listOf(Color(230, 240, 250), Color(210, 220, 235))
                    }
                )
            )
            .padding(top = 30.dp)
            .graphicsLayer { alpha = contentAlpha.value }

    ) {
        if (songs.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Animation for empty state
                var currentRotation by remember { mutableFloatStateOf(0f) }
                val rotation = remember { Animatable(0f) }

                LaunchedEffect(Unit) {
                    // Create continuous rotation animation
                    rotation.animateTo(
                        targetValue = 360f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(3000, easing = LinearEasing),
                            repeatMode = RepeatMode.Restart
                        )
                    )
                }

                Icon(
                    imageVector = Icons.Filled.MusicNote,
                    contentDescription = "No Music",
                    modifier = Modifier
                        .size(80.dp)
                        .graphicsLayer {
                            rotationZ = rotation.value
                            alpha = 0.7f
                        },
                    tint = if (isSystemInDarkTheme()) Color(100, 100, 200, 150) else Color(
                        100,
                        100,
                        200
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "No Songs Found",
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.8f) else Color.DarkGray
                    )
                )
            }
        } else {
            SongListUi(
                songs = songs,
                onPlay = { song ->
                    // Navigate to the Player Screen with encoded song details
                    val index = songs.indexOf(song)
                    navController.navigate(
                        "player/${Uri.encode(song.title)}/${Uri.encode(song.artist)}/${
                            Uri.encode(song.duration)
                        }/${Uri.encode(song.filePath)}/$index"
                    )
                },
            )
        }
    }
}

@Composable
fun SongListUi(
    songs: List<Song>,
    onPlay: (Song) -> Unit,

) {
    // Add top bar with animation
    var topBarHeight by remember { mutableStateOf(0.dp) }
    val topBarHeightPx = with(LocalDensity.current) { topBarHeight.toPx() }
    val topBarOffsetHeightPx = remember { mutableStateOf(0f) }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val newOffset = topBarOffsetHeightPx.value + delta
                topBarOffsetHeightPx.value = newOffset.coerceIn(-topBarHeightPx, 0f)
                return Offset.Zero
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
    ) {
        // Top gradient bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = if (isSystemInDarkTheme()) {
                            listOf(Color(30, 30, 50), Color(20, 20, 40, 180))
                        } else {
                            listOf(Color(230, 240, 250), Color(210, 220, 235, 180))
                        }
                    )
                )
                .graphicsLayer {
                    translationY = topBarOffsetHeightPx.value
                }
                .onGloballyPositioned {
                    topBarHeight = with(LocalDensity) { it.size.height }.dp
                },
            contentAlignment = Alignment.Center
        ) {
            // Animation states
            val infiniteTransition = rememberInfiniteTransition(label = "headerAnimations")



            // 1. Scale animation
            val scale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.2f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 4000
                        1f at 0
                        1f at 1000  // Wait for rotation to finish
                        1.2f at 2000
                        1f at 3000
                        1f at 4000
                    },
                    repeatMode = RepeatMode.Restart
                ),
                label = "scale"
            )

            var  baseColor  = remember { mutableStateOf(Color.White) }
            var accentColor  = remember { mutableStateOf( Color(173, 216, 230)) }
            if (isSystemInDarkTheme()) {
                baseColor.value= Color.White
                accentColor.value = Color(173, 216, 230)
            }else{
                baseColor.value = Color.Black
                accentColor.value = Color(25, 118, 210)
            }

            // 2. Color animation
            val colorTransition by infiniteTransition.animateColor(
                initialValue = if (isSystemInDarkTheme()) Color.White else Color.Black,
                targetValue = if (isSystemInDarkTheme()) Color.White else Color.Black,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 4000
                        val baseColor =  baseColor.value
                        val accentColor =  accentColor.value

                        baseColor at 0
                        baseColor at 2000  // Wait for rotation and scale
                        accentColor at 2500
                        baseColor at 3000
                        baseColor at 4000
                    },
                    repeatMode = RepeatMode.Restart
                ),
                label = "color"
            )

            // 3. Bounce animation
            val offsetY by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 0f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 4000
                        0f at 0
                        0f at 3000  // Wait for previous animations
                        -10f at 3250
                        10f at 3500
                        -5f at 3750
                        0f at 4000
                    },
                    repeatMode = RepeatMode.Restart
                ),
                label = "bounce"
            )

            // Apply all animations to the text
            Text(
                "Your Music",
                style = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorTransition,
                    letterSpacing = 1.sp
                ),
                modifier = Modifier
                    .graphicsLayer {

                        scaleX = scale
                        scaleY = scale
                        translationY = offsetY
                    }
            )
        }

        LazyColumn(
            modifier = Modifier
                .padding(top = 60.dp), // Adjust for the top bar
            contentPadding = PaddingValues(bottom = 30.dp)
        ) {
            items(songs) { song ->
                // Animated song item
                var itemAlpha by remember { mutableStateOf(0f) }
                var itemScale by remember { mutableStateOf(0.95f) }

                LaunchedEffect(song) {
                    launch {
                        animate(
                            0f,
                            1f,
                            animationSpec = tween(300, easing = FastOutSlowInEasing)
                        ) { value, _ ->
                            itemAlpha = value
                        }
                    }
                    launch {
                        animate(
                            0.95f,
                            1f,
                            animationSpec = tween(350, easing = FastOutSlowInEasing)
                        ) { value, _ ->
                            itemScale = value
                        }
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                        .graphicsLayer {
                            alpha = itemAlpha
                            scaleX = itemScale
                            scaleY = itemScale
                        }
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                onPlay(song)
                            }
                        ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 10.dp,
                        focusedElevation = 8.dp,
                        hoveredElevation = 8.dp
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSystemInDarkTheme()) {
                            Color(37, 35, 45, 255)
                        } else {
                            Color(245, 245, 250, 255)
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Animated music icon
                        var isAnimating by remember { mutableStateOf(false) }
                        val animatedSize by animateFloatAsState(
                            targetValue = if (isAnimating) 33f else 28f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(800, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "SizeAnimation"
                        )

                        LaunchedEffect(Unit) {
                            isAnimating = true
                        }

                        Box(
                            modifier = Modifier
                                .padding(8.dp)
                                .size(50.dp)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = if (isSystemInDarkTheme()) {
                                            listOf(Color(80, 80, 120), Color(60, 60, 90))
                                        } else {
                                            listOf(Color(180, 200, 255), Color(150, 170, 240))
                                        }
                                    ),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.QueueMusic,
                                contentDescription = "Music Logo",
                                modifier = Modifier.size(animatedSize.dp),
                                tint = if (isSystemInDarkTheme()) Color.White else Color.White
                            )
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            song.title?.let {
                                Text(
                                    text = it,
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSystemInDarkTheme()) Color.White else Color.Black
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }

                            Spacer(Modifier.height(6.dp))

                            song.artist?.let {
                                Text(
                                    text = it,
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        color = if (isSystemInDarkTheme()) Color.LightGray else Color.DarkGray
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }

                        Column(
                            modifier = Modifier
                                .padding(end = 16.dp),
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.Center
                        ) {
                            song.duration?.let {
                                Text(
                                    text = formatDuration(it.toLong()),
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (isSystemInDarkTheme()) Color(
                                            200,
                                            200,
                                            255
                                        ) else Color(100, 100, 200)
                                    )
                                )
                            }

                            Spacer(Modifier.height(6.dp))

                            Icon(
                                imageVector = Icons.Filled.PlayCircleFilled,
                                contentDescription = "Play",
                                modifier = Modifier.size(24.dp),
                                tint = if (isSystemInDarkTheme()) Color(
                                    100,
                                    255,
                                    200
                                ) else Color(50, 200, 100)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TrackSliderUi(
    value: Float,
    onValueChange: (newValue: Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    songDuration: Float
) {
    // Track the interaction state
    val interactionSource = remember { MutableInteractionSource() }
    val isDragged by interactionSource.collectIsDraggedAsState()

    // Visual feedback animation for the track
    val trackHeightPx by animateDpAsState(
        targetValue = if (isDragged) 6.dp else 4.dp,
        animationSpec = tween(150),
        label = "TrackHeight"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(48.dp),
        contentAlignment = Alignment.Center
    ) {
        // Custom track implementation for better visual control
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(trackHeightPx)
        ) {
            // Draw track background (inactive part)
            drawRoundRect(
                color = Color.White.copy(alpha = 0.2f),
                cornerRadius = CornerRadius(trackHeightPx.toPx() / 2),
                size = Size(size.width, trackHeightPx.toPx())
            )

            // Calculate progress width based on value
            val progressWidth = (value / songDuration) * size.width

            // Draw active track with gradient
            val brush = Brush.horizontalGradient(
                colors = listOf(
                    Color(100, 255, 200),
                    Color(100, 200, 255)
                )
            )

            // Only draw progress if there is progress to show
            if (progressWidth > 0) {
                drawRoundRect(
                    brush = brush,
                    cornerRadius = CornerRadius(trackHeightPx.toPx() / 2),
                    size = Size(progressWidth, trackHeightPx.toPx())
                )
            }
        }

        // Use Slider for interaction but make it invisible
        Slider(
            value = value,
            onValueChange = onValueChange,
            onValueChangeFinished = onValueChangeFinished,
            valueRange = 0f..songDuration,
            interactionSource = interactionSource,
            modifier = Modifier
                .fillMaxWidth()
                .alpha(0f),  // Make the original slider invisible
            colors = SliderDefaults.colors(
                thumbColor = Color.Transparent,
                activeTrackColor = Color.Transparent,
                inactiveTrackColor = Color.Transparent
            )
        )

        // Show pulse animation when dragging
        if (isDragged) {
            val pulsatingAlpha = remember { Animatable(0.7f) }

            LaunchedEffect(isDragged) {
                pulsatingAlpha.animateTo(
                    targetValue = 0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(800),
                        repeatMode = RepeatMode.Restart
                    )
                )
            }
        }
    }
}

@Composable
fun PlayerScreenUi(
    navController: NavHostController,
    filePath: String,
    mediaPlayer: MediaPlayer,
    currentIndex: Int,
    viewModel: ViewModel = hiltViewModel(),
    title: String,
    artist: String,
    duration: String
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val lifecycleOwner = LocalLifecycleOwner.current

    // Use collectAsState() to match the working implementation
    var isPlaying by remember { mutableStateOf(false) }
    var albumArtUri by remember { mutableStateOf<String?>(null) }
    var progress by remember { mutableFloatStateOf(0f) }
    val currentPosition = remember { mutableFloatStateOf(0f) }
    val sliderPosition = remember { mutableFloatStateOf(0f) }
    var totalDuration by remember { mutableStateOf(duration.toFloat()) }

    // Fetch songs first
    LaunchedEffect(true) {
        viewModel.fetchSongs(context)
    }

    // Use collectAsState() instead of collectAsStateWithLifecycle()
    val songs = viewModel.songList.collectAsState().value

    // Store completion listener as a remembered object to prevent recreation
    val completionListener = remember {
        MediaPlayer.OnCompletionListener {
            Log.d("PlayerScreen", "Song completed at index: $currentIndex")
            // Move to next song when current song completes
            if (songs.isNotEmpty()) {
                val nextIndex = (currentIndex + 1) % songs.size
                val nextSong = songs[nextIndex]


                navController.navigate(
                    "player/${Uri.encode(nextSong.title)}/${Uri.encode(nextSong.artist)}/${
                        Uri.encode(nextSong.duration)
                    }/${Uri.encode(nextSong.filePath)}/$nextIndex"
                )
            }
        }
    }

    // Lock screen to portrait mode when entering this screen
    LaunchedEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    // Reset to sensor mode when leaving this screen
    DisposableEffect(Unit) {
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    // Observe lifecycle to properly handle player states
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    if (mediaPlayer.isPlaying) mediaPlayer.pause()
                }

                Lifecycle.Event.ON_DESTROY -> {
                    mediaPlayer.release()
                }

                else -> {} // No action needed for other events
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Animation states
    val animateAlbumArt = remember { Animatable(0.8f) }
    val albumRotation = remember { Animatable(0f) }
    var pulseAnimationValue by remember { mutableFloatStateOf(1f) }
    val pulseAnim by animateFloatAsState(
        targetValue = pulseAnimationValue,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseAnimation"
    )

    // Function to navigate to the next song
    val navigateToNextSong = NavigateToNextSong(songs, currentIndex, navController)

    // Function to navigate to the previous song
    val navigateToPrevSong = NavigationToPreviousSong(songs, currentIndex, navController)

    // Trigger pulse animation when playing
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            pulseAnimationValue = 1.05f
            // Start rotating the album art when playing
            albumRotation.animateTo(
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(20000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        } else {
            pulseAnimationValue = 1f
            // Stop rotation animation
            albumRotation.stop()
        }
    }

    // Animate album art appearance
    LaunchedEffect(albumArtUri) {
        animateAlbumArt.snapTo(0.8f)
        animateAlbumArt.animateTo(
            targetValue = 1f,
            animationSpec = tween(500, easing = FastOutSlowInEasing)
        )
    }

    // Setup media player when filePath changes (when navigating to a new song)
    LaunchedEffect(filePath) {
        Log.d("PlayerScreen", "Setting up media player for file: $filePath")
        try {
            // Reset media player before setting new data source
            mediaPlayer.reset()

            // Set the completion listener BEFORE preparing the media player
            mediaPlayer.setOnCompletionListener(completionListener)

            // Set data source and prepare
            mediaPlayer.setDataSource(filePath)
            mediaPlayer.prepare()
            mediaPlayer.start()
            isPlaying = true

            // Get album art and total duration
            albumArtUri = getAlbumArtUri(context, filePath)
            totalDuration = mediaPlayer.duration.toFloat()

            // Reset progress
            progress = 0f

            Log.d(
                "PlayerScreen",
                "Started playing: $title at index $currentIndex, duration: $totalDuration"
            )
        } catch (e: Exception) {
            Log.e("PlayerScreen", "Error playing song: ${e.message}", e)
            Toast.makeText(context, "Error playing song: ${e.message}", Toast.LENGTH_SHORT).show()
        }

        // Update progress while playing
        while (true) {
            try {
                if (mediaPlayer.isPlaying) {
                    progress = mediaPlayer.currentPosition.toFloat() / totalDuration
                    currentPosition.floatValue = mediaPlayer.currentPosition.toFloat()
                    sliderPosition.floatValue = currentPosition.floatValue
                }
            } catch (e: Exception) {
                Log.e("PlayerScreen", "Error updating progress: ${e.message}")
            }
            delay(500) // Update twice a second for smoother animation
        }
    }

    // Create background gradient based on system theme
    val backgroundGradient = if (isSystemInDarkTheme()) {
        Brush.verticalGradient(
            colors = listOf(
                Color(40, 40, 80),
                Color(15, 15, 30)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(100, 200, 255),
                Color(50, 130, 200)
            )
        )
    }
    var waveColor = remember {
        mutableStateOf(Color(100, 100, 200, 40))
    }
    if (isSystemInDarkTheme()) {
        waveColor.value = Color(100, 100, 200, 40)
    } else {
        waveColor.value = Color(255, 255, 255, 40)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        // Create animated waves in background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Draw animated wave pattern
            val wavePaint = Paint().apply {
                color = waveColor.value
                style = PaintingStyle.Stroke
                strokeWidth = 2f
            }

            val waveCount = 3
            val now = System.currentTimeMillis() / 1000f

            for (i in 0 until waveCount) {
                val path = Path()
                val waveHeight = 20f + (i * 10f)
                val frequency = 0.01f + (i * 0.005f)
                val speed = now * (0.5f + (i * 0.2f))

                path.moveTo(0f, canvasHeight / 2)

                for (x in 0..canvasWidth.toInt() step 5) {
                    val y = sin((x * frequency) + speed) * waveHeight + (canvasHeight / 2)
                    path.lineTo(x.toFloat(), y)
                }

                drawPath(
                    path = path,
                    color = waveColor.value,
                    style = Stroke(width = wavePaint.strokeWidth)
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top bar with back button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .statusBarsPadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        mediaPlayer.reset() // Reset media player before navigating back
                        navController.navigate("song_list") {
                            popUpTo("song_list")
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier
                            .size(28.dp)
                            .shadow(4.dp, CircleShape)
                    )
                }

                Spacer(Modifier.width(16.dp))

                Text(
                    text = "Now Playing",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        shadow = Shadow(color = Color.Black.copy(alpha = 0.3f), blurRadius = 4f)
                    )
                )
            }

            // Album art with animation
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(24.dp)
            ) {
                val albumArtModifier = Modifier
                    .size(320.dp)
                    .graphicsLayer {
                        scaleX = animateAlbumArt.value * pulseAnim
                        scaleY = animateAlbumArt.value * pulseAnim
                        rotationZ = if (isPlaying) albumRotation.value else 0f
                        shadowElevation = 16f
                    }
                    .border(
                        width = 6.dp,
                        brush = Brush.linearGradient(
                            colors = if (isSystemInDarkTheme()) {
                                listOf(Color(90, 90, 140), Color(40, 40, 80))
                            } else {
                                listOf(Color.White, Color(200, 220, 255))
                            }
                        ),
                        shape = CircleShape
                    )
                    .padding(8.dp)
                    .clip(CircleShape)

                if (albumArtUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = albumArtUri,
                            onError = {
                                Log.e("PlayerScreen", "Error loading album art: $it")
                            }
                        ),
                        contentDescription = "Album Art",
                        modifier = albumArtModifier,
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = albumArtModifier.background(
                            brush = Brush.radialGradient(
                                colors = if (isSystemInDarkTheme()) {
                                    listOf(Color(60, 60, 100), Color(30, 30, 60))
                                } else {
                                    listOf(Color(180, 220, 255), Color(100, 150, 220))
                                }
                            )
                        ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MusicNote,
                            contentDescription = "Music",
                            modifier = Modifier.size(100.dp),
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            // Song info and controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Song title with marquee effect if too long
                val titleScrollState = rememberScrollState()
                val titleTextWidth = remember { mutableStateOf(0) }
                val screenWidth = LocalConfiguration.current.screenWidthDp.dp

                LaunchedEffect(title) {
                    if (titleTextWidth.value > screenWidth.value - 48) {
                        // If text is wider than screen, scroll it
                        while (true) {
                            titleScrollState.animateScrollTo(
                                value = titleScrollState.maxValue,
                                animationSpec = tween(
                                    durationMillis = titleTextWidth.value * 20,
                                    easing = LinearEasing
                                )
                            )
                            delay(1000)
                            titleScrollState.animateScrollTo(
                                value = 0,
                                animationSpec = tween(
                                    durationMillis = 500,
                                    easing = FastOutSlowInEasing
                                )
                            )
                            delay(1000)
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(titleScrollState)
                ) {
                    Text(
                        text = title,
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            shadow = Shadow(color = Color.Black.copy(alpha = 0.3f), blurRadius = 4f)
                        ),
                        modifier = Modifier.onGloballyPositioned {
                            titleTextWidth.value = it.size.width
                        }
                    )
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = artist,
                    style = TextStyle(
                        fontSize = 18.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(Modifier.height(24.dp))

                // Enhanced track slider
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Interactive slider
                    TrackSliderUi(
                        value = sliderPosition.floatValue,
                        onValueChange = {
                            sliderPosition.floatValue = it
                        },
                        onValueChangeFinished = {
                            currentPosition.floatValue = sliderPosition.floatValue
                            mediaPlayer.seekTo(sliderPosition.floatValue.toInt())
                        },
                        songDuration = totalDuration
                    )
                }

                // Time indicators
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatDuration(currentPosition.floatValue.toLong()),
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    )

                    val remainTime = remember {
                        mutableFloatStateOf(totalDuration - currentPosition.floatValue)
                    }
                    if (remainTime.value == 0F) {
                        navigateToNextSong()
                    }
                    Text(
                        text = if (remainTime.value >= 0) "-${formatDuration(remainTime.value.toLong())}" else "00:00",
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    )
                }

                Spacer(Modifier.height(24.dp))

                // Enhanced playback controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Previous button with animation
                    IconButton(
                        onClick = { navigateToPrevSong() },
                        modifier = Modifier.size(60.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = "Previous",
                            modifier = Modifier.size(42.dp),
                            tint = Color.White
                        )
                    }

                    // Play/Pause button with animation
                    val playButtonScale = remember { Animatable(1f) }

                    LaunchedEffect(isPlaying) {
                        playButtonScale.animateTo(
                            targetValue = 0.85f,
                            animationSpec = tween(100)
                        )
                        playButtonScale.animateTo(
                            targetValue = 1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .graphicsLayer {
                                scaleX = playButtonScale.value
                                scaleY = playButtonScale.value
                            }
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(100, 255, 200),
                                        Color(50, 200, 150)
                                    )
                                ),
                                shape = CircleShape
                            )
                            .clickable {
                                if (isPlaying) {
                                    mediaPlayer.pause()
                                    isPlaying = false
                                } else {
                                    mediaPlayer.start()
                                    isPlaying = true
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = "Play/Pause",
                            modifier = Modifier.size(40.dp),
                            tint = Color.White
                        )
                    }

                    // Next button
                    IconButton(
                        onClick = { navigateToNextSong() },
                        modifier = Modifier.size(60.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SkipNext,
                            contentDescription = "Next",
                            modifier = Modifier.size(42.dp),
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NavigateToNextSong(
    songs: List<Song>,
    currentIndex: Int,
    navController: NavHostController
): () -> Unit {
    val navigateToNextSong = {
        if (songs.isNotEmpty()) {
            val nextIndex = (currentIndex + 1) % songs.size
            Log.d("PlayerScreen", "Manual navigation to next song: $nextIndex")

            if (nextIndex >= 0 && nextIndex < songs.size) {
                val nextSong = songs[nextIndex]
                navController.navigate(
                    "player/${Uri.encode(nextSong.title)}/${Uri.encode(nextSong.artist)}/${
                        Uri.encode(nextSong.duration)
                    }/${Uri.encode(nextSong.filePath)}/$nextIndex"
                )
            }
        }
    }
    return navigateToNextSong
}


@Composable
private fun NavigationToPreviousSong(
    songs: List<Song>,
    currentIndex: Int,
    navController: NavHostController
): () -> Unit {
    val navigateToPrevSong = {
        if (songs.isNotEmpty()) {
            val prevIndex = if (currentIndex > 0) currentIndex - 1 else songs.size - 1
            Log.d("PlayerScreen", "Manual navigation to prev song: $prevIndex")

            if (prevIndex >= 0 && prevIndex < songs.size) {
                val prevSong = songs[prevIndex]
                navController.navigate(
                    "player/${Uri.encode(prevSong.title)}/${Uri.encode(prevSong.artist)}/${
                        Uri.encode(prevSong.duration)
                    }/${Uri.encode(prevSong.filePath)}/$prevIndex"
                )
            }
        }
    }
    return navigateToPrevSong
}



fun getAlbumArtUri(context: Context, filePath: String): String? {
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(filePath)
    val albumArt = retriever.embeddedPicture
    retriever.release()

    return if (albumArt != null) {
        val bitmap = BitmapFactory.decodeByteArray(albumArt, 0, albumArt.size)
        val uri = saveBitmapToCache(context, bitmap)
        uri.toString()
    } else {
        null
    }
}

fun saveBitmapToCache(context: Context, bitmap: Bitmap): Uri {
    val file = File(context.cacheDir, "album_art.jpg")
    file.outputStream().use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
        out.flush()
    }
    return Uri.fromFile(file)
}

// Helper function for formatting duration - keep as is
fun formatDuration(durationMs: Long): String {
    val minutes = (durationMs / 1000) / 60
    val seconds = (durationMs / 1000) % 60
    return String.format("%02d:%02d", minutes, seconds)
}