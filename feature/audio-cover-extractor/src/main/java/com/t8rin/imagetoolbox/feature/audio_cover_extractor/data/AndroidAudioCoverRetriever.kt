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

package com.t8rin.imagetoolbox.feature.audio_cover_extractor.data

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import androidx.core.net.toUri
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageCompressor
import com.t8rin.imagetoolbox.core.domain.image.ImageGetter
import com.t8rin.imagetoolbox.core.domain.image.ShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.resource.ResourceManager
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.utils.filename
import com.t8rin.imagetoolbox.feature.audio_cover_extractor.domain.AudioCoverRetriever
import com.t8rin.imagetoolbox.feature.audio_cover_extractor.domain.model.AudioCoverResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
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
    ): AudioCoverResult = withContext(defaultDispatcher) {
        val pictureData = runCatching {
            MediaMetadataRetriever().run {
                setDataSource(
                    context,
                    audioUri.toUri()
                )

                embeddedPicture ?: frameAtTime
            }
        }.onFailure {
            return@withContext AudioCoverResult.Failure(it.message.orEmpty())
        }.getOrNull()

        ensureActive()

        val coverUri = pictureData?.let {
            imageGetter.getImage(
                data = pictureData,
                originalSize = true
            )?.let { bitmap ->
                val originalName = audioUri.toUri().filename(context)?.substringBeforeLast('.')
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
                )
            }
        }

        if (coverUri.isNullOrEmpty()) {
            AudioCoverResult.Failure(getString(R.string.no_image))
        } else {
            AudioCoverResult.Success(coverUri)
        }
    }

    override suspend fun loadCover(
        audioData: ByteArray
    ): AudioCoverResult {
        val audioUri = shareProvider.cacheData(
            writeData = {
                it.writeBytes(audioData)
            },
            filename = "Audio_data_${System.currentTimeMillis()}.mp3"
        ) ?: return AudioCoverResult.Failure((getString(R.string.filename_is_not_set)))

        return loadCover(
            audioUri = audioUri
        )
    }


}