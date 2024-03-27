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

package ru.tech.imageresizershrinker.feature.limits_resize.data

import android.graphics.Bitmap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.data.utils.aspectRatio
import ru.tech.imageresizershrinker.core.di.DefaultDispatcher
import ru.tech.imageresizershrinker.core.domain.image.ImageScaler
import ru.tech.imageresizershrinker.core.domain.image.model.ImageScaleMode
import ru.tech.imageresizershrinker.core.domain.image.model.ResizeType
import ru.tech.imageresizershrinker.feature.limits_resize.domain.LimitsImageScaler
import ru.tech.imageresizershrinker.feature.limits_resize.domain.LimitsResizeType
import javax.inject.Inject

internal class AndroidLimitsImageScaler @Inject constructor(
    private val imageScaler: ImageScaler<Bitmap>,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
) : LimitsImageScaler<Bitmap>, ImageScaler<Bitmap> by imageScaler {

    override suspend fun scaleImage(
        image: Bitmap,
        width: Int,
        height: Int,
        resizeType: LimitsResizeType,
        imageScaleMode: ImageScaleMode
    ): Bitmap? = withContext(dispatcher) {
        val widthInternal = width.takeIf { it > 0 } ?: image.width
        val heightInternal = height.takeIf { it > 0 } ?: image.height

        resizeType.resizeWithLimits(
            image = image,
            width = widthInternal,
            height = heightInternal,
            imageScaleMode = imageScaleMode
        )
    }

    private suspend fun LimitsResizeType.resizeWithLimits(
        image: Bitmap,
        width: Int,
        height: Int,
        imageScaleMode: ImageScaleMode
    ): Bitmap? {
        val limitWidth: Int
        val limitHeight: Int

        if (autoRotateLimitBox && image.aspectRatio < 1f) {
            limitWidth = height
            limitHeight = width
        } else {
            limitWidth = width
            limitHeight = height
        }
        val limitAspectRatio = limitWidth / limitHeight.toFloat()

        if (image.height > limitHeight || image.width > limitWidth) {
            if (image.aspectRatio > limitAspectRatio) {
                return scaleImage(
                    image = image,
                    width = limitWidth,
                    height = limitWidth,
                    resizeType = ResizeType.Flexible,
                    imageScaleMode = imageScaleMode
                )
            } else if (image.aspectRatio < limitAspectRatio) {
                return scaleImage(
                    image = image,
                    width = limitHeight,
                    height = limitHeight,
                    imageScaleMode = imageScaleMode
                )
            } else {
                return scaleImage(
                    image = image,
                    width = limitWidth,
                    height = limitHeight,
                    imageScaleMode = imageScaleMode
                )
            }
        } else {
            return when (this) {
                is LimitsResizeType.Recode -> image

                is LimitsResizeType.Zoom -> scaleImage(
                    image = image,
                    width = limitWidth,
                    height = limitHeight,
                    imageScaleMode = imageScaleMode
                )

                is LimitsResizeType.Skip -> null
            }
        }
    }


}