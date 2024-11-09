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

package ru.tech.imageresizershrinker.feature.gif_tools.domain

import kotlinx.coroutines.flow.Flow
import ru.tech.imageresizershrinker.core.domain.image.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.image.model.ImageFrames
import ru.tech.imageresizershrinker.core.domain.image.model.Quality

interface GifConverter {

    fun extractFramesFromGif(
        gifUri: String,
        imageFormat: ImageFormat,
        imageFrames: ImageFrames,
        quality: Quality,
        onGetFramesCount: (frames: Int) -> Unit = {}
    ): Flow<String>

    suspend fun createGifFromImageUris(
        imageUris: List<String>,
        params: GifParams,
        onFailure: (Throwable) -> Unit,
        onProgress: () -> Unit
    ): ByteArray?

    suspend fun convertGifToJxl(
        gifUris: List<String>,
        quality: Quality.Jxl,
        onProgress: suspend (String, ByteArray) -> Unit
    )

    suspend fun convertGifToWebp(
        gifUris: List<String>,
        quality: Quality.Base,
        onProgress: suspend (String, ByteArray) -> Unit
    )

}