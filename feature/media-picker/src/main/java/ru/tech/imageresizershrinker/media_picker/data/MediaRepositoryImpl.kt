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

package ru.tech.imageresizershrinker.media_picker.data

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.os.Bundle
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import ru.tech.imageresizershrinker.core.di.IoDispatcher
import ru.tech.imageresizershrinker.media_picker.domain.Album
import ru.tech.imageresizershrinker.media_picker.domain.AllowedMedia
import ru.tech.imageresizershrinker.media_picker.domain.Media
import ru.tech.imageresizershrinker.media_picker.domain.MediaOrder
import ru.tech.imageresizershrinker.media_picker.domain.MediaRepository
import ru.tech.imageresizershrinker.media_picker.domain.OrderType
import javax.inject.Inject

internal class MediaRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : MediaRepository {

    @SuppressLint("InlinedApi")
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
        it.getAlbums(query, mediaOrder = MediaOrder.Label(OrderType.Ascending))
    }

    override fun mediaFlowWithType(
        albumId: Long,
        allowedMedia: AllowedMedia
    ): Flow<Result<List<Media>>> =
        (if (albumId != -1L) {
            getMediaByAlbumIdWithType(albumId, allowedMedia)
        } else {
            getMediaByType(allowedMedia)
        }).flowOn(dispatcher).conflate()

    @SuppressLint("InlinedApi")
    override fun getMediaByAlbumIdWithType(
        albumId: Long,
        allowedMedia: AllowedMedia
    ): Flow<Result<List<Media>>> =
        context.retrieveMedia {
            val query = Query.MediaQuery().copy(
                bundle = Bundle().apply {
                    val mimeType = when (allowedMedia) {
                        is AllowedMedia.Photos -> "image%"
                        AllowedMedia.Videos -> "video%"
                        AllowedMedia.Both -> "%/%"
                    }
                    putString(
                        ContentResolver.QUERY_ARG_SQL_SELECTION,
                        MediaStore.MediaColumns.BUCKET_ID + "= ? and " + MediaStore.MediaColumns.MIME_TYPE + " like ?"
                    )
                    putStringArray(
                        ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS,
                        arrayOf(albumId.toString(), mimeType)
                    )
                }
            )
            /** return@retrieveMedia */
            it.getMedia(query)
        }

    override fun getMediaByType(allowedMedia: AllowedMedia): Flow<Result<List<Media>>> =
        context.retrieveMedia {
            val query = when (allowedMedia) {
                is AllowedMedia.Photos -> Query.PhotoQuery()
                AllowedMedia.Videos -> Query.VideoQuery()
                AllowedMedia.Both -> Query.MediaQuery()
            }
            it.getMedia(mediaQuery = query)
        }

    private val uris = arrayOf(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    )

    private fun Context.retrieveMedia(dataBody: suspend (ContentResolver) -> List<Media>) =
        contentFlowObserver(uris).map {
            try {
                Result.success(dataBody.invoke(contentResolver))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }.conflate()

    private fun Context.retrieveAlbums(dataBody: suspend (ContentResolver) -> List<Album>) =
        contentFlowObserver(uris).map {
            try {
                Result.success(dataBody.invoke(contentResolver))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }.conflate()

}