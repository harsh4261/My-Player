package com.example.videoplayer.presentation_layer.Screens

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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil3.compose.rememberAsyncImagePainter
import com.example.videoplayer.R
import com.example.videoplayer.data_layer.models.Song
import com.example.videoplayer.presentation_layer.ViewModels.ViewModel
import kotlinx.coroutines.delay
import java.io.File


@Composable
fun SongListScreen(
    navController: NavHostController,
    viewModel: ViewModel = hiltViewModel(),
    modifier: Modifier
) {
    val context = LocalContext.current
    var songs by remember { mutableStateOf(emptyList<Song>()) }

    LaunchedEffect(true) {
        viewModel.fetchSongs(context)
    }

    songs = viewModel.songList.collectAsState().value

    if (songs.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No Songs Found", textAlign = TextAlign.Center)
        }
    } else {
        SongList(
            songs = songs,
            modifier = modifier,
            onPlay = { song ->
                // Navigate to the Player Screen with encoded song details
                val index = songs.indexOf(song)
                navController.navigate(
                    "player/${Uri.encode(song.title)}/${Uri.encode(song.artist)}/${
                        Uri.encode(song.duration)
                    }/${Uri.encode(song.filePath)}/$index"
                )
            }

        )
    }

}

@Composable
fun SongList(
    songs: List<Song>,
    onPlay: (Song) -> Unit,
    modifier: Modifier,
    viewModel: ViewModel = hiltViewModel()
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(if (isSystemInDarkTheme()) Color.Black else Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        items(songs) { song ->

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 10.dp,
                    pressedElevation = 15.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSystemInDarkTheme()) {
                        Color(37, 35, 35, 255)
                    } else {
                        Color(238, 238, 238, 255)
                    }
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            onPlay(song)
                        }
                ) {
                    Box(
                        modifier = Modifier
                            .padding(10.dp)
                            .height(60.dp)
                            .width(60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.QueueMusic,
                            contentDescription = "Music Logo",
                            modifier = Modifier
                                .size(30.dp),
                            tint = if (isSystemInDarkTheme()) Color.White else Color.Black
                        )
                    }

                    Spacer(Modifier.width(20.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.7f),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center
                    ) {
                        song.title?.let {
                            Text(
                                text = it,
                                style = TextStyle(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSystemInDarkTheme()) Color.White else Color.Black
                                ),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )

                        }

                        Spacer(Modifier.height(10.dp))

                        song.artist?.let {

                            Text(
                                text = "Artist  - $it",
                                style = TextStyle(
                                    color = if (isSystemInDarkTheme()) Color.White else Color.Black
                                )
                            )
                        }

                    }

                    Spacer(Modifier.width(10.dp))

                    Column(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {

                        song.duration?.let {
                            Text(
                                text = formatDuration(it.toLong()),
                                style = TextStyle(

                                    color = if (isSystemInDarkTheme()) Color.White else Color.Black
                                )
                            )
                        }

                    }

                }
            }
        }
    }
}


@Composable
fun PlayerScreen(
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


    var isPlaying by remember { mutableStateOf(false) }
    var albumArtUri by remember { mutableStateOf<String?>(null) }
    var progress by remember { mutableFloatStateOf(0f) }
    val currentPosition = remember { mutableFloatStateOf(0f) }
    val sliderPosition = remember { mutableFloatStateOf(0f) }
    var totalDuration = duration.toFloat()
    // Use a local variable for songs
    LaunchedEffect(true) {
        viewModel.fetchSongs(context)
    }
    val songs = viewModel.songList.collectAsState().value


    LaunchedEffect(filePath) {
        playSong(context, mediaPlayer, filePath) {
            isPlaying = false
        }
        albumArtUri = getAlbumArtUri(context, filePath)
        isPlaying = true

        totalDuration = mediaPlayer.duration.toFloat() // Get the duration of the song
        progress = 0f // Reset progress when the song changes

        // Update progress every second while playing
        while (isPlaying) {
            delay(1000) // Update every second
            progress = mediaPlayer.currentPosition.toFloat() / totalDuration // Calculate progress
            currentPosition.floatValue = mediaPlayer.currentPosition.toFloat()
            sliderPosition.floatValue = currentPosition.floatValue
        }
    }
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.Cyan),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.6f)
                .padding(16.dp)
                .systemBarsPadding(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {

                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back to Page 1",
                    modifier = Modifier
                        .size(30.dp)
                        .clickable {
                            navController.navigate("song_list")
                        },
                    tint = Color.Black
                )

                Spacer(Modifier.width(20.dp))

                Text(
                    text = "Now Playing",
                    style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    ),
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                )
            }


            // Display album art
            if (albumArtUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(model = albumArtUri),
                    contentDescription = "Album Art",
                    modifier = Modifier
                        .size(360.dp)
                        .align(Alignment.CenterHorizontally)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.video_player_app_logo),
                    contentDescription = "Album Art",
                    modifier = Modifier
                        .size(360.dp)
                        .align(Alignment.CenterHorizontally)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Text(
                text = title,
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSystemInDarkTheme()) Color.White else Color.Black
                )
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = artist,
                style = TextStyle(
                    fontSize = 16.sp,
                    color = if (isSystemInDarkTheme()) Color.White else Color.Black
                ),
            )



            TrackSlider(
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

            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = formatDuration(currentPosition.floatValue.toLong()),
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    color = Color.Black,
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )

                val remainTime = totalDuration - currentPosition.floatValue
                Text(
                    text = if (remainTime >= 0) formatDuration(remainTime.toLong()) else "",
                    modifier = Modifier
                        .padding(8.dp),
                    color = Color.Black,
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(Modifier.height(10.dp))

            // Control Buttons
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                IconButton(onClick = {
                    // Previous song
                    val prevIndex = if (currentIndex > 0) currentIndex - 1 else songs.size - 1
                    val prevSong = songs[prevIndex]
                    navController.navigate(
                        "player/${Uri.encode(prevSong.title)}/${Uri.encode(prevSong.artist)}/${
                            Uri.encode(
                                prevSong.duration
                            )
                        }/${Uri.encode(prevSong.filePath)}/$prevIndex"
                    )
                }) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "previous",
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                IconButton(onClick = {
                    if (isPlaying) {
                        mediaPlayer.pause()
                        isPlaying = false
                    } else {
                        mediaPlayer.start()
                        isPlaying = true
                    }
                }) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = "Play/Pause",
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(100.dp)
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))


                IconButton(onClick = {
                    // Next song
                    val nextIndex = (currentIndex + 1) % songs.size
                    val nextSong = songs[nextIndex]
                    navController.navigate(
                        "player/${Uri.encode(nextSong.title)}/${Uri.encode(nextSong.artist)}/${
                            Uri.encode(
                                nextSong.duration
                            )
                        }/${Uri.encode(nextSong.filePath)}/$nextIndex"
                    )
                }) {
                    Icon(
                        imageVector = Icons.Filled.SkipNext,
                        contentDescription = "next",
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(40.dp)
                    )
                }
            }
        }
    }
}

// Helper functions (playSong, getAlbumArtUri, etc.) remain unchanged.
fun playSong(
    context: Context,
    mediaPlayer: MediaPlayer,
    filePath: String,
    onCompletion: () -> Unit
) {
    try {
        Log.d("MediaPlayer", "Playing song with filePath: $filePath")
        mediaPlayer.reset()  // Stop the current song if playing
        mediaPlayer.setDataSource(filePath)  // Check filePath here
        mediaPlayer.prepare()
        mediaPlayer.start()

        mediaPlayer.setOnCompletionListener {
            onCompletion()  // When the song completes, trigger the callback
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Error playing song: ${e.message}", Toast.LENGTH_SHORT).show()
    }
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

fun formatDuration(durationMs: Long): String {
    val minutes = (durationMs / 1000) / 60
    val seconds = (durationMs / 1000) % 60
    return String.format("%02d:%02d", minutes, seconds)
}

@Composable
fun formatDurationForVideo(durationMs: String): String {
    return try {
        val duration = durationMs.toLong()
        val minutes = (duration / 1000) / 60
        val seconds = (duration / 1000) % 60
        String.format("%02d:%02d", minutes, seconds)
    } catch (e: Exception) {
        "00:00" // Default if conversion fails
    }
}

@Composable
fun TrackSlider(
    value: Float,
    onValueChange: (newValue: Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    songDuration: Float
) {
    Slider(
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        onValueChangeFinished = {
            onValueChangeFinished()
        },
        valueRange = 0f..songDuration,
        colors = SliderDefaults.colors(
            thumbColor = Color.Green,
            activeTrackColor = Color.White,
            inactiveTrackColor = Color.DarkGray,
        )
    )
}