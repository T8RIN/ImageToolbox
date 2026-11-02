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

package com.t8rin.imagetoolbox.feature.media_picker.data

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.feature.media_picker.domain.MediaActionsManager
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.Media
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.MediaAction
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.MediaActionResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class AndroidMediaActionsManager @Inject constructor(
    @ApplicationContext private val context: Context,
    dispatchersHolder: DispatchersHolder
) : MediaActionsManager, DispatchersHolder by dispatchersHolder {

    override val isAvailable: Boolean
        @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.R)
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

    override suspend fun perform(
        media: List<Media>,
        action: MediaAction
    ): MediaActionResult = withContext(ioDispatcher) {
        val items = media.distinctBy(Media::uri)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            return@withContext MediaActionResult(emptySet(), items.mapTo(hashSetOf(), Media::uri))
        }
        require(items.size <= MediaActionsManager.BATCH_SIZE)
        val successful = linkedSetOf<String>()
        val failed = linkedSetOf<String>()
        val requests = linkedMapOf<String, Uri>()
        for (item in items) {
            currentCoroutineContext().ensureActive()
            val uri = item.uri.toUri()
            if (uri.scheme != ContentResolver.SCHEME_CONTENT || uri.authority != MediaStore.AUTHORITY) {
                failed += item.uri
                continue
            }
            val before = readState(uri)
            if (before.isSuccess && matches(before.getOrNull(), action)) {
                successful += item.uri
                continue
            }
            if (action == MediaAction.Delete && before.getOrNull()?.trashed != true) {
                failed += item.uri
                continue
            }
            val changed = try {
                if (action == MediaAction.Delete) {
                    context.contentResolver.delete(uri, includeTrashed().apply {
                        putString(
                            ContentResolver.QUERY_ARG_SQL_SELECTION,
                            "${MediaStore.MediaColumns.IS_TRASHED}=1"
                        )
                    })
                } else {
                    val values = ContentValues().apply {
                        when (action) {
                            MediaAction.Favorite, MediaAction.Unfavorite ->
                                put(
                                    MediaStore.MediaColumns.IS_FAVORITE,
                                    if (action == MediaAction.Favorite) 1 else 0
                                )

                            MediaAction.Trash, MediaAction.Restore ->
                                put(
                                    MediaStore.MediaColumns.IS_TRASHED,
                                    if (action == MediaAction.Trash) 1 else 0
                                )

                            MediaAction.Delete -> Unit
                        }
                    }
                    context.contentResolver.update(uri, values, includeTrashed())
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: SecurityException) {
                0
            } catch (_: Exception) {
                failed += item.uri
                continue
            }
            val after = readState(uri)
            if (changed > 0 && after.isSuccess && matches(after.getOrNull(), action)) {
                successful += item.uri
            } else {
                val requestUri = item.requestUri()
                if (requestUri != null) requests[item.uri] = requestUri
                else failed += item.uri
            }
        }
        val sender = if (requests.isEmpty()) null else try {
            val uris = requests.values.distinct()
            when (action) {
                MediaAction.Favorite, MediaAction.Unfavorite -> MediaStore.createFavoriteRequest(
                    context.contentResolver, uris, action == MediaAction.Favorite
                )

                MediaAction.Trash, MediaAction.Restore -> MediaStore.createTrashRequest(
                    context.contentResolver, uris, action == MediaAction.Trash
                )

                MediaAction.Delete -> MediaStore.createDeleteRequest(context.contentResolver, uris)
            }.intentSender
        } catch (exception: CancellationException) {
            throw exception
        } catch (_: Exception) {
            failed += requests.keys
            null
        }
        MediaActionResult(
            successfulUris = successful,
            failedUris = failed,
            permissionRequest = sender,
            permissionUris = if (sender != null) requests.keys.toSet() else emptySet()
        )
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun includeTrashed() = Bundle().apply {
        putInt(MediaStore.QUERY_ARG_MATCH_TRASHED, MediaStore.MATCH_INCLUDE)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun readState(uri: Uri): Result<MediaFlags?> = runCatching {
        context.contentResolver.query(
            uri,
            arrayOf(MediaStore.MediaColumns.IS_FAVORITE, MediaStore.MediaColumns.IS_TRASHED),
            includeTrashed(),
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) MediaFlags(cursor.getInt(0) == 1, cursor.getInt(1) == 1)
            else null
        }
    }

    private fun matches(flags: MediaFlags?, action: MediaAction): Boolean = when (action) {
        MediaAction.Favorite -> flags?.favorite == true && !flags.trashed
        MediaAction.Unfavorite -> flags?.favorite == false
        MediaAction.Trash -> flags?.trashed == true
        MediaAction.Restore -> flags?.trashed == false
        MediaAction.Delete -> flags == null
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun Media.requestUri(): Uri? {
        val uri = uri.toUri()
        if (uri.pathSegments.getOrNull(1) in setOf("images", "video")) return uri
        val mediaType = runCatching {
            context.contentResolver.query(
                uri, arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE), includeTrashed(), null
            )?.use { if (it.moveToFirst()) it.getInt(0) else null }
        }.getOrNull()
        val collection = when (mediaType) {
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE -> MediaStore.Images.Media.getContentUri(
                volumeName
            )

            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO -> MediaStore.Video.Media.getContentUri(
                volumeName
            )

            else -> return null
        }
        return ContentUris.withAppendedId(collection, id)
    }

    private data class MediaFlags(val favorite: Boolean, val trashed: Boolean)
}