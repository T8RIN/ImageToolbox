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

package ru.tech.imageresizershrinker.feature.filters.data.model

import android.content.Context
import android.graphics.Bitmap
import coil3.size.Size
import coil3.size.pxOrElse
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import ru.tech.imageresizershrinker.core.data.utils.asCoil
import ru.tech.imageresizershrinker.core.data.utils.aspectRatio
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.domain.transformation.Transformation
import java.lang.Integer.max
import coil3.transform.Transformation as CoilTransformation

internal abstract class GPUFilterTransformation(
    private val context: Context,
) : CoilTransformation(), Transformation<Bitmap> {

    /**
     * Create the [GPUImageFilter] to apply to this [Transformation]
     */
    abstract fun createFilter(): GPUImageFilter

    override suspend fun transform(
        input: Bitmap,
        size: Size
    ): Bitmap {
        val gpuImage = GPUImage(context)
        gpuImage.setImage(
            flexibleResize(
                image = input,
                max = max(
                    size.height.pxOrElse { input.height },
                    size.width.pxOrElse { input.width }
                )
            )
        )
        gpuImage.setFilter(createFilter())
        return gpuImage.bitmapWithFilterApplied
    }

    override suspend fun transform(
        input: Bitmap,
        size: IntegerSize
    ): Bitmap = transform(input, size.asCoil())
}

private fun flexibleResize(
    image: Bitmap,
    max: Int
): Bitmap {
    return runCatching {
        if (image.height >= image.width) {
            val aspectRatio = image.aspectRatio
            val targetWidth = (max * aspectRatio).toInt()
            Bitmap.createScaledBitmap(image, targetWidth, max, true)
        } else {
            val aspectRatio = 1f / image.aspectRatio
            val targetHeight = (max * aspectRatio).toInt()
            Bitmap.createScaledBitmap(image, max, targetHeight, true)
        }
    }.getOrNull() ?: image
}