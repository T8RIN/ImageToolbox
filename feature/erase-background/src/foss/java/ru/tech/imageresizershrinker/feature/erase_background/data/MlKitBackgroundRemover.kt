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

package ru.tech.imageresizershrinker.feature.erase_background.data

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object MlKitBackgroundRemover {

    /**
     * Process the image to get buffer and image height and width
     * @param bitmap Bitmap which you want to remove background.
     * @param trimEmptyPart After removing the background if its true it will remove the empty part of bitmap. by default its false.
     * @param listener listener for success and failure callback.
     **/
    fun bitmapForProcessing(
        bitmap: Bitmap,
        scope: CoroutineScope,
        trimEmptyPart: Boolean? = false,
        listener: suspend (Result<Bitmap>) -> Unit
    ) {
        scope.launch {
            withContext(Dispatchers.IO) {
                listener(Result.failure(UnsupportedOperationException("FOSS")))
            }
        }
    }

    /**
     * trim the empty part of a bitmap.
     **/
    suspend fun trim(
        bitmap: Bitmap
    ): Bitmap {
        val result = CoroutineScope(Dispatchers.IO).async {
            var firstX = 0
            var firstY = 0
            var lastX = bitmap.width
            var lastY = bitmap.height
            val pixels = IntArray(bitmap.width * bitmap.height)
            bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
            loop@ for (x in 0 until bitmap.width) {
                for (y in 0 until bitmap.height) {
                    if (pixels[x + y * bitmap.width] != Color.Transparent.toArgb()) {
                        firstX = x
                        break@loop
                    }
                }
            }
            loop@ for (y in 0 until bitmap.height) {
                for (x in firstX until bitmap.width) {
                    if (pixels[x + y * bitmap.width] != Color.Transparent.toArgb()) {
                        firstY = y
                        break@loop
                    }
                }
            }
            loop@ for (x in bitmap.width - 1 downTo firstX) {
                for (y in bitmap.height - 1 downTo firstY) {
                    if (pixels[x + y * bitmap.width] != Color.Transparent.toArgb()) {
                        lastX = x
                        break@loop
                    }
                }
            }
            loop@ for (y in bitmap.height - 1 downTo firstY) {
                for (x in bitmap.width - 1 downTo firstX) {
                    if (pixels[x + y * bitmap.width] != Color.Transparent.toArgb()) {
                        lastY = y
                        break@loop
                    }
                }
            }
            return@async Bitmap.createBitmap(bitmap, firstX, firstY, lastX - firstX, lastY - firstY)
        }
        return result.await()
    }

}