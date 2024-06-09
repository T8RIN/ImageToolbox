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

package ru.tech.imageresizershrinker.feature.image_stacking.data

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuffXfermode
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.data.image.utils.toPorterDuffMode
import ru.tech.imageresizershrinker.core.data.utils.getSuitableConfig
import ru.tech.imageresizershrinker.core.domain.dispatchers.DispatchersHolder
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.feature.image_stacking.domain.ImageStacker
import ru.tech.imageresizershrinker.feature.image_stacking.domain.StackImage
import ru.tech.imageresizershrinker.feature.image_stacking.domain.StackingParams
import javax.inject.Inject

internal class AndroidImageStacker @Inject constructor(
    private val imageGetter: ImageGetter<Bitmap, ExifInterface>,
    dispatchersHolder: DispatchersHolder
) : DispatchersHolder by dispatchersHolder, ImageStacker<Bitmap> {

    override suspend fun stackImages(
        stackImages: List<StackImage>,
        stackingParams: StackingParams,
        onProgress: (Int) -> Unit
    ): Bitmap = withContext(defaultDispatcher) {
        val resultSize = stackingParams.imageSize
            ?: imageGetter.getImage(
                data = stackImages.firstOrNull()?.uri ?: "",
                originalSize = true
            )?.let {
                IntegerSize(it.width, it.height)
            } ?: IntegerSize(0, 0)

        val outputBitmap = Bitmap.createBitmap(
            resultSize.width,
            resultSize.height,
            getSuitableConfig()
        )

        val canvas = Canvas(outputBitmap)
        val paint = Paint()

        stackImages.forEachIndexed { index, stackImage ->
            val bitmap = imageGetter.getImage(data = stackImage.uri, size = resultSize)
            paint.alpha = (stackImage.alpha * 255).toInt()
            paint.xfermode = PorterDuffXfermode(stackImage.blendingMode.toPorterDuffMode())

            bitmap?.let {
                canvas.drawBitmap(it, 0f, 0f, paint)
            }

            onProgress((index + 1) * 100 / stackImages.size)
        }

        outputBitmap
    }

}