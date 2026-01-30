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
import android.provider.MediaStore
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.FULL_DATE_FORMAT
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

private var observerJob: Job? = null

/**
 * Register an observer class that gets callbacks when data identified by a given content URI
 * changes.
 */
fun Context.contentFlowObserver(
    uris: Array<Uri>,
    coroutineContext: CoroutineContext
) = callbackFlow {
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

        mediaOrder.sortMedia(media)
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
    val duration: String? = try {
        getString(getColumnIndexOrThrow(MediaStore.MediaColumns.DURATION))
    } catch (_: Throwable) {
        null
    }

    val expiryTimestamp: Long? = try {
        getLong(getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_EXPIRES))
    } catch (_: Throwable) {
        null
    }

    val (mimeType, contentUri) = SUPPORTED_FILES[Path(path).extension]?.let { (mimeType, _) ->
        Pair(mimeType, MediaStore.Files.getContentUri("external"))
    } ?: run {
        val mimeType: String =
            getString(getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE))

        val contentUri = if (mimeType.contains("image"))
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        else
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI

        Pair(mimeType, contentUri)
    }

    val uri = ContentUris.withAppendedId(contentUri, id)
    val formattedDate = modifiedTimestamp.getDate(FULL_DATE_FORMAT)

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
        fullDate = formattedDate,
        duration = duration,
        mimeType = mimeType,
    )
}

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