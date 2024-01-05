@file:Suppress("UNCHECKED_CAST")

package ru.tech.imageresizershrinker.core.data.image.filters.provider

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import ru.tech.imageresizershrinker.core.data.image.filters.BilaterialBlurFilter
import ru.tech.imageresizershrinker.core.data.image.filters.BlackAndWhiteFilter
import ru.tech.imageresizershrinker.core.data.image.filters.BoxBlurFilter
import ru.tech.imageresizershrinker.core.data.image.filters.BrightnessFilter
import ru.tech.imageresizershrinker.core.data.image.filters.BulgeDistortionFilter
import ru.tech.imageresizershrinker.core.data.image.filters.CGAColorSpaceFilter
import ru.tech.imageresizershrinker.core.data.image.filters.CirclePixelationFilter
import ru.tech.imageresizershrinker.core.data.image.filters.ColorBalanceFilter
import ru.tech.imageresizershrinker.core.data.image.filters.ColorFilter
import ru.tech.imageresizershrinker.core.data.image.filters.ColorMatrixFilter
import ru.tech.imageresizershrinker.core.data.image.filters.ContrastFilter
import ru.tech.imageresizershrinker.core.data.image.filters.Convolution3x3Filter
import ru.tech.imageresizershrinker.core.data.image.filters.CrosshatchFilter
import ru.tech.imageresizershrinker.core.data.image.filters.DiamondPixelationFilter
import ru.tech.imageresizershrinker.core.data.image.filters.DilationFilter
import ru.tech.imageresizershrinker.core.data.image.filters.EmbossFilter
import ru.tech.imageresizershrinker.core.data.image.filters.EnhancedCirclePixelationFilter
import ru.tech.imageresizershrinker.core.data.image.filters.EnhancedDiamondPixelationFilter
import ru.tech.imageresizershrinker.core.data.image.filters.EnhancedPixelationFilter
import ru.tech.imageresizershrinker.core.data.image.filters.ExposureFilter
import ru.tech.imageresizershrinker.core.data.image.filters.FalseColorFilter
import ru.tech.imageresizershrinker.core.data.image.filters.FastBlurFilter
import ru.tech.imageresizershrinker.core.data.image.filters.GammaFilter
import ru.tech.imageresizershrinker.core.data.image.filters.GaussianBlurFilter
import ru.tech.imageresizershrinker.core.data.image.filters.GlassSphereRefractionFilter
import ru.tech.imageresizershrinker.core.data.image.filters.HalftoneFilter
import ru.tech.imageresizershrinker.core.data.image.filters.HazeFilter
import ru.tech.imageresizershrinker.core.data.image.filters.HighlightsAndShadowsFilter
import ru.tech.imageresizershrinker.core.data.image.filters.HueFilter
import ru.tech.imageresizershrinker.core.data.image.filters.KuwaharaFilter
import ru.tech.imageresizershrinker.core.data.image.filters.LaplacianFilter
import ru.tech.imageresizershrinker.core.data.image.filters.LookupFilter
import ru.tech.imageresizershrinker.core.data.image.filters.LuminanceThresholdFilter
import ru.tech.imageresizershrinker.core.data.image.filters.MonochromeFilter
import ru.tech.imageresizershrinker.core.data.image.filters.NegativeFilter
import ru.tech.imageresizershrinker.core.data.image.filters.NonMaximumSuppressionFilter
import ru.tech.imageresizershrinker.core.data.image.filters.OpacityFilter
import ru.tech.imageresizershrinker.core.data.image.filters.PixelationFilter
import ru.tech.imageresizershrinker.core.data.image.filters.PosterizeFilter
import ru.tech.imageresizershrinker.core.data.image.filters.RGBFilter
import ru.tech.imageresizershrinker.core.data.image.filters.RemoveColorFilter
import ru.tech.imageresizershrinker.core.data.image.filters.ReplaceColorFilter
import ru.tech.imageresizershrinker.core.data.image.filters.SaturationFilter
import ru.tech.imageresizershrinker.core.data.image.filters.SepiaFilter
import ru.tech.imageresizershrinker.core.data.image.filters.SharpenFilter
import ru.tech.imageresizershrinker.core.data.image.filters.SketchFilter
import ru.tech.imageresizershrinker.core.data.image.filters.SmoothToonFilter
import ru.tech.imageresizershrinker.core.data.image.filters.SobelEdgeDetectionFilter
import ru.tech.imageresizershrinker.core.data.image.filters.SolarizeFilter
import ru.tech.imageresizershrinker.core.data.image.filters.SphereRefractionFilter
import ru.tech.imageresizershrinker.core.data.image.filters.StackBlurFilter
import ru.tech.imageresizershrinker.core.data.image.filters.StrokePixelationFilter
import ru.tech.imageresizershrinker.core.data.image.filters.SwirlDistortionFilter
import ru.tech.imageresizershrinker.core.data.image.filters.ToonFilter
import ru.tech.imageresizershrinker.core.data.image.filters.VibranceFilter
import ru.tech.imageresizershrinker.core.data.image.filters.VignetteFilter
import ru.tech.imageresizershrinker.core.data.image.filters.WeakPixelFilter
import ru.tech.imageresizershrinker.core.data.image.filters.WhiteBalanceFilter
import ru.tech.imageresizershrinker.core.data.image.filters.ZoomBlurFilter
import ru.tech.imageresizershrinker.core.domain.image.Transformation
import ru.tech.imageresizershrinker.core.domain.image.filters.Filter
import ru.tech.imageresizershrinker.core.domain.image.filters.provider.FilterProvider
import javax.inject.Inject

class AndroidFilterProvider @Inject constructor(
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