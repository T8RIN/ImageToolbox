/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package com.t8rin.imagetoolbox.feature.media_picker.data.utils

import android.content.ContentResolver
import android.content.ContentUris
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.Album
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.MediaOrder
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.OrderType
import com.t8rin.logger.makeLog
import kotlinx.coroutines.coroutineScope

@RequiresApi(26)
sealed class Query(
    var projection: Array<String>,
    var bundle: Bundle? = null
) {
    class MediaQuery : Query(
        projection = listOfNotNull(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.DATA,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.MediaColumns.RELATIVE_PATH
            } else MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.BUCKET_ID,
            MediaStore.MediaColumns.DATE_MODIFIED,
            MediaStore.MediaColumns.DATE_TAKEN,
            MediaStore.MediaColumns.BUCKET_DISPLAY_NAME,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.MediaColumns.DURATION
            } else null,
            MediaStore.MediaColumns.MIME_TYPE
        ).toTypedArray(),
    )

    class PhotoQuery : Query(
        projection = listOfNotNull(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.DATA,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.MediaColumns.RELATIVE_PATH
            } else MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.BUCKET_ID,
            MediaStore.MediaColumns.DATE_MODIFIED,
            MediaStore.MediaColumns.DATE_TAKEN,
            MediaStore.MediaColumns.BUCKET_DISPLAY_NAME,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.MediaColumns.DURATION
            } else null,
            MediaStore.MediaColumns.MIME_TYPE
        ).toTypedArray(),
        bundle = defaultBundle.apply {
            putString(
                ContentResolver.QUERY_ARG_SQL_SELECTION,
                MediaStore.MediaColumns.MIME_TYPE + " like ?"
            )
            putStringArray(
                ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS,
                arrayOf("image%")
            )
        }
    )

    class VideoQuery : Query(
        projection = listOfNotNull(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.DATA,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.MediaColumns.RELATIVE_PATH
            } else MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.BUCKET_ID,
            MediaStore.MediaColumns.DATE_MODIFIED,
            MediaStore.MediaColumns.BUCKET_DISPLAY_NAME,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.MediaColumns.DURATION
            } else null,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.ORIENTATION
        ).toTypedArray(),
        bundle = defaultBundle.apply {
            putString(
                ContentResolver.QUERY_ARG_SQL_SELECTION,
                MediaStore.MediaColumns.MIME_TYPE + " LIKE ?"
            )
            putStringArray(
                ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS,
                arrayOf("video%")
            )
        }
    )

    class AlbumQuery : Query(
        projection = arrayOf(
            MediaStore.MediaColumns.BUCKET_ID,
            MediaStore.MediaColumns.BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.DATA,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.MediaColumns.RELATIVE_PATH
            } else MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.DATE_MODIFIED,
            MediaStore.MediaColumns.DATE_TAKEN
        )
    )

    class FileQuery(fileExtensions: List<String>) : Query(
        projection = listOfNotNull(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.DATA,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.MediaColumns.RELATIVE_PATH
            } else MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.BUCKET_ID,
            MediaStore.MediaColumns.DATE_MODIFIED,
            MediaStore.MediaColumns.DATE_TAKEN,
            MediaStore.MediaColumns.BUCKET_DISPLAY_NAME,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.MediaColumns.DURATION
            } else null,
            MediaStore.MediaColumns.MIME_TYPE
        ).toTypedArray(),
        bundle = defaultBundle.deepCopy().apply {
            putString(
                ContentResolver.QUERY_ARG_SQL_SELECTION,
                fileExtensions.indices.asSequence().map { "${MediaStore.MediaColumns.DATA} like ?" }
                    .joinToString(" OR ")
            )
            putStringArray(
                ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS,
                fileExtensions.toTypedArray()
            )
        }
    )

    fun copy(
        projection: Array<String> = this.projection,
        bundle: Bundle? = this.bundle,
    ): Query {
        this.projection = projection
        this.bundle = bundle
        return this
    }

    companion object {
        val defaultBundle = Bundle().apply {
            putStringArray(
                ContentResolver.QUERY_ARG_SORT_COLUMNS,
                arrayOf(MediaStore.MediaColumns.DATE_MODIFIED)
            )
            putInt(
                ContentResolver.QUERY_ARG_SQL_SORT_ORDER,
                ContentResolver.QUERY_SORT_DIRECTION_DESCENDING
            )
        }
    }

}

@RequiresApi(26)
private fun Query.copyAsAlbum(): Query {
    val bundle = this.bundle ?: Bundle()

    return this.copy(
        bundle = bundle.apply {
            putInt(
                ContentResolver.QUERY_ARG_SORT_DIRECTION,
                ContentResolver.QUERY_SORT_DIRECTION_DESCENDING
            )
            putStringArray(
                ContentResolver.QUERY_ARG_SORT_COLUMNS,
                arrayOf(MediaStore.MediaColumns.DATE_MODIFIED)
            )
        }
    )
}

@RequiresApi(26)
suspend fun ContentResolver.getAlbums(
    query: Query = Query.AlbumQuery(),
    fileQuery: Query = Query.AlbumQuery(),
    mediaOrder: MediaOrder = MediaOrder.Date(OrderType.Descending)
): List<Album> = coroutineScope {
    val timeStart = System.currentTimeMillis()
    val albums = mutableListOf<Album>()
    val albumQuery = query.copyAsAlbum()
    val albumFileQuery = fileQuery.copyAsAlbum()

    query(
        mediaQuery = albumQuery,
        fileQuery = albumFileQuery
    ).use {
        with(it) {
            while (moveToNext()) {
                runCatching {
                    val albumId =
                        getLong(getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_ID))
                    val id = getLong(getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                    val label: String? = try {
                        getString(
                            getColumnIndexOrThrow(
                                MediaStore.MediaColumns.BUCKET_DISPLAY_NAME
                            )
                        )
                    } catch (_: Throwable) {
                        Build.MODEL
                    }
                    val thumbnailPath =
                        getString(getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))
                    val thumbnailRelativePath =
                        getString(
                            getColumnIndexOrThrow(
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    MediaStore.MediaColumns.RELATIVE_PATH
                                } else MediaStore.MediaColumns.DATA
                            )
                        )
                    val thumbnailDate =
                        getLong(getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED))
                    val mimeType =
                        getString(getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE))
                    val contentUri = if (mimeType.contains("image"))
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    else
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    val album = Album(
                        id = albumId,
                        label = label ?: Build.MODEL,
                        uri = ContentUris.withAppendedId(contentUri, id).toString(),
                        pathToThumbnail = thumbnailPath,
                        relativePath = thumbnailRelativePath,
                        timestamp = thumbnailDate,
                        count = 1
                    )
                    val currentAlbum = albums.find { a -> a.id == albumId }
                    if (currentAlbum == null)
                        albums.add(album)
                    else {
                        val i = albums.indexOf(currentAlbum)
                        albums[i] = albums[i].let { a -> a.copy(count = a.count + 1) }
                        if (albums[i].timestamp <= thumbnailDate) {
                            albums[i] = album.copy(count = albums[i].count)
                        }
                    }
                }
            }
        }
    }

    mediaOrder.sortAlbums(albums).also {
        "Album parsing took: ${System.currentTimeMillis() - timeStart}ms".makeLog()
    }
}