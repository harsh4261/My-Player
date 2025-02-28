package com.example.videoplayer.presentation_layer.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.videoplayer.presentation_layer.Navigation.V_Page1
import com.example.videoplayer.presentation_layer.Navigation.V_Page2
import com.example.videoplayer.presentation_layer.ViewModels.ViewModel
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

@Composable
fun VideoPlayerPage(
    navController: NavHostController,
    modifier: Modifier,
    viewModel: ViewModel = hiltViewModel()
) {

    LaunchedEffect(true) {
        viewModel.loadVideos()
    }


    val allVideoes = viewModel.videoList.collectAsState().value
    Log.d("allVideo112", "$allVideoes")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(if (isSystemInDarkTheme()) Color.Black else Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {

        items(allVideoes) { item ->
            val context = LocalContext.current

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 10.dp,
                    pressedElevation = 15.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor =  Color(252,244,236)
                )
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {

                    VideoFrameExtractor(context, item.path)

                    item.duration?.let {
                        Text(
                            text = formatDuration(it.toLong()),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp)
                                .background(
                                    Color.Black.copy(alpha = 0.6f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            style = TextStyle(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Icon(
                        imageVector = Icons.Filled.PlayCircle,
                        contentDescription = "Play button",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(60.dp)
                            .clickable {
                                navController.navigate(V_Page2(item.path))
                            },
                        tint = Color.White
                    )
                }
            }
        }
    }

}




@Composable
fun VideoFrameExtractor(context: Context, videoPath: String) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(videoPath) {
        bitmap = extractFrameFromVideo(context, videoPath)
    }

    bitmap?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = "Video Frame",
            modifier = Modifier.fillMaxWidth().fillMaxHeight(),
            contentScale = ContentScale.Crop
        )
    }
}

fun extractFrameFromVideo(context: Context, videoPath: String): Bitmap? {
    return try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, Uri.parse(videoPath))
        retriever.getFrameAtTime(1000000) // Extract frame at 1 second (1000000 microseconds)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

