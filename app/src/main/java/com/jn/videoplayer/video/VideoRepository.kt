package com.jn.videoplayer.video

import android.content.Context
import android.provider.MediaStore
import java.io.File

data class Video(
    val id: Long,
    val title: String,
    val displayName: String,
    val relativePath: String,
    val absolutePath: String,
    val durationMillis: Long,
    val size: Long,
    val isFavorite: Boolean = false
) {
    val folderName: String
        get = {
            val path = absolutePath
            if (path.lastIndexOf('/') > 0) {
                val folderPath = path.substring(0, path.lastIndexOf('/'))
                folderPath.substring(folderPath.lastIndexOf('/') + 1).takeIf { it.isNotBlank() } ?: "Unknown"
            } else "Unknown"
        }

    val videoFile: File
        get = File(absolutePath)

    companion object {
        val EMPTY = Video(
            id = 0L,
            title = "",
            displayName = "",
            relativePath = "",
            absolutePath = "",
            durationMillis = 0L,
            size = 0L
        )
    }
}

object VideoRepository {
    private val PROJECTION = arrayOf(
        MediaStore.Video.Media._ID,
        MediaStore.Video.Media.TITLE,
        MediaStore.Video.DISPLAY_NAME,
        MediaStore.Video.RELATIVE_PATH,
        MediaStore.Video.DATA,
        MediaStore.Video.DURATION,
        MediaStore.Video.SIZE
    )

    private val SORT_ORDER = MediaStore.Video.Media.DISPLAY_NAME + " COLLATE NOCASE ASC"

    fun getVideosByFolder(context: Context): List<Pair<Video, String>> {
        val result = mutableListOf<Pair<Video, String>>()
        val cursor = context.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            PROJECTION,
            null,
            null,
            SORT_ORDER
        )

        if (cursor != null) {
            try {
                val folderMap = mutableMapOf<String, String>()

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID))
                    val title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE))
                    val displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.DISPLAY_NAME))
                    val relativePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.RELATIVE_PATH))
                    val data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.DATA))
                    val duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.DURATION))
                    val size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.SIZE))

                    val video = Video(
                        id = id,
                        title = title ?: displayName,
                        displayName = displayName,
                        relativePath = relativePath ?: "",
                        absolutePath = data ?: "",
                        durationMillis = duration,
                        size = size
                    )

                    result.add(Pair(video, relativePath ?: ""))

                    val key = relativePath ?: ""
                    if (key.isNotBlank() && !folderMap.containsKey(key)) {
                        folderMap[key] = video.folderName
                    }
                }

                val groupedByFolder = folderMap.entries.associate { key to value } { folderKey ->
                    result.filter { it.second == folderKey }.map { it.first }
                }

                val finalResult = mutableListOf<Pair<Video, String>>()
                for ((folderKey, folderName) in groupedByFolder.entries) {
                    val folderVideos = groupedByFolder[folderKey] ?: emptyList()
                    if (folderVideos.isNotEmpty()) {
                        finalResult.add(Pair(folderVideos.first(), folderName))
                        for (video in folderVideos.dropFirst()) {
                            finalResult.add(Pair(video, folderName))
                        }
                    }
                }

                return finalResult
            } finally {
                cursor.close()
            }
        }

        return result
    }

    fun getAllVideos(context: Context): List<Video> {
        val result = mutableListOf<Video>()
        val cursor = context.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            PROJECTION,
            null,
            null,
            SORT_ORDER
        )

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID))
                    val title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE))
                    val displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.DISPLAY_NAME))
                    val relativePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.RELATIVE_PATH))
                    val data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.DATA))
                    val duration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.DURATION))
                    val size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.SIZE))

                    result.add(Video(
                        id = id,
                        title = title ?: displayName,
                        displayName = displayName,
                        relativePath = relativePath ?: "",
                        absolutePath = data ?: "",
                        durationMillis = duration,
                        size = size
                    ))
                }
            } finally {
                cursor.close()
            }
        }

        return result
    }
}
