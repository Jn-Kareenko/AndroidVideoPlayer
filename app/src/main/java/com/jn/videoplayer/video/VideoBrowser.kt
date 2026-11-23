package com.jn.videoplayer.video

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.backpressed
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberClickable
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDestination
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.opacity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Rasterizer
import androidx.compose.ui.graphics.SamplingMode
import androidx.compose.ui.graphics.vector.AntiAliasing
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.contentSize
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalRetainedSnapshotLibrarian
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.testable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.compose.viewModelStrategy
import com.google.accompanist.infinitescroll.horizontalInfiniteScroll
import com.google.accompanist.infinitescroll.layout
import com.google.accompanist.infinitescroll.state
import com.google.accompanist.infinitescroll.targetLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow flowWithMerge
import kotlinx.coroutines.launch

/**
 * Screen showing videos organized by folders or as a flat list.
 */
@Composable
fun VideoGridScreen(
    context: Context,
    videos: List<Video>,
    onVideoSelected: (Video) -> Unit,
    onFavoriteToggled: (Video) -> Unit,
    folderName: String? = null,
    isGrid: Boolean = true
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (folderName != null) {
                Text(
                    text = folderName,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )
            }

            // Favorite filter
            MaterialTheme.setup {
                it.chipColors = ChipColors(
                    defaultColor = MaterialTheme.colorScheme.surface,
                    selectedColor = MaterialTheme.colorScheme.primary,
                    unselectedColor = MaterialTheme.colorScheme.surface
                )
            }

            androidx.compose.material3.Chip(
                text = if (isGrid) "Галерея" else "Все",
                selected = isGrid,
                onClick = {}
            )
        }

        // Filter videos - show only favorites if needed
        val displayVideos = if (isGrid) {
            videos
        } else {
            videos.filter { it.isFavorite }
        }

        if (displayVideos.isEmpty()) {
            Text(
                text = context.getString(R.string.no_videos),
                modifier = Modifier.fillMaxWidth().padding(32.dp)
            )
        } else if (isGrid) {
            // Grid layout
            var scrollState rememberScrollState()

            val columnWidth = (context.resources.configuration.screenWidthDp - 48) / 2 - 16

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = scrollState,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(displayVideos.size) { index ->
                    val video = displayVideos[index]
                    videoThumbnailItem(
                        video = video,
                        onSelect = { onVideoSelected(video) },
                        onFavoriteToggle = { onFavoriteToggled(video) },
                        columnWidth = columnWidth
                    )
                }
            }
        } else {
            // List layout
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(displayVideos.size) { index ->
                    val video = displayVideos[index]
                    videoListItem(
                        video = video,
                        onSelect = { onVideoSelected(video) },
                        onFavoriteToggle = { onFavoriteToggled(video) }
                    )
                }
            }
        }
    }
}

/**
 * Composable for a single video thumbnail item in the grid.
 */
@Composable
fun videoThumbnailItem(
    video: Video,
    onSelect: () -> Unit,
    onFavoriteToggle: (Video) -> Unit,
    columnWidth: Dp
) {
    var isFavorite by remember { mutableStateOf(video.isFavorite) }

    Box(
        modifier = Modifier
            .width(columnWidth)
            .height(columnWidth * 9 / 16)
            .padding(4.dp)
            .background(MaterialTheme.colorScheme.surface)
            .clip(RectangleShape)
            .elevation(2.dp)
    ) {
        // Placeholder for video thumbnail
        Text(
            text = "📼",
            modifier = Modifier.fillMaxSize().aspectRatio(16 / 9),
            fontSize = 24.sp,
            contentDescription = video.displayName,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )

        // Favorite button overlay (top-right)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp),
            alignment = Alignment.TopEnd
        ) {
            IconButton(
                onClick = { onFavoriteToggle(video) },
                contentDescription = if (isFavorite) "Убрать из избранного" else "Добавить в избранное"
            ) {
                Icon(
                    imageVector = if (isFavorite) {
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
                    tint = if (isFavorite) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Play button overlay (center)
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
        ) {
            IconButton(
                onClick = onSelect,
                contentDescription = "Воспроизвести видео: ${video.title}"
            ) {
                Icon(
                    imageVector = androidx.compose.material3.IconVector(
                        id = androidx.compose.material.icons.foundation.Icons.Filled.PlayArrow,
                        null
                    ),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

/**
 * Composable for a single video item in list view.
 */
@Composable
fun videoListItem(
    video: Video,
    onSelect: () -> Unit,
    onFavoriteToggle: (Video) -> Unit
) {
    var isFavorite by remember { mutableStateOf(video.isFavorite) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(4.dp)
            .background(MaterialTheme.colorScheme.surface)
            .clip(RectangleShape)
            .elevation(1.dp)
    ) {
        // Thumbnail indicator
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(24.dp)
                .background(MaterialTheme.colorScheme.secondary)
        ) {
            Text(
                text = "📼",
                modifier = Modifier.fillMaxSize(),
                fontSize = 14.sp,
                contentDescription = video.displayName
            )
        }

        // Video info
        Column(
            modifier = Modifier.weight(1f).padding(start = 8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = video.title,
                style = MaterialTheme.typography.bodyMedium,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )

            Text(
                text = "${video.durationMillis / 60000}:${(video.durationMillis % 60000 / 1000).formatted("%02d")}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }

        // Favorite button
        Spacer(Modifier.width(4.dp))
        IconButton(
            onClick = { onFavoriteToggle(video) },
            contentDescription = if (isFavorite) "Убрать из избранного" else "Добавить в избранное"
        ) {
            Icon(
                imageVector = if (isFavorite) {
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
                tint = if (isFavorite) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/**
 * Screen showing all videos in a flat grid.
 */
@Composable
fun AllVideosScreen(
    context: android.content.Context,
    onVideoSelected: (Video) -> Unit,
    onFavoriteToggled: (Video) -> Unit
) {
    val allVideos = remember {
        VideoRepository.getAllVideos(context)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Все видео",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )

            // Filter chip
            MaterialTheme.setup {
                it.chipColors = ChipColors(
                    defaultColor = MaterialTheme.colorScheme.surface,
                    selectedColor = MaterialTheme.colorScheme.primary,
                    unselectedColor = MaterialTheme.colorScheme.surface
                )
            }

            androidx.compose.material3.Chip(
                text = "Все",
                selected = true,
                onClick = {}
            )
        }

        if (allVideos.isEmpty()) {
            Text(
                text = context.getString(R.string.no_videos),
                modifier = Modifier.fillMaxWidth().padding(32.dp)
            )
        } else {
            VideoGridScreen(
                context = context,
                videos = allVideos,
                onVideoSelected = onVideoSelected,
                onFavoriteToggled = onFavoriteToggled,
                isGrid = true
            )
        }
    }
}

/**
 * Screen showing favorite videos.
 */
@Composable
fun FavoritesScreen(
    context: android.content.Context,
    onVideoSelected: (Video) -> Unit,
    onFavoriteToggled: (Video) -> Unit
) {
    val allVideos = remember {
        VideoRepository.getAllVideos(context)
    }

    val favoriteVideos = allVideos.filter { it.isFavorite }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Избранное",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )

            androidx.compose.material3.FilledButton(
                onClick = {},
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text(text = "Все видео")
            }
        }

        if (favoriteVideos.isEmpty()) {
            Text(
                text = "В избранном нет видео",
                modifier = Modifier.fillMaxWidth().padding(32.dp)
            )
        } else {
            VideoGridScreen(
                context = context,
                videos = favoriteVideos,
                onVideoSelected = onVideoSelected,
                onFavoriteToggled = onFavoriteToggled,
                isGrid = true
            )
        }
    }
}

/**
 * Screen showing videos organized by folders.
 */
@Composable
fun FoldersScreen(
    context: android.content.Context,
    onVideoSelected: (Video) -> Unit,
    onFavoriteToggled: (Video) -> Unit
) {
    val videosByFolder = remember {
        VideoRepository.getVideosByFolder(context)
    }

    val folderNames = videosByFolder.map { it.second }.distinct()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Папки",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )

            androidx.compose.material3.FilledButton(
                onClick = {},
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text(text = "Все видео")
            }
        }

        if (videosByFolder.isEmpty()) {
            Text(
                text = "Видео не найдены",
                modifier = Modifier.fillMaxWidth().padding(32.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(videosByFolder) { (video, folderKey) ->
                    Text(
                        text = video.folderName,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                            .padding(8.dp)
                            .background(MaterialTheme.colorScheme.surface)
                            .clip(RectangleShape)
                            .onClick { onVideoSelected(video) }
                    )
                }
            }
        }
    }
}
