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

package ru.tech.imageresizershrinker.feature.jxl_tools.domain

import kotlinx.coroutines.flow.Flow
import ru.tech.imageresizershrinker.core.domain.image.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.image.model.ImageFrames
import ru.tech.imageresizershrinker.core.domain.image.model.Quality

interface JxlConverter {

    suspend fun jpegToJxl(
        jpegUris: List<String>,
        onFailure: (Throwable) -> Unit,
        onProgress: suspend (originalUri: String, data: ByteArray) -> Unit
    )

    suspend fun jxlToJpeg(
        jxlUris: List<String>,
        onFailure: (Throwable) -> Unit,
        onProgress: suspend (originalUri: String, data: ByteArray) -> Unit
    )

    suspend fun createJxlAnimation(
        imageUris: List<String>,
        params: AnimatedJxlParams,
        onFailure: (Throwable) -> Unit,
        onProgress: () -> Unit
    ): ByteArray?

    fun extractFramesFromJxl(
        jxlUri: String,
        imageFormat: ImageFormat,
        imageFrames: ImageFrames,
        quality: Quality,
        onFailure: (Throwable) -> Unit,
        onGetFramesCount: (frames: Int) -> Unit = {}
    ): Flow<String>

}