package com.jn.videoplayer.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.media3.exoplayer.ExoPlayer
import com.google.android.media3.exoplayer.MediaItem
import com.google.android.media3.common.MediaMetrics

class VideoPlayerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VideoPlayerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    VideoPlayerContent()
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

data class VideoPlayerState(
    var player: ExoPlayer? = null,
    var mediaItem: MediaItem? = null,
    var isPlaying = false
)

@Composable
fun VideoPlayerContent(
    video: com.jn.videoplayer.video.Video,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val context = LocalContext.current
    var state by remember { mutableStateOf(VideoPlayerState()) }

    // Initialize player when video changes
    effect {
        if (state.player == null) {
            state = state.copy(
                player = ExoPlayer.Builder(context).build(),
                mediaItem = MediaItem.fromUri(Uri.parse(video.absolutePath))
            )
            state.player?.setMediaItem(state.mediaItem!!)
            state.player?.prepare()
            state.player.playWhenReady = true
        }
    }

    // Listen for playback completion
    LaunchedEffect(state.player) {
        if (state.player != null) {
            state.player?.addListener(object : com.google.android.media3.exoplayer.ExoPlayer.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        com.google.android.media3.exoplayer.ExoPlayer.STATE_ENDED -> {
                            onNext()
                        }
                        com.google.android.media3.exoplayer.ExoPlayer.STATE_READY -> {
                            state.player?.play()
                        }
                    }
                }

                override fun onPlayerError(error: Exception) {
                    Log.e("VideoPlayer", "Player error: ${error.message}")
                }
            })
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.Start
    ) {
        PlayerView(
            modifier = Modifier.fillMaxSize(),
            player = state.player
        )

        // Control bar at bottom
        Surface(
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Previous video
                IconButton(
                    onClick = { /* TODO: previous video */ },
                    contentDescription = "Предыдущее видео"
                ) {
                    Icon(
                        imageVector = androidx.compose.material3.IconVector(
                            id = androidx.compose.material.icons.foundation.Icons.Filled.SkipPrevious,
                            null
                        ),
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Favorite button
                IconButton(
                    onClick = {
                        // Toggle favorite state
                        // TODO: Save to database or shared preferences
                    },
                    contentDescription = if (video.isFavorite) "Убран из избранного" else "Добавить в избранное"
                ) {
                    Icon(
                        imageVector = if (video.isFavorite) {
                            androidx.compose.material3.IconVector(
                                id = androidx.compose.material.icons.foundation.Icons.Filled.Favorite,
                                null
                            )
                        } else {
                            androidx.compose.material3.IconVector(
                                id = androidx.compose.material.icons.foundation.Icons.Filled.FavoriteBorder,
                                null
                            )
                        },
                        tint = if (video.isFavorite) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Next video
                IconButton(
                    onClick = { onNext() },
                    contentDescription = "Следующее видео"
                ) {
                    Icon(
                        imageVector = androidx.compose.material3.IconVector(
                            id = androidx.compose.material.icons.foundation.Icons.Filled.SkipNext,
                            null
                        ),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Progress seek bar
            androidx.compose.slider.Slider(
                value = 0f,
                onValueChange = { /* TODO: seek to position */ },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(16.dp)
            )

            // Time display
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "00:00",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                Text(
                    text = "00:00",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}
