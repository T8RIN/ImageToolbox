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

@file:Suppress("UNCHECKED_CAST")

package ru.tech.imageresizershrinker.feature.filters.data.provider

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import ru.tech.imageresizershrinker.core.domain.image.Transformation
import ru.tech.imageresizershrinker.core.filters.domain.FilterProvider
import ru.tech.imageresizershrinker.core.filters.domain.model.Filter
import ru.tech.imageresizershrinker.feature.filters.data.BilaterialBlurFilter
import ru.tech.imageresizershrinker.feature.filters.data.BlackAndWhiteFilter
import ru.tech.imageresizershrinker.feature.filters.data.BoxBlurFilter
import ru.tech.imageresizershrinker.feature.filters.data.BrightnessFilter
import ru.tech.imageresizershrinker.feature.filters.data.BulgeDistortionFilter
import ru.tech.imageresizershrinker.feature.filters.data.CGAColorSpaceFilter
import ru.tech.imageresizershrinker.feature.filters.data.CirclePixelationFilter
import ru.tech.imageresizershrinker.feature.filters.data.ColorBalanceFilter
import ru.tech.imageresizershrinker.feature.filters.data.ColorFilter
import ru.tech.imageresizershrinker.feature.filters.data.ColorMatrixFilter
import ru.tech.imageresizershrinker.feature.filters.data.ContrastFilter
import ru.tech.imageresizershrinker.feature.filters.data.Convolution3x3Filter
import ru.tech.imageresizershrinker.feature.filters.data.CrosshatchFilter
import ru.tech.imageresizershrinker.feature.filters.data.DiamondPixelationFilter
import ru.tech.imageresizershrinker.feature.filters.data.DilationFilter
import ru.tech.imageresizershrinker.feature.filters.data.EmbossFilter
import ru.tech.imageresizershrinker.feature.filters.data.EnhancedCirclePixelationFilter
import ru.tech.imageresizershrinker.feature.filters.data.EnhancedDiamondPixelationFilter
import ru.tech.imageresizershrinker.feature.filters.data.EnhancedPixelationFilter
import ru.tech.imageresizershrinker.feature.filters.data.ExposureFilter
import ru.tech.imageresizershrinker.feature.filters.data.FalseColorFilter
import ru.tech.imageresizershrinker.feature.filters.data.FastBlurFilter
import ru.tech.imageresizershrinker.feature.filters.data.GammaFilter
import ru.tech.imageresizershrinker.feature.filters.data.GaussianBlurFilter
import ru.tech.imageresizershrinker.feature.filters.data.GlassSphereRefractionFilter
import ru.tech.imageresizershrinker.feature.filters.data.HalftoneFilter
import ru.tech.imageresizershrinker.feature.filters.data.HazeFilter
import ru.tech.imageresizershrinker.feature.filters.data.HighlightsAndShadowsFilter
import ru.tech.imageresizershrinker.feature.filters.data.HueFilter
import ru.tech.imageresizershrinker.feature.filters.data.KuwaharaFilter
import ru.tech.imageresizershrinker.feature.filters.data.LaplacianFilter
import ru.tech.imageresizershrinker.feature.filters.data.LookupFilter
import ru.tech.imageresizershrinker.feature.filters.data.LuminanceThresholdFilter
import ru.tech.imageresizershrinker.feature.filters.data.MonochromeFilter
import ru.tech.imageresizershrinker.feature.filters.data.NegativeFilter
import ru.tech.imageresizershrinker.feature.filters.data.NonMaximumSuppressionFilter
import ru.tech.imageresizershrinker.feature.filters.data.OpacityFilter
import ru.tech.imageresizershrinker.feature.filters.data.PixelationFilter
import ru.tech.imageresizershrinker.feature.filters.data.PosterizeFilter
import ru.tech.imageresizershrinker.feature.filters.data.RGBFilter
import ru.tech.imageresizershrinker.feature.filters.data.RemoveColorFilter
import ru.tech.imageresizershrinker.feature.filters.data.ReplaceColorFilter
import ru.tech.imageresizershrinker.feature.filters.data.SaturationFilter
import ru.tech.imageresizershrinker.feature.filters.data.SepiaFilter
import ru.tech.imageresizershrinker.feature.filters.data.SharpenFilter
import ru.tech.imageresizershrinker.feature.filters.data.SketchFilter
import ru.tech.imageresizershrinker.feature.filters.data.SmoothToonFilter
import ru.tech.imageresizershrinker.feature.filters.data.SobelEdgeDetectionFilter
import ru.tech.imageresizershrinker.feature.filters.data.SolarizeFilter
import ru.tech.imageresizershrinker.feature.filters.data.SphereRefractionFilter
import ru.tech.imageresizershrinker.feature.filters.data.StackBlurFilter
import ru.tech.imageresizershrinker.feature.filters.data.StrokePixelationFilter
import ru.tech.imageresizershrinker.feature.filters.data.SwirlDistortionFilter
import ru.tech.imageresizershrinker.feature.filters.data.ToonFilter
import ru.tech.imageresizershrinker.feature.filters.data.VibranceFilter
import ru.tech.imageresizershrinker.feature.filters.data.VignetteFilter
import ru.tech.imageresizershrinker.feature.filters.data.WeakPixelFilter
import ru.tech.imageresizershrinker.feature.filters.data.WhiteBalanceFilter
import ru.tech.imageresizershrinker.feature.filters.data.ZoomBlurFilter
import javax.inject.Inject

internal class AndroidFilterProvider @Inject constructor(
    private val context: Context
) : FilterProvider<Bitmap> {

    override fun filterToTransformation(filter: Filter<Bitmap, *>): Transformation<Bitmap> {
        filter.run {
            return when (this) {
                is Filter.BilaterialBlur -> BilaterialBlurFilter(context, value)
                is Filter.BlackAndWhite -> BlackAndWhiteFilter(context)
                is Filter.BoxBlur -> BoxBlurFilter(context, value)
                is Filter.Brightness -> BrightnessFilter(context, value)
                is Filter.BulgeDistortion -> BulgeDistortionFilter(context, value)
                is Filter.CGAColorSpace -> CGAColorSpaceFilter(context)
                is Filter.CirclePixelation -> CirclePixelationFilter(value)
                is Filter.ColorBalance -> ColorBalanceFilter(context, value)
                is Filter.Color -> ColorFilter(value as Color)
                is Filter.ColorMatrix -> ColorMatrixFilter(context, value)
                is Filter.Contrast -> ContrastFilter(context, value)
                is Filter.Convolution3x3 -> Convolution3x3Filter(context, value)
                is Filter.Crosshatch -> CrosshatchFilter(context, value)
                is Filter.DiamondPixelation -> DiamondPixelationFilter(value)
                is Filter.Dilation -> DilationFilter(context, value)
                is Filter.Emboss -> EmbossFilter(context, value)
                is Filter.EnhancedCirclePixelation -> EnhancedCirclePixelationFilter(value)
                is Filter.EnhancedDiamondPixelation -> EnhancedDiamondPixelationFilter(value)
                is Filter.EnhancedPixelation -> EnhancedPixelationFilter(value)
                is Filter.Exposure -> ExposureFilter(context, value)
                is Filter.FalseColor<*, *> -> FalseColorFilter(context, value as Pair<Color, Color>)
                is Filter.FastBlur -> FastBlurFilter(value)
                is Filter.Gamma -> GammaFilter(context, value)
                is Filter.GaussianBlur -> GaussianBlurFilter(context, value)
                is Filter.GlassSphereRefraction -> GlassSphereRefractionFilter(context, value)
                is Filter.Halftone -> HalftoneFilter(context, value)
                is Filter.Haze -> HazeFilter(context, value)
                is Filter.HighlightsAndShadows -> HighlightsAndShadowsFilter(context, value)
                is Filter.Hue -> HueFilter(context, value)
                is Filter.Kuwahara -> KuwaharaFilter(context, value)
                is Filter.Laplacian -> LaplacianFilter(context)
                is Filter.Lookup -> LookupFilter(context, value)
                is Filter.LuminanceThreshold -> LuminanceThresholdFilter(context, value)
                is Filter.Monochrome -> MonochromeFilter(context, value)
                is Filter.Negative -> NegativeFilter(context)
                is Filter.NonMaximumSuppression -> NonMaximumSuppressionFilter(context)
                is Filter.Opacity -> OpacityFilter(context, value)
                is Filter.Pixelation -> PixelationFilter(value)
                is Filter.Posterize -> PosterizeFilter(context, value)
                is Filter.RemoveColor<*, *> -> RemoveColorFilter(value as Pair<Float, Color>)
                is Filter.ReplaceColor<*, *> -> ReplaceColorFilter(value as Triple<Float, Color, Color>)
                is Filter.RGB -> RGBFilter(context, value as Color)
                is Filter.Saturation -> SaturationFilter(context, value)
                is Filter.Sepia -> SepiaFilter(context, value)
                is Filter.Sharpen -> SharpenFilter(context, value)
                is Filter.Sketch -> SketchFilter(context, value)
                is Filter.SmoothToon -> SmoothToonFilter(context, value)
                is Filter.SobelEdgeDetection -> SobelEdgeDetectionFilter(context, value)
                is Filter.Solarize -> SolarizeFilter(context, value)
                is Filter.SphereRefraction -> SphereRefractionFilter(context, value)
                is Filter.StackBlur -> StackBlurFilter(value)
                is Filter.StrokePixelation -> StrokePixelationFilter(value)
                is Filter.SwirlDistortion -> SwirlDistortionFilter(context, value)
                is Filter.Toon -> ToonFilter(context, value)
                is Filter.Vibrance -> VibranceFilter(context, value)
                is Filter.Vignette -> VignetteFilter(context, value)
                is Filter.WeakPixel -> WeakPixelFilter(context)
                is Filter.WhiteBalance -> WhiteBalanceFilter(context, value)
                is Filter.ZoomBlur -> ZoomBlurFilter(context, value)

                else -> throw IllegalArgumentException("No filter implementation for interface ${filter::class.simpleName}")
            }
        }
    }


}