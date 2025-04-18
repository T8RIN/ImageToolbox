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

package ru.tech.imageresizershrinker.core.filters.presentation.model

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ru.tech.imageresizershrinker.core.filters.domain.model.Filter
import ru.tech.imageresizershrinker.core.filters.domain.model.FilterParam
import kotlin.reflect.full.primaryConstructor

sealed class UiFilter<T>(
    @StringRes val title: Int,
    val paramsInfo: List<FilterParam> = listOf(),
    override val value: T,
) : Filter<T> {

    override var isVisible: Boolean by mutableStateOf(true)

    constructor(
        @StringRes title: Int,
        valueRange: ClosedFloatingPointRange<Float>,
        value: T,
    ) : this(
        title = title,
        paramsInfo = listOf(
            FilterParam(valueRange = valueRange)
        ),
        value = value
    )

    fun <T : Any> copy(value: T): UiFilter<*> {
        if (this.value == null) return newInstance()
        if (this.value!!::class.simpleName != value::class.simpleName) return newInstance()
        return runCatching {
            this::class.primaryConstructor?.run {
                callBy(mapOf(parameters[0] to value))
            }
        }.getOrNull()?.also { it.isVisible = this.isVisible } ?: newInstance()
    }

    fun newInstance(): UiFilter<*> {
        val instance = this::class.primaryConstructor!!.callBy(emptyMap())

        return if (this.value is Unit) {
            instance.also { it.isVisible = this.isVisible }
        } else instance
    }

    companion object {
        val groupedEntries by lazy {
            listOf(
                listOf(
                    UiSepiaFilter(),
                    UiNegativeFilter(),
                    UiBlackAndWhiteFilter(),
                    UiCGAColorSpaceFilter(),
                    UiLaplacianFilter(),
                    UiNonMaximumSuppressionFilter(),
                    UiWeakPixelFilter(),
                    UiTritanopiaFilter(),
                    UiDeutaronotopiaFilter(),
                    UiProtanopiaFilter(),
                    UiTritonomalyFilter(),
                    UiDeutaromalyFilter(),
                    UiProtonomalyFilter(),
                    UiVintageFilter(),
                    UiBrowniFilter(),
                    UiCodaChromeFilter(),
                    UiNightVisionFilter(),
                    UiWarmFilter(),
                    UiCoolFilter(),
                    UiPolaroidFilter(),
                    UiAchromatopsiaFilter(),
                    UiAchromatomalyFilter(),
                    UiPastelFilter(),
                    UiOrangeHazeFilter(),
                    UiPinkDreamFilter(),
                    UiGoldenHourFilter(),
                    UiHotSummerFilter(),
                    UiPurpleMistFilter(),
                    UiSunriseFilter(),
                    UiColorfulSwirlFilter(),
                    UiSoftSpringLightFilter(),
                    UiAutumnTonesFilter(),
                    UiLavenderDreamFilter(),
                    UiCyberpunkFilter(),
                    UiLemonadeLightFilter(),
                    UiSpectralFireFilter(),
                    UiNightMagicFilter(),
                    UiFantasyLandscapeFilter(),
                    UiColorExplosionFilter(),
                    UiElectricGradientFilter(),
                    UiCaramelDarknessFilter(),
                    UiFuturisticGradientFilter(),
                    UiGreenSunFilter(),
                    UiRainbowWorldFilter(),
                    UiDeepPurpleFilter(),
                    UiSpacePortalFilter(),
                    UiRedSwirlFilter(),
                    UiDigitalCodeFilter(),
                    UiOldTvFilter(),
                    UiEqualizeHistogramFilter(),
                    UiSimpleOldTvFilter(),
                    UiGothamFilter(),
                    UiHDRFilter(),
                    UiSimpleSketchFilter(),
                    UiSobelSimpleFilter(),
                    UiLaplacianSimpleFilter()
                ),
                listOf(
                    UiHueFilter(),
                    UiColorFilter(),
                    UiNeonFilter(),
                    UiSaturationFilter(),
                    UiRGBFilter(),
                    UiReplaceColorFilter(),
                    UiRemoveColorFilter(),
                    UiFalseColorFilter(),
                    UiGrayscaleFilter(),
                    UiMonochromeFilter(),
                    UiColorMatrix4x4Filter(),
                    UiColorMatrix3x3Filter(),
                    UiColorBalanceFilter(),
                    UiPaletteTransferFilter(),
                    UiPaletteTransferVariantFilter(),
                    UiPosterizeFilter(),
                    UiColorPosterFilter(),
                    UiTriToneFilter(),
                    UiPopArtFilter(),
                    UiToneCurvesFilter()
                ),
                listOf(
                    UiLUT512x512Filter(),
                    UiAmatorkaFilter(),
                    UiMissEtikateFilter(),
                    UiSoftEleganceFilter(),
                    UiSoftEleganceVariantFilter(),
                    UiCubeLutFilter(),
                    UiBleachBypassFilter(),
                    UiCandlelightFilter(),
                    UiDropBluesFilter(),
                    UiEdgyAmberFilter(),
                    UiFallColorsFilter(),
                    UiFilmStock50Filter(),
                    UiFoggyNightFilter(),
                    UiKodakFilter(),
                    UiCelluloidFilter(),
                    UiCoffeeFilter(),
                    UiGoldenForestFilter(),
                    UiGreenishFilter(),
                    UiRetroYellowFilter()
                ),
                listOf(
                    UiBrightnessFilter(),
                    UiContrastFilter(),
                    UiVibranceFilter(),
                    UiExposureFilter(),
                    UiWhiteBalanceFilter(),
                    UiGammaFilter(),
                    UiHighlightsAndShadowsFilter(),
                    UiSolarizeFilter(),
                    UiHazeFilter(),
                    UiDehazeFilter(),
                    UiLogarithmicToneMappingFilter(),
                    UiAcesFilmicToneMappingFilter(),
                    UiAcesHillToneMappingFilter(),
                    UiHableFilmicToneMappingFilter(),
                    UiHejlBurgessToneMappingFilter(),
                    UiEqualizeHistogramAdaptiveFilter(),
                    UiEqualizeHistogramAdaptiveLUVFilter(),
                    UiEqualizeHistogramAdaptiveLABFilter(),
                    UiEqualizeHistogramAdaptiveHSLFilter(),
                    UiEqualizeHistogramAdaptiveHSVFilter(),
                    UiEqualizeHistogramHSVFilter(),
                    UiClaheHSVFilter(),
                    UiClaheHSLFilter(),
                    UiClaheFilter(),
                    UiClaheLABFilter(),
                    UiClaheLUVFilter(),
                    UiMobiusFilter(),
                    UiAldridgeFilter(),
                    UiUchimuraFilter(),
                    UiDragoFilter(),
                    UiClaheOklabFilter(),
                    UiClaheOklchFilter(),
                    UiClaheJzazbzFilter(),
                    UiAutoRemoveRedEyesFilter()
                ),
                listOf(
                    UiNoiseFilter(),
                    UiAnisotropicDiffusionFilter(),
                    UiSharpenFilter(),
                    UiUnsharpFilter(),
                    UiGrainFilter(),
                    UiSobelEdgeDetectionFilter(),
                    UiCannyFilter(),
                    UiOilFilter(),
                    UiEnhancedOilFilter(),
                    UiEmbossFilter(),
                    UiVignetteFilter(),
                    UiKuwaharaFilter(),
                    UiErodeFilter(),
                    UiDilationFilter(),
                    UiOpeningFilter(),
                    UiClosingFilter(),
                    UiMorphologicalGradientFilter(),
                    UiTopHatFilter(),
                    UiBlackHatFilter(),
                    UiOpacityFilter(),
                    UiSideFadeFilter(),
                    UiCropToContentFilter(),
                    UiAutoCropFilter(),
                    UiToonFilter(),
                    UiSmoothToonFilter(),
                    UiSketchFilter(),
                    UiLookupFilter(),
                    UiConvolution3x3Filter(),
                    UiThresholdFilter()
                ),
                listOf(
                    UiShuffleBlurFilter(),
                    UiRingBlurFilter(),
                    UiCircleBlurFilter(),
                    UiCrossBlurFilter(),
                    UiStarBlurFilter(),
                    UiRadialTiltShiftFilter(),
                    UiLinearTiltShiftFilter(),
                    UiGaussianBlurFilter(),
                    UiNativeStackBlurFilter(),
                    UiBoxBlurFilter(),
                    UiBilaterialBlurFilter(),
                    UiTentBlurFilter(),
                    UiStackBlurFilter(),
                    UiFastBlurFilter(),
                    UiZoomBlurFilter(),
                    UiEnhancedZoomBlurFilter(),
                    UiFastBilaterialBlurFilter(),
                    UiPoissonBlurFilter(),
                    UiMedianBlurFilter(),
                    UiBokehFilter(),
                    UiFastGaussianBlur2DFilter(),
                    UiFastGaussianBlur3DFilter(),
                    UiFastGaussianBlur4DFilter(),
                    UiLinearBoxBlurFilter(),
                    UiLinearTentBlurFilter(),
                    UiLinearGaussianBoxBlurFilter(),
                    UiLinearStackBlurFilter(),
                    UiGaussianBoxBlurFilter(),
                    UiLinearFastGaussianBlurNextFilter(),
                    UiLinearFastGaussianBlurFilter(),
                    UiLinearGaussianBlurFilter(),
                    UiMotionBlurFilter()
                ),
                listOf(
                    UiCrystallizeFilter(),
                    UiEqualizeHistogramPixelationFilter(),
                    UiPixelationFilter(),
                    UiEnhancedPixelationFilter(),
                    UiDiamondPixelationFilter(),
                    UiEnhancedDiamondPixelationFilter(),
                    UiCirclePixelationFilter(),
                    UiEnhancedCirclePixelationFilter(),
                    UiStrokePixelationFilter(),
                    UiLowPolyFilter(),
                    UiSandPaintingFilter(),
                    UiPolkaDotFilter()
                ),
                listOf(
                    UiEnhancedGlitchFilter(),
                    UiFractalGlassFilter(),
                    UiGlitchFilter(),
                    UiMarbleFilter(),
                    UiConvexFilter(),
                    UiColorAnomalyFilter(),
                    UiWaterEffectFilter(),
                    UiPerlinDistortionFilter(),
                    UiAnaglyphFilter(),
                    UiHorizontalWindStaggerFilter(),
                    UiSwirlDistortionFilter(),
                    UiBulgeDistortionFilter(),
                    UiSphereRefractionFilter(),
                    UiGlassSphereRefractionFilter()
                ),
                listOf(
                    UiHalftoneFilter(),
                    UiCrosshatchFilter(),
                    UiBayerTwoDitheringFilter(),
                    UiBayerThreeDitheringFilter(),
                    UiBayerFourDitheringFilter(),
                    UiBayerEightDitheringFilter(),
                    UiFloydSteinbergDitheringFilter(),
                    UiJarvisJudiceNinkeDitheringFilter(),
                    UiSierraDitheringFilter(),
                    UiTwoRowSierraDitheringFilter(),
                    UiSierraLiteDitheringFilter(),
                    UiAtkinsonDitheringFilter(),
                    UiStuckiDitheringFilter(),
                    UiBurkesDitheringFilter(),
                    UiFalseFloydSteinbergDitheringFilter(),
                    UiLeftToRightDitheringFilter(),
                    UiRandomDitheringFilter(),
                    UiSimpleThresholdDitheringFilter(),
                    UiQuantizierFilter(),
                    UiClustered2x2DitheringFilter(),
                    UiClustered4x4DitheringFilter(),
                    UiClustered8x8DitheringFilter(),
                    UiYililomaDitheringFilter()
                )
            )
        }

        fun groupedEntries(
            context: Context,
        ) = groupedEntries.map { list ->
            list.sortedBy { context.getString(it.title) }
        }
    }

}

private val sealedValues = UiFilter::class.sealedSubclasses

fun Filter<*>.toUiFilter(
    preserveVisibility: Boolean = true
): UiFilter<*> = sealedValues.first {
    it.java.isAssignableFrom(this::class.java)
}.primaryConstructor!!.run {
    if (parameters.isNotEmpty()) callBy(mapOf(parameters[0] to value))
    else callBy(emptyMap())
}.also {
    if (preserveVisibility) {
        it.isVisible = isVisible
    }
}


infix fun Int.paramTo(valueRange: ClosedFloatingPointRange<Float>) = FilterParam(
    title = this,
    valueRange = valueRange
)

//private suspend fun reflectionTest() = coroutineScope {
//    val filters = UiFilter.groupedEntries.flatten()
//    val failedCopy = mutableListOf<Pair<String, String?>>()
//    val failedToUi = mutableListOf<Pair<String, String?>>()
//    filters.forEach { filter ->
//        runCatching {
//            filter.copy(filter.value)
//        }.onFailure {
//            failedCopy.add(filter::class.simpleName.toString() to it.message)
//        }
//        runCatching {
//            filter.toUiFilter()
//        }.onFailure {
//            failedToUi.add(filter::class.simpleName.toString() to it.message)
//        }
//    }
//    "------------------".makeLog()
//    failedCopy.makeLog()
//    " ".makeLog()
//    failedToUi.makeLog()
//    "------------------".makeLog()
//}