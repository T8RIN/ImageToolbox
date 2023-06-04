@file:Suppress("UNCHECKED_CAST")

package ru.tech.imageresizershrinker.utils.coil.filters

import android.content.Context
import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import coil.transform.Transformation
import com.commit451.coiltransformations.gpu.GPUFilterTransformation
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
sealed class FilterTransformation<T>(
    private val context: @RawValue Context,
    @StringRes val title: Int,
    val valueRange: @RawValue ClosedFloatingPointRange<Float>,
    open val value: @RawValue T,
) : GPUFilterTransformation(context), Transformation, Parcelable {
    fun <T : Any> copy(value: T): FilterTransformation<*> {
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
            is SobelEdgeDetectionFilter -> SobelEdgeDetectionFilter(context)
            is HalftoneFilter -> HalftoneFilter(context, value as Float)
            is GCAColorSpaceFilter -> GCAColorSpaceFilter(context)
            is GaussianBlurFilter -> GaussianBlurFilter(context, value as Float)
            is BilaterialBlurFilter -> BilaterialBlurFilter(context, value as Float)
            is BoxBlurFilter -> BoxBlurFilter(context, value as Float)
            is EmbossFilter -> EmbossFilter(context, value as Float)
            is LaplacianFilter -> LaplacianFilter(context)
            is VignetteFilter -> VignetteFilter(context, value as Pair<Float, Float>)
            is KuwaharaFilter -> KuwaharaFilter(context, value as Float)
            is SlowBlurFilter -> SlowBlurFilter(context, value as Pair<Float, Int>)
            is SwirlDistortionEffect -> SwirlDistortionEffect(context, value as Pair<Float, Float>)
            is BulgeDistortionEffect -> BulgeDistortionEffect(context, value as Pair<Float, Float>)
            is DilationFilter -> DilationFilter(context, value as Float)

            is SphereRefractionFilter -> SphereRefractionFilter(
                context,
                value as Pair<Float, Float>
            )

            is GlassSphereRefractionFilter -> GlassSphereRefractionFilter(
                context,
                value as Pair<Float, Float>
            )

            is HighlightsAndShadowsFilter -> HighlightsAndShadowsFilter(
                context,
                value as Pair<Float, Float>
            )

            is ColorMatrixFilter -> ColorMatrixFilter(context, value as FloatArray)
            is OpacityFilter -> OpacityFilter(context, value as Float)
            is SketchFilter -> SketchFilter(context)
            is ToonFilter -> ToonFilter(context)
            is PosterizeFilter -> PosterizeFilter(context, value as Float)
            is SmoothToonFilter -> SmoothToonFilter(context)
        }
    }
}