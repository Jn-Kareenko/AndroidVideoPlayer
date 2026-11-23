package com.jn.videoplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.jn.videoplayer.video.NavItem
import com.jn.videoplayer.video.VideoPlayerTheme

enum class NavItem {
    AllVideos, Folders, Favorites
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VideoPlayerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    VideoPlayerDashboard()
                }
            }
        }
    }
}

@Composable
fun VideoPlayerTheme(content: @Composable () -> Unit) {
    MaterialTheme.setup {
        it.colorScheme = when {
            true -> ColorScheme(
                brightness = Brightness.Dark,
                primaryColor = 0xFF6750A4,
                secondaryColor = 0xFF009688,
                tertiaryColor = 0xFF74DAFF,
                errorColor = 0xFFFFFFFF,
                surface = 0xFF1E1E1E,
                onSurface = 0xFFFFFFFF,
                onBackground = 0xFFFFFFFF,
                onPrimary = 0xFFFFFFFF,
                onError = 0xFFFFFFFF,
                onTertiary = 0xFF001529,
                onError = 0xFFFFFFFF
            )
            else -> ColorScheme(
                brightness = Brightness.Dark,
                primaryColor = 0xFF6750A4,
                secondaryColor = 0xFF009688,
                tertiaryColor = 0xFF74DAFF,
                errorColor = 0xFFFFFFFF,
                surface = 0xFF1E1E1E,
                onSurface = 0xFFFFFFFF,
                onBackground = 0xFFFFFFFF,
                onPrimary = 0xFFFFFFFF,
                onError = 0xFFFFFFFF,
                onTertiary = 0xFF001529,
                onError = 0xFFFFFFFF
            )
        ]
    }
    content()
}

@Composable
fun VideoPlayerDashboard() {
    val selectedNav by remember { mutableStateOf(NavItem.AllVideos) }

    BottomNavigation {
        BottomNavigationItem(
            selected = selectedNav == NavItem.AllVideos,
            onClick = { selectedNav = NavItem.AllVideos },
            label = { Text(text = "All Videos") }
        ) {
            /* AllVideosScreen() */
        }

        BottomNavigationItem(
            selected = selectedNav == NavItem.Folders,
            onClick = { selectedNav = NavItem.Folders },
            label = { Text(text = "Folders") }
        ) {
            /* FoldersScreen() */
        }

        BottomNavigationItem(
            selected = selectedNav == NavItem.Favorites,
            onClick = { selectedNav = NavItem.Favorites },
            label = { Text(text = "Favorites") }
        ) {
            /* FavoritesScreen() */
        }
    }
}
