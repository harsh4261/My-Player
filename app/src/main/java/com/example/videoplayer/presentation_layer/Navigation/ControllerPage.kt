package com.example.videoplayer.presentation_layer.Navigation

import android.media.MediaPlayer
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Scaffold
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.outlined.MusicVideo
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.videoplayer.presentation_layer.ViewModels.ViewModel


@Composable
fun ControllerPage(
    mediaPlayer: MediaPlayer,
    viewModel: ViewModel = hiltViewModel()
) {

    val rootNavController = rememberNavController()
    val navBackStack by rootNavController.currentBackStackEntryAsState()

    val showBottomBar = remember { mutableStateOf(true) }


    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        bottomBar = {
            if (showBottomBar.value) {
                NavigationBar(
                     containerColor = if (isSystemInDarkTheme()) Color.Black else Color.White

                ) {
                    items.forEach { items ->

                        val isSelected = items.title.lowercase() == navBackStack?.destination?.route

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                rootNavController.navigate(items.title.lowercase()) {
                                    popUpTo(rootNavController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) items.selectedIcon else items.unselectedIcon,
                                    contentDescription = items.title
                                )
                            },
                            label = {
                                Text(text = items.title)
                            },

                            )

                    }

                }
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = rootNavController,
            startDestination = "video"
        ) {

            composable("video") {
                showBottomBar.value = true // ✅ Show BottomBar on Video main screen
                VideoPlayerNavigation(
                    modifier = Modifier.padding(innerPadding),
                    showBottomBar = showBottomBar
                )
            }

            composable("music") {
                showBottomBar.value = true // ✅ Show BottomBar on Video main screen
                MusicNavigation(
                    modifier = Modifier.padding(innerPadding),
                    mediaPlayer = mediaPlayer,
                    showBottomBar = showBottomBar
                )
            }
        }

    }
}

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

val items = listOf(
    BottomNavigationItem(
        title = "Video",
        selectedIcon = Icons.Filled.PlayCircleFilled,
        unselectedIcon = Icons.Outlined.PlayCircleOutline,
    ),
    BottomNavigationItem(
        title = "Music",
        selectedIcon = Icons.Filled.LibraryMusic,
        unselectedIcon = Icons.Outlined.MusicVideo,
    ),
)


