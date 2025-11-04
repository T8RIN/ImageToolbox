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

package com.t8rin.imagetoolbox.feature.limits_resize.data

import android.graphics.Bitmap
import com.t8rin.imagetoolbox.core.data.utils.aspectRatio
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageScaler
import com.t8rin.imagetoolbox.core.domain.image.model.ImageScaleMode
import com.t8rin.imagetoolbox.feature.limits_resize.domain.LimitsImageScaler
import com.t8rin.imagetoolbox.feature.limits_resize.domain.LimitsResizeType
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class AndroidLimitsImageScaler @Inject constructor(
    private val imageScaler: ImageScaler<Bitmap>,
    dispatchersHolder: DispatchersHolder
) : DispatchersHolder by dispatchersHolder,
    LimitsImageScaler<Bitmap>,
    ImageScaler<Bitmap> by imageScaler {

    override suspend fun scaleImage(
        image: Bitmap,
        width: Int,
        height: Int,
        resizeType: LimitsResizeType,
        imageScaleMode: ImageScaleMode
    ): Bitmap? = withContext(defaultDispatcher) {
        val widthInternal = width.takeIf { it > 0 } ?: Int.MAX_VALUE
        val heightInternal = height.takeIf { it > 0 } ?: Int.MAX_VALUE

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
            return when {
                image.aspectRatio > limitAspectRatio -> {
                    scaleImage(
                        image = image,
                        width = limitWidth,
                        height = (limitWidth / image.aspectRatio).toInt(),
                        imageScaleMode = imageScaleMode
                    )
                }

                image.aspectRatio < limitAspectRatio -> {
                    scaleImage(
                        image = image,
                        width = (limitHeight * image.aspectRatio).toInt(),
                        height = limitHeight,
                        imageScaleMode = imageScaleMode
                    )
                }

                else -> {
                    scaleImage(
                        image = image,
                        width = limitWidth,
                        height = limitHeight,
                        imageScaleMode = imageScaleMode
                    )
                }
            }
        } else {
            return when (this) {
                is LimitsResizeType.Recode -> image

                is LimitsResizeType.Zoom -> {
                    when {
                        limitHeight == Int.MAX_VALUE -> {
                            val newHeight = (limitWidth / image.aspectRatio).toInt()
                            scaleImage(
                                image = image,
                                width = limitWidth,
                                height = newHeight,
                                imageScaleMode = imageScaleMode
                            )
                        }

                        limitWidth == Int.MAX_VALUE -> {
                            val newWidth = (limitHeight * image.aspectRatio).toInt()
                            scaleImage(
                                image = image,
                                width = newWidth,
                                height = limitHeight,
                                imageScaleMode = imageScaleMode
                            )
                        }

                        else -> {
                            val widthRatio = limitWidth.toDouble() / image.width
                            val heightRatio = limitHeight.toDouble() / image.height
                            val ratio = minOf(widthRatio, heightRatio)

                            val newWidth = (image.width * ratio).toInt()
                            val newHeight = (image.height * ratio).toInt()

                            scaleImage(
                                image = image,
                                width = newWidth,
                                height = newHeight,
                                imageScaleMode = imageScaleMode
                            )
                        }
                    }
                }

                is LimitsResizeType.Skip -> null
            }
        }
    }

}