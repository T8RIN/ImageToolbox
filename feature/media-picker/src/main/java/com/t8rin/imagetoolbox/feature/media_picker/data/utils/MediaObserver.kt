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
import android.content.Context
import android.database.ContentObserver
import android.database.Cursor
import android.database.MergeCursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.Album
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.Media
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.MediaOrder
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.OrderType
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.io.path.Path
import kotlin.io.path.extension

/**
 * Register an observer class that gets callbacks when data identified by a given content URI
 * changes.
 */
fun Context.contentFlowObserver(
    uris: Array<Uri>,
    coroutineContext: CoroutineContext
) = callbackFlow {
    var observerJob: Job? = null
    val observer = object : ContentObserver(null) {
        override fun onChange(selfChange: Boolean) {
            observerJob?.cancel()
            observerJob = launch(coroutineContext) {
                send(false)
            }
        }
    }
    for (uri in uris) {
        contentResolver.registerContentObserver(uri, true, observer)
    }
    // trigger first.
    observerJob = launch(coroutineContext) {
        send(true)
    }
    awaitClose {
        contentResolver.unregisterContentObserver(observer)
    }
}.conflate().onEach { if (!it) delay(1000) }

suspend fun ContentResolver.getMedia(
    mediaQuery: Query = Query.MediaQuery(),
    fileQuery: Query = Query.MediaQuery(),
    mediaOrder: MediaOrder = MediaOrder.Date(OrderType.Descending)
): List<Media> {
    return coroutineScope {
        val media = mutableListOf<Media>()
        query(mediaQuery, fileQuery).use { cursor ->
            while (cursor.moveToNext()) {
                try {
                    media.add(cursor.getMediaFromCursor())
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }

        mediaOrder.sortMedia(media.distinctBy(Media::uri))
    }
}


fun Cursor.getMediaFromCursor(): Media {
    val id: Long =
        getLong(getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
    val path: String =
        getString(getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))
    val relativePath: String =
        getString(
            getColumnIndexOrThrow(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.MediaColumns.RELATIVE_PATH
                } else MediaStore.MediaColumns.DATA
            )
        )
    val title: String =
        getString(getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME))
    val albumID: Long =
        getLong(getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_ID))
    val albumLabel: String = try {
        getString(
            getColumnIndexOrThrow(
                MediaStore.MediaColumns.BUCKET_DISPLAY_NAME
            )
        )
    } catch (_: Throwable) {
        Build.MODEL
    }
    val takenTimestamp: Long? = try {
        getLong(getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_TAKEN))
    } catch (_: Throwable) {
        null
    }
    val modifiedTimestamp: Long =
        getLong(getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED))

    val expiryTimestamp: Long? = try {
        getLong(getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_EXPIRES))
    } catch (_: Throwable) {
        null
    }
    val width: Int? = getPositiveIntOrNull(MediaStore.MediaColumns.WIDTH)
    val height: Int? = getPositiveIntOrNull(MediaStore.MediaColumns.HEIGHT)

    val mimeType = SUPPORTED_FILES[Path(path).extension.lowercase()]?.first
        ?: getString(getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)).orEmpty()

    val uri = mediaStoreItemUri(id, mimeType, path)

    return Media(
        id = id,
        label = title,
        uri = uri.toString(),
        path = path,
        relativePath = relativePath,
        albumID = albumID,
        albumLabel = albumLabel,
        timestamp = modifiedTimestamp,
        takenTimestamp = takenTimestamp,
        expiryTimestamp = expiryTimestamp,
        mimeType = mimeType,
        width = width,
        height = height,
        isFavorite = getOptionalInt(MediaStore.MediaColumns.IS_FAVORITE) == 1,
        isTrashed = getOptionalInt(MediaStore.MediaColumns.IS_TRASHED) == 1,
        volumeName = getOptionalString(MediaStore.MediaColumns.VOLUME_NAME) ?: "external",
        size = getOptionalLong(MediaStore.MediaColumns.SIZE),
        generationModified = getOptionalLong(MediaStore.MediaColumns.GENERATION_MODIFIED)
    )
}

private fun Cursor.getOptionalLong(column: String): Long? =
    getColumnIndex(column).takeIf { it != -1 && !isNull(it) }?.let(::getLong)

private fun Cursor.getOptionalInt(column: String): Int =
    getColumnIndex(column).takeIf { it >= 0 && !isNull(it) }?.let(::getInt) ?: 0

private fun Cursor.getOptionalString(column: String): String? =
    getColumnIndex(column).takeIf { it >= 0 && !isNull(it) }?.let(::getString)

fun Cursor.mediaStoreItemUri(id: Long, mimeType: String, path: String): Uri {
    val volume = getOptionalString(MediaStore.MediaColumns.VOLUME_NAME) ?: "external"
    val collection = when {
        Path(path).extension.lowercase() in SUPPORTED_FILES -> MediaStore.Files.getContentUri(volume)
        mimeType.startsWith("image/") -> MediaStore.Images.Media.getContentUri(volume)
        mimeType.startsWith("video/") -> MediaStore.Video.Media.getContentUri(volume)
        else -> MediaStore.Files.getContentUri(volume)
    }
    return ContentUris.withAppendedId(collection, id)
}

fun Query.forAlbum(albumId: Long = Album.ALL_ID): Query {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return this
    val args = bundle?.deepCopy() ?: Bundle()
    args.putInt(
        MediaStore.QUERY_ARG_MATCH_TRASHED,
        if (albumId == Album.TRASH_ID) MediaStore.MATCH_ONLY else MediaStore.MATCH_EXCLUDE
    )
    if (albumId == Album.FAVORITES_ID) {
        val selection = args.getString(ContentResolver.QUERY_ARG_SQL_SELECTION)
        args.putString(
            ContentResolver.QUERY_ARG_SQL_SELECTION,
            listOfNotNull(selection?.let { "($it)" }, "${MediaStore.MediaColumns.IS_FAVORITE}=1")
                .joinToString(" AND ")
        )
    }
    return copy(bundle = args)
}

private fun Cursor.getPositiveIntOrNull(column: String): Int? = runCatching {
    val index = getColumnIndex(column)
    if (index != -1 && !isNull(index)) getInt(index).takeIf { it > 0 } else null
}.getOrNull()

suspend fun ContentResolver.query(
    mediaQuery: Query,
    fileQuery: Query
): Cursor = coroutineScope {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        MergeCursor(
            arrayOf(
                query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    mediaQuery.projection,
                    mediaQuery.bundle,
                    null
                ),
                query(
                    MediaStore.Files.getContentUri("external"),
                    fileQuery.projection,
                    fileQuery.bundle,
                    null
                ),
                query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    mediaQuery.projection,
                    mediaQuery.bundle,
                    null
                )
            )
        )
    } else {
        MergeCursor(
            arrayOf(
                query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    mediaQuery.projection,
                    null,
                    null,
                    null
                ),
                query(
                    MediaStore.Files.getContentUri("external"),
                    fileQuery.projection,
                    null,
                    null,
                    null
                ),
                query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    mediaQuery.projection,
                    null,
                    null,
                    null
                )
            )
        )
    }
}