package com.example.videoplayer.presentation_layer.Screens

import android.Manifest
import android.media.MediaPlayer
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.videoplayer.presentation_layer.Navigation.ControllerPage
import com.example.videoplayer.presentation_layer.ViewModels.ViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale


@Composable
fun App(
    mediaPlayer: MediaPlayer,
    viewModel: ViewModel = hiltViewModel()
) {

    val context = LocalContext.current

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
        RequestPermissionsForHighVersion(  onGranted = {
            viewModel.fetchSongs(context)
            viewModel.loadVideos()
            viewModel.showUi.value = true
        }
        )
    }else{
        RequestPermissionForLowVersion(  onGranted = {
            viewModel.fetchSongs(context)
            viewModel.loadVideos()
            viewModel.showUi.value = true
        }
        )
    }

    val state = viewModel.showUi.collectAsState()

    if (state.value) {
         ControllerPage(mediaPlayer)
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Something wrong!",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSystemInDarkTheme()) Color.White else Color.Black
                )
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestPermissionsForHighVersion(onGranted: () -> Unit) {
    val context = LocalContext.current

    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO
        )
    )

    LaunchedEffect(key1 = permissionsState) {
        permissionsState.launchMultiplePermissionRequest()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = when {
                permissionsState.allPermissionsGranted -> "All permissions granted"
                permissionsState.shouldShowRationale -> "Permissions required for proper functionality"
                else -> "Requesting permissions..."
            },
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = { permissionsState.launchMultiplePermissionRequest() }) {
            Text("Request Permissions")
        }
    }

    if (permissionsState.allPermissionsGranted) {
        onGranted()
    } else if (!permissionsState.shouldShowRationale) {
        Toast.makeText(context, "Permissions denied permanently", Toast.LENGTH_SHORT).show()
    }
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestPermissionForLowVersion(onGranted: () -> Unit) {
    val context = LocalContext.current

    val permissionState = rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE)

    LaunchedEffect(key1 = permissionState) {
        permissionState.launchPermissionRequest()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = when {
                permissionState.status.isGranted -> "Permission Granted"
                permissionState.status.shouldShowRationale -> "Storage permission is required"
                else -> "Requesting permission..."
            },
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = { permissionState.launchPermissionRequest() }) {
            Text("Request Permission")
        }
    }

    if (permissionState.status.isGranted) {
        onGranted()
    } else if (!permissionState.status.shouldShowRationale) {
        Toast.makeText(context, "Permission denied permanently", Toast.LENGTH_SHORT).show()
    }

}
