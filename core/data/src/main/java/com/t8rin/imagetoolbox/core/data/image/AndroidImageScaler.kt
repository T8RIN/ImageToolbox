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

package com.t8rin.imagetoolbox.core.data.image

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import androidx.core.graphics.BitmapCompat
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale
import com.awxkee.aire.Aire
import com.awxkee.aire.ResizeFunction
import com.t8rin.imagetoolbox.core.data.image.utils.drawBitmap
import com.t8rin.imagetoolbox.core.data.utils.aspectRatio
import com.t8rin.imagetoolbox.core.data.utils.safeConfig
import com.t8rin.imagetoolbox.core.data.utils.toSoftware
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.ImageScaler
import com.t8rin.imagetoolbox.core.domain.image.ImageTransformer
import com.t8rin.imagetoolbox.core.domain.image.model.ImageScaleMode
import com.t8rin.imagetoolbox.core.domain.image.model.ResizeAnchor
import com.t8rin.imagetoolbox.core.domain.image.model.ResizeType
import com.t8rin.imagetoolbox.core.domain.image.model.ScaleColorSpace
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.model.Position
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.core.filters.domain.FilterProvider
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.createFilter
import com.t8rin.imagetoolbox.core.settings.domain.SettingsProvider
import com.t8rin.logger.makeLog
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt
import com.awxkee.aire.ScaleColorSpace as AireScaleColorSpace

internal class AndroidImageScaler @Inject constructor(
    settingsProvider: SettingsProvider,
    private val imageTransformer: ImageTransformer<Bitmap>,
    private val filterProvider: FilterProvider<Bitmap>,
    dispatchersHolder: DispatchersHolder
) : DispatchersHolder by dispatchersHolder, ImageScaler<Bitmap> {

    private val _settingsState = settingsProvider.settingsState
    private val settingsState get() = _settingsState.value

    override suspend fun scaleImage(
        image: Bitmap,
        width: Int,
        height: Int,
        resizeType: ResizeType,
        imageScaleMode: ImageScaleMode
    ): Bitmap = withContext(defaultDispatcher) {

        val widthInternal = width.takeIf { it > 0 } ?: image.width
        val heightInternal = height.takeIf { it > 0 } ?: image.height

        runSuspendCatching {
            when (resizeType) {
                ResizeType.Explicit -> {
                    createScaledBitmap(
                        image = image,
                        width = widthInternal,
                        height = heightInternal,
                        imageScaleMode = imageScaleMode
                    )
                }

                is ResizeType.Flexible -> {
                    flexibleResize(
                        image = image,
                        width = widthInternal,
                        height = heightInternal,
                        resizeAnchor = resizeType.resizeAnchor,
                        imageScaleMode = imageScaleMode
                    )
                }

                is ResizeType.CenterCrop -> {
                    resizeType.performCenterCrop(
                        image = image,
                        targetWidth = widthInternal,
                        targetHeight = heightInternal,
                        imageScaleMode = imageScaleMode
                    )
                }

                is ResizeType.Fit -> {
                    resizeType.performFitResize(
                        image = image,
                        targetWidth = widthInternal,
                        targetHeight = heightInternal,
                        imageScaleMode = imageScaleMode
                    )
                }
            }
        }.onFailure {
            it.makeLog("AndroidImageScaler")
        }.getOrNull() ?: image
    }

    override suspend fun scaleUntilCanShow(
        image: Bitmap?
    ): Bitmap? = withContext(defaultDispatcher) {
        if (image == null) return@withContext null
        if (canShow(image.width * image.height * 4)) return@withContext image

        var (height, width) = image.run { height to width }

        var iterations = 0
        while (!canShow(size = height * width * 4)) {
            height = (height * 0.85f).roundToInt()
            width = (width * 0.85f).roundToInt()
            iterations++
        }

        if (iterations == 0) image
        else scaleImage(
            image = image,
            height = height,
            width = width,
            imageScaleMode = ImageScaleMode.Bicubic()
        )
    }

    private fun canShow(size: Int): Boolean {
        return size < 3096 * 3096 * 3
    }

    private suspend fun Bitmap.fitResize(
        targetWidth: Int,
        targetHeight: Int,
        imageScaleMode: ImageScaleMode
    ): Bitmap {
        val aspectRatio = width.toFloat() / height.toFloat()
        val targetAspectRatio = targetWidth.toFloat() / targetHeight.toFloat()

        val finalWidth: Int
        val finalHeight: Int

        if (aspectRatio > targetAspectRatio) {
            // Image is wider than the target aspect ratio
            finalWidth = targetWidth
            finalHeight = (targetWidth / aspectRatio).toInt()
        } else {
            // Image is taller than or equal to the target aspect ratio
            finalWidth = (targetHeight * aspectRatio).toInt()
            finalHeight = targetHeight
        }

        return createScaledBitmap(
            image = this,
            width = finalWidth,
            height = finalHeight,
            imageScaleMode = imageScaleMode
        )
    }

    private suspend fun ResizeType.Fit.performFitResize(
        image: Bitmap,
        targetWidth: Int,
        targetHeight: Int,
        imageScaleMode: ImageScaleMode
    ): Bitmap = withContext(defaultDispatcher) {
        if (targetWidth == image.width && targetHeight == image.height) {
            return@withContext image
        }

        val originalWidth: Int
        val originalHeight: Int

        val aspect = image.aspectRatio
        val originalAspect = image.aspectRatio

        if (abs(aspect - originalAspect) > 0.001f) {
            originalWidth = image.height
            originalHeight = image.width
        } else {
            originalWidth = image.width
            originalHeight = image.height
        }

        val drawImage = image.fitResize(
            targetWidth = targetWidth,
            targetHeight = targetHeight,
            imageScaleMode = imageScaleMode
        )

        val blurredBitmap = imageTransformer.transform(
            image = drawImage.let { bitmap ->
                val xScale: Float = targetWidth.toFloat() / originalWidth
                val yScale: Float = targetHeight.toFloat() / originalHeight
                val scale = xScale.coerceAtLeast(yScale)
                createScaledBitmap(
                    image = bitmap,
                    width = (scale * originalWidth).toInt(),
                    height = (scale * originalHeight).toInt(),
                    imageScaleMode = imageScaleMode
                )
            },
            transformations = listOf(
                filterProvider.filterToTransformation(
                    createFilter<Float, Filter.NativeStackBlur>(
                        blurRadius.toFloat() / 1000 * max(targetWidth, targetHeight)
                    )
                )
            )
        )

        createBitmap(targetWidth, targetHeight, drawImage.safeConfig).apply { setHasAlpha(true) }
            .applyCanvas {
                drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
                canvasColor?.let {
                    drawColor(it)
                } ?: blurredBitmap?.let {
                    drawBitmap(
                        bitmap = blurredBitmap,
                        position = Position.Center
                    )
                }
                drawBitmap(
                    bitmap = drawImage,
                    position = position
                )
            }
    }

    private suspend fun ResizeType.CenterCrop.performCenterCrop(
        image: Bitmap,
        targetWidth: Int,
        targetHeight: Int,
        imageScaleMode: ImageScaleMode
    ): Bitmap = withContext(defaultDispatcher) {
        val originalSize = if (!originalSize.isDefined()) {
            IntegerSize(
                width = image.width,
                height = image.height
            )
        } else {
            originalSize
        } * scaleFactor

        if (targetWidth == originalSize.width && targetHeight == originalSize.height) {
            return@withContext image
        }

        val originalWidth: Int
        val originalHeight: Int

        val aspect = image.aspectRatio
        val originalAspect = originalSize.aspectRatio

        if (abs(aspect - originalAspect) > 0.001f) {
            originalWidth = originalSize.height
            originalHeight = originalSize.width
        } else {
            originalWidth = originalSize.width
            originalHeight = originalSize.height
        }

        val drawImage = createScaledBitmap(
            image = image,
            width = originalWidth,
            height = originalHeight,
            imageScaleMode = imageScaleMode
        )

        val blurredBitmap = if (canvasColor == null) {
            imageTransformer.transform(
                image = drawImage.let { bitmap ->
                    val xScale: Float = targetWidth.toFloat() / originalWidth
                    val yScale: Float = targetHeight.toFloat() / originalHeight
                    val scale = xScale.coerceAtLeast(yScale)
                    createScaledBitmap(
                        image = bitmap,
                        width = (scale * originalWidth).toInt(),
                        height = (scale * originalHeight).toInt(),
                        imageScaleMode = imageScaleMode
                    )
                },
                transformations = listOf(
                    filterProvider.filterToTransformation(
                        createFilter<Float, Filter.NativeStackBlur>(
                            blurRadius.toFloat() / 1000 * max(targetWidth, targetHeight)
                        )
                    )
                )
            )
        } else null

        createBitmap(targetWidth, targetHeight, drawImage.safeConfig).apply { setHasAlpha(true) }
            .applyCanvas {
                drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
                canvasColor?.let {
                    drawColor(it)
                } ?: blurredBitmap?.let {
                    drawBitmap(
                        bitmap = blurredBitmap,
                        position = Position.Center
                    )
                }
                drawBitmap(
                    bitmap = drawImage,
                    position = position
                )
            }
    }

    private suspend fun createScaledBitmap(
        image: Bitmap,
        width: Int,
        height: Int,
        imageScaleMode: ImageScaleMode
    ): Bitmap = withContext(defaultDispatcher) {
        if (width == image.width && height == image.height) return@withContext image

        val softwareImage = image.toSoftware()

        if (imageScaleMode is ImageScaleMode.Base) {
            return@withContext if (width < softwareImage.width && height < softwareImage.width) {
                BitmapCompat.createScaledBitmap(softwareImage, width, height, null, true)
            } else {
                softwareImage.scale(width, height)
            }
        }

        val mode = imageScaleMode.takeIf {
            it != ImageScaleMode.NotPresent && it.value >= 0
        } ?: settingsState.defaultImageScaleMode

        Aire.scale(
            bitmap = softwareImage,
            dstWidth = width,
            dstHeight = height,
            scaleMode = mode.toResizeFunction(),
            colorSpace = mode.scaleColorSpace.toColorSpace()
        )
    }

    private fun ImageScaleMode.toResizeFunction(): ResizeFunction = when (this) {
        ImageScaleMode.NotPresent,
        ImageScaleMode.Base -> ResizeFunction.Bilinear

        is ImageScaleMode.Bilinear -> ResizeFunction.Bilinear
        is ImageScaleMode.Nearest -> ResizeFunction.Nearest
        is ImageScaleMode.Cubic -> ResizeFunction.Cubic
        is ImageScaleMode.Mitchell -> ResizeFunction.MitchellNetravalli
        is ImageScaleMode.Catmull -> ResizeFunction.CatmullRom
        is ImageScaleMode.Hermite -> ResizeFunction.Hermite
        is ImageScaleMode.BSpline -> ResizeFunction.BSpline
        is ImageScaleMode.Hann -> ResizeFunction.Hann
        is ImageScaleMode.Bicubic -> ResizeFunction.Bicubic
        is ImageScaleMode.Hamming -> ResizeFunction.Hamming
        is ImageScaleMode.Hanning -> ResizeFunction.Hanning
        is ImageScaleMode.Blackman -> ResizeFunction.Blackman
        is ImageScaleMode.Welch -> ResizeFunction.Welch
        is ImageScaleMode.Quadric -> ResizeFunction.Quadric
        is ImageScaleMode.Gaussian -> ResizeFunction.Gaussian
        is ImageScaleMode.Sphinx -> ResizeFunction.Sphinx
        is ImageScaleMode.Bartlett -> ResizeFunction.Bartlett
        is ImageScaleMode.Robidoux -> ResizeFunction.Robidoux
        is ImageScaleMode.RobidouxSharp -> ResizeFunction.RobidouxSharp
        is ImageScaleMode.Spline16 -> ResizeFunction.Spline16
        is ImageScaleMode.Spline36 -> ResizeFunction.Spline36
        is ImageScaleMode.Spline64 -> ResizeFunction.Spline64
        is ImageScaleMode.Kaiser -> ResizeFunction.Kaiser
        is ImageScaleMode.BartlettHann -> ResizeFunction.BartlettHann
        is ImageScaleMode.Box -> ResizeFunction.Box
        is ImageScaleMode.Bohman -> ResizeFunction.Bohman
        is ImageScaleMode.Lanczos2 -> ResizeFunction.Lanczos2
        is ImageScaleMode.Lanczos3 -> ResizeFunction.Lanczos3
        is ImageScaleMode.Lanczos4 -> ResizeFunction.Lanczos4
        is ImageScaleMode.Lanczos2Jinc -> ResizeFunction.Lanczos2Jinc
        is ImageScaleMode.Lanczos3Jinc -> ResizeFunction.Lanczos3Jinc
        is ImageScaleMode.Lanczos4Jinc -> ResizeFunction.Lanczos4Jinc
        is ImageScaleMode.EwaHanning -> ResizeFunction.EwaHanning
        is ImageScaleMode.EwaRobidoux -> ResizeFunction.EwaRobidoux
        is ImageScaleMode.EwaBlackman -> ResizeFunction.EwaBlackman
        is ImageScaleMode.EwaQuadric -> ResizeFunction.EwaQuadric
        is ImageScaleMode.EwaRobidouxSharp -> ResizeFunction.EwaRobidouxSharp
        is ImageScaleMode.EwaLanczos3Jinc -> ResizeFunction.EwaLanczos3Jinc
        is ImageScaleMode.Ginseng -> ResizeFunction.Ginseng
        is ImageScaleMode.EwaGinseng -> ResizeFunction.EwaGinseng
        is ImageScaleMode.EwaLanczosSharp -> ResizeFunction.EwaLanczosSharp
        is ImageScaleMode.EwaLanczos4Sharpest -> ResizeFunction.EwaLanczos4Sharpest
        is ImageScaleMode.EwaLanczosSoft -> ResizeFunction.EwaLanczosSoft
        is ImageScaleMode.HaasnSoft -> ResizeFunction.HaasnSoft
        is ImageScaleMode.Lagrange2 -> ResizeFunction.Lagrange2
        is ImageScaleMode.Lagrange3 -> ResizeFunction.Lagrange3
        is ImageScaleMode.Lanczos6 -> ResizeFunction.Lanczos6
        is ImageScaleMode.Lanczos6Jinc -> ResizeFunction.Lanczos6Jinc
        is ImageScaleMode.Area -> ResizeFunction.Area
    }

    private fun ScaleColorSpace.toColorSpace(): AireScaleColorSpace = when (this) {
        ScaleColorSpace.LAB -> AireScaleColorSpace.LAB
        ScaleColorSpace.Linear -> AireScaleColorSpace.LINEAR
        ScaleColorSpace.SRGB -> AireScaleColorSpace.SRGB
        ScaleColorSpace.LUV -> AireScaleColorSpace.LUV
        ScaleColorSpace.Sigmoidal -> AireScaleColorSpace.SIGMOIDAL
        ScaleColorSpace.XYZ -> AireScaleColorSpace.XYZ
        ScaleColorSpace.F32Gamma22 -> AireScaleColorSpace.LINEAR_F32_GAMMA_2_2
        ScaleColorSpace.F32Gamma28 -> AireScaleColorSpace.LINEAR_F32_GAMMA_2_8
        ScaleColorSpace.F32Rec709 -> AireScaleColorSpace.LINEAR_F32_REC709
        ScaleColorSpace.F32sRGB -> AireScaleColorSpace.LINEAR_F32_SRGB
        ScaleColorSpace.LCH -> AireScaleColorSpace.LCH
        ScaleColorSpace.OklabGamma22 -> AireScaleColorSpace.OKLAB_GAMMA_2_2
        ScaleColorSpace.OklabGamma28 -> AireScaleColorSpace.OKLAB_GAMMA_2_8
        ScaleColorSpace.OklabRec709 -> AireScaleColorSpace.OKLAB_REC709
        ScaleColorSpace.OklabSRGB -> AireScaleColorSpace.OKLAB_SRGB
        ScaleColorSpace.JzazbzGamma22 -> AireScaleColorSpace.JZAZBZ_GAMMA_2_2
        ScaleColorSpace.JzazbzGamma28 -> AireScaleColorSpace.JZAZBZ_GAMMA_2_8
        ScaleColorSpace.JzazbzRec709 -> AireScaleColorSpace.JZAZBZ_REC709
        ScaleColorSpace.JzazbzSRGB -> AireScaleColorSpace.JZAZBZ_SRGB
    }

    private suspend fun flexibleResize(
        image: Bitmap,
        width: Int,
        height: Int,
        resizeAnchor: ResizeAnchor,
        imageScaleMode: ImageScaleMode
    ): Bitmap = withContext(defaultDispatcher) {
        val max = max(width, height)

        val scaleByWidth = suspend {
            val aspectRatio = image.aspectRatio
            createScaledBitmap(
                image = image,
                width = width,
                height = (width / aspectRatio).toInt(),
                imageScaleMode = imageScaleMode
            )
        }

        val scaleByHeight = suspend {
            val aspectRatio = image.aspectRatio
            createScaledBitmap(
                image = image,
                width = (height * aspectRatio).toInt(),
                height = height,
                imageScaleMode = imageScaleMode
            )
        }

        when (resizeAnchor) {
            ResizeAnchor.Max -> {
                if (width >= height) {
                    scaleByWidth()
                } else scaleByHeight()
            }

            ResizeAnchor.Min -> {
                if (width >= height) {
                    scaleByHeight()
                } else scaleByWidth()
            }

            ResizeAnchor.Width -> scaleByWidth()

            ResizeAnchor.Height -> scaleByHeight()

            ResizeAnchor.Default -> {
                runSuspendCatching {
                    if (image.height >= image.width) {
                        val aspectRatio = image.aspectRatio
                        val targetWidth = (max * aspectRatio).toInt()
                        createScaledBitmap(image, targetWidth, max, imageScaleMode)
                    } else {
                        val aspectRatio = 1f / image.aspectRatio
                        val targetHeight = (max * aspectRatio).toInt()
                        createScaledBitmap(image, max, targetHeight, imageScaleMode)
                    }
                }.getOrNull() ?: image
            }
        }
    }

}