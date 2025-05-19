package com.example.videoplayer.presentation_layer.Screens.Videos

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.videoplayer.presentation_layer.Navigation.V_Page2
import com.example.videoplayer.presentation_layer.ViewModels.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.ln
import kotlin.math.pow


@Composable
fun VideoPlayerUI(
    navController: NavHostController,
    viewModel: ViewModel = hiltViewModel(),
    modifier: Modifier
) {
    val isLoading = viewModel.isLoadingVideo.collectAsStateWithLifecycle().value

    LaunchedEffect(true) {
        viewModel.loadVideos()
    }

    val allVideos = viewModel.videoList.collectAsStateWithLifecycle().value



    // Animated state for item appearance
    var animatedItems by remember { mutableStateOf(false) }

    LaunchedEffect(allVideos) {
        if (allVideos.isNotEmpty()) {
            animatedItems = true
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = if (isSystemInDarkTheme()) {
                        listOf(Color(30, 30, 50), Color(20, 20, 40, 180))
                    } else {
                        listOf(Color(230, 240, 250), Color(210, 220, 235, 180))
                    }
                )
            )
    ) {


        // Video list with animations
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(60.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 5.dp
                )
            }
        }
        else if (allVideos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VideoLibrary,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = Color.Gray
                    )

                    Text(
                        text = "No videos found",
                        style = TextStyle(
                            fontSize = 18.sp,
                            color = Color.Gray
                        )
                    )
                }
            }
        }
        else {
            // Add top bar with animation
            var topBarHeight by remember { mutableStateOf(0.dp) }
            val topBarHeightPx = with(LocalDensity.current) { topBarHeight.toPx() }
            val topBarOffsetHeightPx = remember { mutableStateOf(0f) }
            val nestedScrollConnection = remember {
                object : NestedScrollConnection {
                    override fun onPreScroll(
                        available: Offset,
                        source: NestedScrollSource
                    ): Offset {
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
                        .height(60.dp)
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
                    val infiniteTransition =
                        rememberInfiniteTransition(label = "headerAnimations")


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

                    var baseColor = remember { mutableStateOf(Color.White) }
                    var accentColor = remember { mutableStateOf(Color(173, 216, 230)) }
                    if (isSystemInDarkTheme()) {
                        baseColor.value = Color.White
                        accentColor.value = Color(173, 216, 230)
                    } else {
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
                                val baseColor = baseColor.value
                                val accentColor = accentColor.value

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
                        "Your Videos",
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
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)

                ) {
                    itemsIndexed(allVideos) { index, item ->
                        val context = LocalContext.current

                        // Animate items appearing one by one
                        val itemVisibility by animateFloatAsState(
                            targetValue = if (animatedItems) 1f else 0f,
                            animationSpec = tween(
                                durationMillis = 300,
                                delayMillis = index * 100,
                                easing = FastOutSlowInEasing
                            ),
                            label = "itemVisibility"
                        )

                        // Animate on click
                        var isPressed by remember { mutableStateOf(false) }
                        val scale by animateFloatAsState(
                            targetValue = if (isPressed) 0.95f else 1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            label = "cardScale"
                        )

                        val customCoroutine = rememberCoroutineScope()

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .alpha(itemVisibility)
                                .scale(scale)
                                .clickable {
                                    isPressed = true
                                    // Delay navigation to see the animation
                                    customCoroutine.launch {
                                        delay(100)
                                        navController.navigate(V_Page2(item.path))
                                        isPressed = false
                                    }
                                }
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onPress = {
                                            isPressed = true
                                            try {
                                                awaitRelease()
                                            } finally {
                                                isPressed = false
                                            }
                                        }
                                    )
                                },
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = 4.dp,
                                pressedElevation = 8.dp,
                                focusedElevation = 6.dp
                            ),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF1E1E1E)
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                            ) {
                                // Video thumbnail with shimmer effect while loading
                                var isFrameLoading by remember { mutableStateOf(true) }

                                Box(
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    if (isFrameLoading) {
                                        ShimmerEffect(
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }

                                    VideoThumbnail(
                                        context = context,
                                        videoPath = item.path,
                                        onLoaded = { isFrameLoading = false }
                                    )
                                }

                                // Play button with pulsing animation
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF2196F3).copy(alpha = 0.8f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Play video",
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clickable(
                                                onClick = {
                                                    navController.navigate(V_Page2(item.path))
                                                }
                                            ),
                                        tint = Color.White
                                    )
                                }

                                // Video info overlay at bottom
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .fillMaxWidth()
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.Transparent,
                                                    Color.Black.copy(alpha = 0.7f)
                                                )
                                            )
                                        )
                                        .padding(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Video title
                                        Text(
                                            text = item.title ?: "Unknown",
                                            style = TextStyle(
                                                color = Color.White,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f)
                                        )

                                        // Video duration
                                        item.duration?.let {
                                            Text(
                                                text = formatDuration(it.toLong()),
                                                style = TextStyle(
                                                    color = Color.White,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Medium
                                                ),
                                                modifier = Modifier
                                                    .background(
                                                        Color.Black.copy(alpha = 0.6f),
                                                        shape = RoundedCornerShape(4.dp)
                                                    )
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
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
    }
}

@Composable
fun VideoThumbnail(
    context: Context,
    videoPath: String,
    onLoaded: () -> Unit
) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(videoPath) {
        withContext(Dispatchers.IO) {
            val result = extractFrameFromVideo(context, videoPath)
            bitmap = result
            if (result != null) {
                onLoaded()
            }
        }
    }

    bitmap?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = "Video Thumbnail",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun ShimmerEffect(modifier: Modifier = Modifier) {
    val shimmerColors = listOf(
        Color.DarkGray.copy(alpha = 0.6f),
        Color.DarkGray.copy(alpha = 0.2f),
        Color.DarkGray.copy(alpha = 0.6f)
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim.value, y = translateAnim.value)
    )

    Box(
        modifier = modifier
            .background(brush)
    )
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