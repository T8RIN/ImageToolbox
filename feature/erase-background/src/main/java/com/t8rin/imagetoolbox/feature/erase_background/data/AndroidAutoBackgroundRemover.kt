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

package com.t8rin.imagetoolbox.feature.erase_background.data

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.t8rin.imagetoolbox.core.data.image.utils.healAlpha
import com.t8rin.imagetoolbox.core.domain.coroutines.AppScope
import com.t8rin.imagetoolbox.feature.erase_background.domain.AutoBackgroundRemover
import com.t8rin.imagetoolbox.feature.erase_background.domain.AutoBackgroundRemoverBackendFactory
import com.t8rin.imagetoolbox.feature.erase_background.domain.model.BgModelType
import com.t8rin.logger.makeLog
import com.t8rin.neural_tools.bgremover.BgRemover
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class AndroidAutoBackgroundRemover @Inject constructor(
    private val backendFactory: AutoBackgroundRemoverBackendFactory<Bitmap>,
    private val appScope: AppScope
) : AutoBackgroundRemover<Bitmap> {

    override suspend fun trimEmptyParts(
        image: Bitmap,
        emptyColor: Int?
    ): Bitmap = coroutineScope {
        val transparent = emptyColor ?: Color.Transparent.toArgb()

        async {
            var firstX = 0
            var firstY = 0
            var lastX = image.width
            var lastY = image.height
            val pixels = IntArray(image.width * image.height)
            image.getPixels(pixels, 0, image.width, 0, 0, image.width, image.height)

            loop@ for (x in 0 until image.width) {
                for (y in 0 until image.height) {
                    if (pixels[x + y * image.width] != transparent) {
                        firstX = x
                        break@loop
                    }
                }
            }
            loop@ for (y in 0 until image.height) {
                for (x in firstX until image.width) {
                    if (pixels[x + y * image.width] != transparent) {
                        firstY = y
                        break@loop
                    }
                }
            }
            loop@ for (x in image.width - 1 downTo firstX) {
                for (y in image.height - 1 downTo firstY) {
                    if (pixels[x + y * image.width] != transparent) {
                        lastX = x
                        break@loop
                    }
                }
            }
            loop@ for (y in image.height - 1 downTo firstY) {
                for (x in image.width - 1 downTo firstX) {
                    if (pixels[x + y * image.width] != transparent) {
                        lastY = y
                        break@loop
                    }
                }
            }

            return@async runCatching {
                Bitmap.createBitmap(
                    image,
                    firstX,
                    firstY,
                    lastX - firstX + 1,
                    lastY - firstY + 1
                )
            }.onFailure {
                "trimEmptyParts".makeLog("Failed to crop image (firstX = $firstX, firstY = $firstX, lastX = $lastX, lastY = $lastY): ${it.message}")
            }.getOrNull() ?: image
        }.await()
    }

    override fun removeBackgroundFromImage(
        image: Bitmap,
        modelType: BgModelType,
        onSuccess: (Bitmap) -> Unit,
        onFailure: (Throwable) -> Unit
    ) {
        appScope.launch {
            backendFactory.create(modelType)
                .performBackgroundRemove(image)
                .map { it.healAlpha(image) }
                .onSuccess(onSuccess)
                .onFailure {
                    onFailure(it.makeLog())
                }
        }
    }

    override fun cleanup() {
        BgRemover.closeAll()
    }

}