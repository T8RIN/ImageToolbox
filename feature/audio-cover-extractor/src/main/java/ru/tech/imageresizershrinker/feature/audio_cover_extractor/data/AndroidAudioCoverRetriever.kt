/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.feature.audio_cover_extractor.data

import android.content.Context
import android.graphics.Bitmap
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.data.utils.getFilename
import ru.tech.imageresizershrinker.core.domain.dispatchers.DispatchersHolder
import ru.tech.imageresizershrinker.core.domain.image.ImageCompressor
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.image.ShareProvider
import ru.tech.imageresizershrinker.core.domain.image.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.image.model.Quality
import ru.tech.imageresizershrinker.core.domain.resource.ResourceManager
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.feature.audio_cover_extractor.domain.AudioCoverRetriever
import wseemann.media.FFmpegMediaMetadataRetriever
import javax.inject.Inject

internal class AndroidAudioCoverRetriever @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val shareProvider: ShareProvider,
    private val imageGetter: ImageGetter<Bitmap>,
    dispatchersHolder: DispatchersHolder,
    resourceManager: ResourceManager
) : AudioCoverRetriever,
    DispatchersHolder by dispatchersHolder,
    ResourceManager by resourceManager {

    override suspend fun loadCover(
        audioUri: String
    ): Result<String> = withContext(defaultDispatcher) {
        val fail = {
            Result.failure<String>(NullPointerException(getString(R.string.no_image)))
        }

        val pictureData = runCatching {
            FFmpegMediaMetadataRetriever().run {
                setDataSource(
                    context,
                    audioUri.toUri()
                )

                embeddedPicture ?: frameAtTime
            }
        }.onFailure {
            return@withContext Result.failure(it)
        }.getOrNull() ?: return@withContext fail()

        ensureActive()

        imageGetter.getImage(
            data = pictureData,
            originalSize = true
        )?.let { bitmap ->
            val originalName = audioUri.toUri().getFilename(context)?.substringBeforeLast('.')
                ?: "AUDIO_${System.currentTimeMillis()}"

            shareProvider.cacheData(
                writeData = {
                    it.writeBytes(
                        imageCompressor.compress(
                            image = bitmap,
                            imageFormat = ImageFormat.Png.Lossless,
                            quality = Quality.Base()
                        )
                    )
                },
                filename = "$originalName.png"
            )?.let(Result.Companion::success)
        } ?: fail()
    }

    override suspend fun loadCover(
        audioData: ByteArray
    ): Result<String> {
        val audioUri = shareProvider.cacheData(
            writeData = {
                it.writeBytes(audioData)
            },
            filename = "Audio_data_${System.currentTimeMillis()}.mp3"
        ) ?: return Result.failure(NullPointerException(getString(R.string.filename_is_not_set)))

        return loadCover(
            audioUri = audioUri
        )
    }


}