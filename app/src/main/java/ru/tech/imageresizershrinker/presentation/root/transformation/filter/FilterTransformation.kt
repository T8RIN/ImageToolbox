@file:Suppress("UNCHECKED_CAST")

package ru.tech.imageresizershrinker.presentation.root.transformation.filter

import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import ru.tech.imageresizershrinker.domain.image.Transformation
import ru.tech.imageresizershrinker.domain.image.filters.FilterParam

sealed class FilterTransformation<T>(
    private val context: Context,
    @StringRes val title: Int,
    val paramsInfo: List<FilterParam> = listOf(),
    open val value: T,
) : GPUFilterTransformation(context), Transformation<Bitmap> {

    constructor(
        context: Context,
        @StringRes title: Int,
        valueRange: ClosedFloatingPointRange<Float>,
        value: T,
    ) : this(
        context = context,
        title = title,
        paramsInfo = listOf(
            FilterParam(valueRange = valueRange)
        ),
        value = value
    )

    @Throws
    fun <T : Any> copy(value: T): FilterTransformation<*> {
        if (this.value == null) return newInstance()
        if (this.value!!::class.simpleName != value::class.simpleName) return newInstance()
        return when (this) {
            is BrightnessFilter -> BrightnessFilter(context, value as Float)
            is ContrastFilter -> ContrastFilter(context, value as Float)
            is HueFilter -> HueFilter(context, value as Float)
            is SaturationFilter -> SaturationFilter(context, value as Float)
            is ColorFilter -> ColorFilter(context, value as Color)
            is ExposureFilter -> ExposureFilter(context, value as Float)
            is WhiteBalanceFilter -> WhiteBalanceFilter(context, value as Pair<Float, Float>)
            is MonochromeFilter -> MonochromeFilter(context, value as Float)
            is GammaFilter -> GammaFilter(context, value as Float)
            is HazeFilter -> HazeFilter(context, value as Pair<Float, Float>)
            is SepiaFilter -> SepiaFilter(context, value as Float)
            is SharpenFilter -> SharpenFilter(context, value as Float)
            is NegativeFilter -> NegativeFilter(context)
            is SolarizeFilter -> SolarizeFilter(context, value as Float)
            is VibranceFilter -> VibranceFilter(context, value as Float)
            is BlackAndWhiteFilter -> BlackAndWhiteFilter(context)
            is CrosshatchFilter -> CrosshatchFilter(context, value as Pair<Float, Float>)
            is SobelEdgeDetectionFilter -> SobelEdgeDetectionFilter(context, value as Float)
            is HalftoneFilter -> HalftoneFilter(context, value as Float)
            is CGAColorSpaceFilter -> CGAColorSpaceFilter(context)
            is GaussianBlurFilter -> GaussianBlurFilter(context, value as Float)
            is BilaterialBlurFilter -> BilaterialBlurFilter(context, value as Float)
            is BoxBlurFilter -> BoxBlurFilter(context, value as Float)
            is EmbossFilter -> EmbossFilter(context, value as Float)
            is LaplacianFilter -> LaplacianFilter(context)
            is VignetteFilter -> VignetteFilter(context, value as Pair<Float, Float>)
            is KuwaharaFilter -> KuwaharaFilter(context, value as Float)
            is StackBlurFilter -> StackBlurFilter(context, value as Pair<Float, Int>)
            is DilationFilter -> DilationFilter(context, value as Float)
            is ColorMatrixFilter -> ColorMatrixFilter(context, value as FloatArray)
            is OpacityFilter -> OpacityFilter(context, value as Float)
            is SketchFilter -> SketchFilter(context)
            is ToonFilter -> ToonFilter(context)
            is PosterizeFilter -> PosterizeFilter(context, value as Float)
            is SmoothToonFilter -> SmoothToonFilter(context, value as Triple<Float, Float, Float>)
            is LookupFilter -> LookupFilter(context, value as Float)
            is NonMaximumSuppressionFilter -> NonMaximumSuppressionFilter(context)
            is WeakPixelFilter -> WeakPixelFilter(context)
            is ColorBalanceFilter -> ColorBalanceFilter(context, value as FloatArray)
            is Convolution3x3Filter -> Convolution3x3Filter(context, value as FloatArray)
            is FalseColorFilter -> FalseColorFilter(context, value as Pair<Color, Color>)
            is FastBlurFilter -> FastBlurFilter(context, value as Pair<Float, Int>)
            is LuminanceThresholdFilter -> LuminanceThresholdFilter(context, value as Float)
            is RGBFilter -> RGBFilter(context, value as Color)
            is ZoomBlurFilter -> ZoomBlurFilter(context, value as Triple<Float, Float, Float>)
            is SwirlDistortionEffect -> SwirlDistortionEffect(
                context,
                value as Pair<Float, Float>
            )

            is BulgeDistortionEffect -> BulgeDistortionEffect(
                context = context,
                value = value as Pair<Float, Float>
            )

            is SphereRefractionFilter -> SphereRefractionFilter(
                context = context,
                value = value as Pair<Float, Float>
            )

            is GlassSphereRefractionFilter -> GlassSphereRefractionFilter(
                context = context,
                value = value as Pair<Float, Float>
            )

            is HighlightsAndShadowsFilter -> HighlightsAndShadowsFilter(
                context = context,
                value = value as Pair<Float, Float>
            )

            is PixelationFilter -> PixelationFilter(
                context = context,
                value = value as Float
            )

            is EnhancedPixelationFilter -> EnhancedPixelationFilter(
                context = context,
                value = value as Float
            )

            is StrokePixelationFilter -> StrokePixelationFilter(
                context = context,
                value = value as Float
            )

            is CirclePixelationFilter -> CirclePixelationFilter(
                context = context,
                value = value as Float
            )

            is DiamondPixelationFilter -> DiamondPixelationFilter(
                context = context,
                value = value as Float
            )

            is EnhancedCirclePixelationFilter -> EnhancedCirclePixelationFilter(
                context = context,
                value = value as Float
            )

            is EnhancedDiamondPixelationFilter -> EnhancedDiamondPixelationFilter(
                context = context,
                value = value as Float
            )

            is ReplaceColorFilter -> ReplaceColorFilter(
                context = context,
                value = value as Triple<Float, Color, Color>
            )
        }
    }

    fun newInstance(): FilterTransformation<*> {
        return when (this) {
            is BrightnessFilter -> BrightnessFilter(context)
            is ContrastFilter -> ContrastFilter(context)
            is HueFilter -> HueFilter(context)
            is SaturationFilter -> SaturationFilter(context)
            is ColorFilter -> ColorFilter(context)
            is ExposureFilter -> ExposureFilter(context)
            is WhiteBalanceFilter -> WhiteBalanceFilter(context)
            is MonochromeFilter -> MonochromeFilter(context)
            is GammaFilter -> GammaFilter(context)
            is HazeFilter -> HazeFilter(context)
            is SepiaFilter -> SepiaFilter(context)
            is SharpenFilter -> SharpenFilter(context)
            is NegativeFilter -> NegativeFilter(context)
            is SolarizeFilter -> SolarizeFilter(context)
            is VibranceFilter -> VibranceFilter(context)
            is BlackAndWhiteFilter -> BlackAndWhiteFilter(context)
            is CrosshatchFilter -> CrosshatchFilter(context)
            is SobelEdgeDetectionFilter -> SobelEdgeDetectionFilter(context)
            is HalftoneFilter -> HalftoneFilter(context)
            is CGAColorSpaceFilter -> CGAColorSpaceFilter(context)
            is GaussianBlurFilter -> GaussianBlurFilter(context)
            is BilaterialBlurFilter -> BilaterialBlurFilter(context)
            is BoxBlurFilter -> BoxBlurFilter(context)
            is EmbossFilter -> EmbossFilter(context)
            is LaplacianFilter -> LaplacianFilter(context)
            is VignetteFilter -> VignetteFilter(context)
            is KuwaharaFilter -> KuwaharaFilter(context)
            is StackBlurFilter -> StackBlurFilter(context)
            is DilationFilter -> DilationFilter(context)
            is ColorMatrixFilter -> ColorMatrixFilter(context)
            is OpacityFilter -> OpacityFilter(context)
            is SketchFilter -> SketchFilter(context)
            is ToonFilter -> ToonFilter(context)
            is PosterizeFilter -> PosterizeFilter(context)
            is SmoothToonFilter -> SmoothToonFilter(context)
            is LookupFilter -> LookupFilter(context)
            is NonMaximumSuppressionFilter -> NonMaximumSuppressionFilter(context)
            is WeakPixelFilter -> WeakPixelFilter(context)
            is ColorBalanceFilter -> ColorBalanceFilter(context)
            is Convolution3x3Filter -> Convolution3x3Filter(context)
            is FalseColorFilter -> FalseColorFilter(context)
            is FastBlurFilter -> FastBlurFilter(context)
            is LuminanceThresholdFilter -> LuminanceThresholdFilter(context)
            is RGBFilter -> RGBFilter(context)
            is ZoomBlurFilter -> ZoomBlurFilter(context)
            is SwirlDistortionEffect -> SwirlDistortionEffect(context)
            is BulgeDistortionEffect -> BulgeDistortionEffect(context)
            is SphereRefractionFilter -> SphereRefractionFilter(context)
            is GlassSphereRefractionFilter -> GlassSphereRefractionFilter(context)
            is HighlightsAndShadowsFilter -> HighlightsAndShadowsFilter(context)
            is PixelationFilter -> PixelationFilter(context)
            is EnhancedPixelationFilter -> EnhancedPixelationFilter(context)
            is StrokePixelationFilter -> StrokePixelationFilter(context)
            is CirclePixelationFilter -> CirclePixelationFilter(context)
            is DiamondPixelationFilter -> DiamondPixelationFilter(context)
            is EnhancedCirclePixelationFilter -> EnhancedCirclePixelationFilter(context)
            is EnhancedDiamondPixelationFilter -> EnhancedDiamondPixelationFilter(context)
            is ReplaceColorFilter -> ReplaceColorFilter(context)
        }
    }

}

infix fun Int.paramTo(valueRange: ClosedFloatingPointRange<Float>) = FilterParam(
    title = this,
    valueRange = valueRange
)