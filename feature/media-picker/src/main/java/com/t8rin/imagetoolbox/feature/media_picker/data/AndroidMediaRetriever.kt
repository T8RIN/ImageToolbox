/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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
import android.content.Context
import android.os.Bundle
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.feature.media_picker.data.utils.Query
import com.t8rin.imagetoolbox.feature.media_picker.data.utils.contentFlowObserver
import com.t8rin.imagetoolbox.feature.media_picker.data.utils.getAlbums
import com.t8rin.imagetoolbox.feature.media_picker.data.utils.getMedia
import com.t8rin.imagetoolbox.feature.media_picker.data.utils.getSupportedFileSequence
import com.t8rin.imagetoolbox.feature.media_picker.domain.MediaRetriever
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.Album
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.AllowedMedia
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.Media
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.MediaOrder
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.OrderType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@RequiresApi(26)
internal class AndroidMediaRetriever @Inject constructor(
    @ApplicationContext private val context: Context,
    dispatchersHolder: DispatchersHolder
) : DispatchersHolder by dispatchersHolder, MediaRetriever {

    override fun getAlbumsWithType(
        allowedMedia: AllowedMedia
    ): Flow<Result<List<Album>>> = context.retrieveAlbums {
        val query = Query.AlbumQuery().copy(
            bundle = Bundle().apply {
                val mimeType = when (allowedMedia) {
                    is AllowedMedia.Photos -> "image%"
                    AllowedMedia.Videos -> "video%"
                    AllowedMedia.Both -> "%/%"
                }
                putString(
                    ContentResolver.QUERY_ARG_SQL_SELECTION,
                    MediaStore.MediaColumns.MIME_TYPE + " like ?"
                )
                putStringArray(
                    ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS,
                    arrayOf(mimeType)
                )
            }
        )
        val fileQuery = Query.AlbumQuery().copy(
            bundle = Bundle().apply {
                val extensions = getSupportedFileSequence(allowedMedia).toList()
                putString(
                    ContentResolver.QUERY_ARG_SQL_SELECTION,
                    extensions.asSequence().map { MediaStore.MediaColumns.DATA + " LIKE ?" }
                        .joinToString(" OR ")
                )
                putStringArray(
                    ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS,
                    extensions.toTypedArray()
                )
            }
        )
        it.getAlbums(
            query = query,
            fileQuery = fileQuery,
            mediaOrder = MediaOrder.Date(OrderType.Descending)
        )
    }

    override fun mediaFlowWithType(
        albumId: Long,
        allowedMedia: AllowedMedia
    ): Flow<Result<List<Media>>> = if (albumId != -1L) {
        getMediaByAlbumIdWithType(albumId, allowedMedia)
    } else {
        getMediaByType(allowedMedia)
    }.flowOn(defaultDispatcher).conflate()

    override fun getMediaByAlbumIdWithType(
        albumId: Long,
        allowedMedia: AllowedMedia
    ): Flow<Result<List<Media>>> = context.retrieveMedia {
        val query = Query.MediaQuery().copy(
            bundle = Bundle().apply {
                val mimeType = when (allowedMedia) {
                    is AllowedMedia.Photos -> "image%"
                    AllowedMedia.Videos -> "video%"
                    AllowedMedia.Both -> "%/%"
                }
                putString(
                    ContentResolver.QUERY_ARG_SQL_SELECTION,
                    MediaStore.MediaColumns.BUCKET_ID + "= ? and (" + MediaStore.MediaColumns.MIME_TYPE + " like ? OR ${MediaStore.MediaColumns.DATA} LIKE ? OR ${MediaStore.MediaColumns.DATA} LIKE ?)"
                )
                putStringArray(
                    ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS,
                    arrayOf(albumId.toString(), mimeType, "%.jxl", "%.qoi")
                )
            }
        )
        val fileQuery = Query.MediaQuery().copy(
            bundle = Bundle().apply {
                val extensions = getSupportedFileSequence(allowedMedia).toList()

                putString(
                    ContentResolver.QUERY_ARG_SQL_SELECTION,
                    MediaStore.MediaColumns.BUCKET_ID + "= ? and ("
                            + extensions.asSequence()
                        .map { MediaStore.MediaColumns.DATA + " LIKE ?" }
                        .joinToString(" OR ")
                            + ")"
                )
                putStringArray(
                    ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS,
                    arrayOf(albumId.toString(), *extensions.toTypedArray())
                )
            }
        )
        /** return@retrieveMedia */
        it.getMedia(
            mediaQuery = query,
            fileQuery = fileQuery
        )
    }

    override fun getMediaByType(
        allowedMedia: AllowedMedia
    ): Flow<Result<List<Media>>> = context.retrieveMedia {
        val query = when (allowedMedia) {
            is AllowedMedia.Photos -> Query.PhotoQuery()
            AllowedMedia.Videos -> Query.VideoQuery()
            AllowedMedia.Both -> Query.MediaQuery()
        }
        val fileQuery = Query.FileQuery(getSupportedFileSequence(allowedMedia).toList())
        it.getMedia(
            mediaQuery = query,
            fileQuery = fileQuery
        )
    }

    private val uris = arrayOf(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
        MediaStore.Files.getContentUri("external")
    )

    private fun Context.retrieveMedia(
        dataBody: suspend (ContentResolver) -> List<Media>
    ) = contentFlowObserver(
        uris = uris,
        coroutineContext = ioDispatcher
    ).map {
        runSuspendCatching {
            dataBody.invoke(contentResolver)
        }
    }.conflate()

    private fun Context.retrieveAlbums(
        dataBody: suspend (ContentResolver) -> List<Album>
    ) = contentFlowObserver(
        uris = uris,
        coroutineContext = ioDispatcher
    ).map {
        runSuspendCatching {
            dataBody.invoke(contentResolver)
        }
    }.conflate()

}