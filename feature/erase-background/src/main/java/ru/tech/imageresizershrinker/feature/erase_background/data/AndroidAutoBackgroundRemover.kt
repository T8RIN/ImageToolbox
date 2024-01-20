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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import ru.tech.imageresizershrinker.feature.erase_background.domain.AutoBackgroundRemover

internal class AndroidAutoBackgroundRemover : AutoBackgroundRemover<Bitmap> {
    override suspend fun trimEmptyParts(image: Bitmap): Bitmap = MlKitBackgroundRemover.trim(image)

    override fun removeBackgroundFromImage(
        image: Bitmap,
        onSuccess: (Bitmap) -> Unit,
        onFailure: (Throwable) -> Unit,
        trimEmptyParts: Boolean
    ) {
        kotlin.runCatching {
            MlKitBackgroundRemover.bitmapForProcessing(
                bitmap = image,
                scope = CoroutineScope(Dispatchers.IO)
            ) { result ->
                if (result.isSuccess) {
                    result.getOrNull()?.let {
                        if (trimEmptyParts) trimEmptyParts(it)
                        else it
                    }?.let(onSuccess)
                } else result.exceptionOrNull()?.let(onFailure)
            }
        }.exceptionOrNull()?.let(onFailure)
    }
}