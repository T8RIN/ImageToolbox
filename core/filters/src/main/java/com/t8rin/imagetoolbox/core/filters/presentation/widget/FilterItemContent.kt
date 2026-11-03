/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.core.filters.presentation.widget

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.GradientPalette
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.utils.Quad
import com.t8rin.imagetoolbox.core.domain.utils.cast
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterValueWrapper
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.PolarCoordinatesType
import com.t8rin.imagetoolbox.core.filters.domain.model.params.AdaptiveBinarizationParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.AgxToneMappingParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.AnamorphicStreaksParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.ArcParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.AsciiParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.AutostereogramParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.BayerSensorParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.BilaterialBlurParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.BloomParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.CapillaryInkBleedParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.ChannelMixParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.ClaheParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.CmykPressParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.ContourHatchingParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.CropOrPerspectiveParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.DaltonizationParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.DctGlitchParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.DiffractionPsfParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.DirectionalEmbroideryParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.DisplacementParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.DistortPerspectiveParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.DropShadowParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.EnhancedZoomBlurParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.FilmGrainParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.FilmHalationParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.FilmNegativeParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.FlareParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.FourierPhaseScrambleParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GlitchParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GmicFilterParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GuidedSmoothingParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.KaleidoscopeParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.LayeredPaperCutParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.LcdSubpixelsParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.LinearGaussianParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.LinearTiltShiftParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.NtscParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.PalCompositeParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.PaletteRemapParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.PhotoCrossStitchParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.PinchParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.ProceduralParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.RadialTiltShiftParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.RetroHardwareParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.RfReceptionParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.RisographParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.RubberStampParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.SeamCarvingParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.SecamParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.ShaderParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.ShearParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.SideFadeParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.SignedDistanceFieldParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.SingleLineDrawingParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.SmearParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.SparkleParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.SstvReceptionParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.ThermalReceiptParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.ToneCurvesParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.TornEdgeParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.VoronoiCrystallizeParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.WaterDropParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.WaterParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.WienerDeconvolutionParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.XdogParams
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderPreset
import com.t8rin.imagetoolbox.core.filters.presentation.model.GmicUiFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiProceduralFilter
import com.t8rin.imagetoolbox.core.filters.presentation.utils.translatedName
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.AdaptiveBinarizationParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.AgxToneMappingParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.AnamorphicStreaksParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.ArcParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.AsciiParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.AutostereogramParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.BayerSensorParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.BilaterialBlurParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.BloomParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.BooleanItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.CapillaryInkBleedParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.ChannelMixParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.ClaheParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.CmykPressParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.ContourHatchingParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.CropOrPerspectiveParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.DaltonizationParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.DctGlitchParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.DiffractionPsfParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.DirectionalEmbroideryParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.DisplacementParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.DistortPerspectiveParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.DropShadowParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.EnhancedZoomBlurParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.FilmGrainParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.FilmHalationParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.FilmNegativeParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.FilterValueWrapperItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.FlareParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.FloatArrayItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.FloatItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.FourierPhaseScrambleParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.GlitchParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.GmicFilterParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.GuidedSmoothingParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.IntegerSizeParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.KaleidoscopeParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.LayeredPaperCutParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.LcdSubpixelsParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.LinearGaussianParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.LinearTiltShiftParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.NtscParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.PairItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.PalCompositeParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.PaletteRemapParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.PhotoCrossStitchParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.PinchParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.ProceduralParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.QuadItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.RadialTiltShiftParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.RetroHardwareParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.RfReceptionParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.RisographParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.RubberStampParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.SeamCarvingParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.SecamParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.ShaderParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.ShearParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.SideFadeRelativeItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.SignedDistanceFieldParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.SingleLineDrawingParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.SmearParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.SparkleParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.SstvReceptionParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.StringItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.ThermalReceiptParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.ToneCurvesParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.TornEdgeParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.TripleItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.VoronoiCrystallizeParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.WaterDropParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.WaterParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.WienerDeconvolutionParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.XdogParamsItem
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
import com.t8rin.imagetoolbox.core.ui.widget.palette_selection.GradientPaletteSelector

@Composable
internal fun <T : Any> FilterItemContent(
    filter: UiFilter<T>,
    onFilterChange: (value: Any) -> Unit,
    modifier: Modifier = Modifier,
    shaderPresets: List<ShaderPreset> = emptyList(),
    onImportShaderPreset: (suspend (Uri) -> ShaderPreset?)? = null,
    previewOnly: Boolean = false,
) {
    Column(
        modifier = modifier
    ) {
        when (val value = filter.value) {
            is ProceduralParams -> {
                ProceduralParamsItem(
                    value = value,
                    filter = filter as UiProceduralFilter,
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is AdaptiveBinarizationParams -> {
                AdaptiveBinarizationParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is AgxToneMappingParams -> {
                AgxToneMappingParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is AnamorphicStreaksParams -> {
                AnamorphicStreaksParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is AutostereogramParams -> {
                AutostereogramParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is BayerSensorParams -> {
                BayerSensorParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is CapillaryInkBleedParams -> {
                CapillaryInkBleedParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is CmykPressParams -> {
                CmykPressParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is ContourHatchingParams -> {
                ContourHatchingParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is DaltonizationParams -> {
                DaltonizationParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is DctGlitchParams -> {
                DctGlitchParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is DiffractionPsfParams -> {
                DiffractionPsfParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is DirectionalEmbroideryParams -> {
                DirectionalEmbroideryParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is FilmGrainParams -> {
                FilmGrainParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is FilmHalationParams -> {
                FilmHalationParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is FilmNegativeParams -> {
                FilmNegativeParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is FourierPhaseScrambleParams -> {
                FourierPhaseScrambleParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is GuidedSmoothingParams -> {
                GuidedSmoothingParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is LayeredPaperCutParams -> {
                LayeredPaperCutParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is LcdSubpixelsParams -> {
                LcdSubpixelsParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is PalCompositeParams -> {
                PalCompositeParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is PaletteRemapParams -> {
                PaletteRemapParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is PhotoCrossStitchParams -> {
                PhotoCrossStitchParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is RetroHardwareParams -> {
                RetroHardwareParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is RfReceptionParams -> {
                RfReceptionParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is RisographParams -> {
                RisographParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is SecamParams -> {
                SecamParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is SignedDistanceFieldParams -> {
                SignedDistanceFieldParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is SingleLineDrawingParams -> {
                SingleLineDrawingParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is SstvReceptionParams -> {
                SstvReceptionParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is ThermalReceiptParams -> {
                ThermalReceiptParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is WienerDeconvolutionParams -> {
                WienerDeconvolutionParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is XdogParams -> {
                XdogParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is GmicFilterParams -> {
                GmicFilterParamsItem(
                    value = value,
                    filter = filter as GmicUiFilter,
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is DisplacementParams -> {
                DisplacementParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is String -> {
                StringItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is FilterValueWrapper<*> -> {
                FilterValueWrapperItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is ShaderParams -> {
                ShaderParamsItem(
                    value = value,
                    presets = shaderPresets,
                    onValueChange = onFilterChange,
                    onImportPreset = onImportShaderPreset,
                    previewOnly = previewOnly
                )
            }

            is FloatArray -> {
                FloatArrayItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is Float -> {
                FloatItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is Boolean -> {
                BooleanItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is PolarCoordinatesType -> {
                EnhancedButtonGroup(
                    inactiveButtonColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    items = PolarCoordinatesType.entries.map { it.translatedName },
                    selectedIndex = PolarCoordinatesType.entries.indexOf(value),
                    onIndexChange = {
                        onFilterChange(PolarCoordinatesType.entries[it])
                    }
                )
            }

            is GradientPalette -> {
                GradientPaletteSelector(
                    value = value,
                    onValueChange = onFilterChange,
                    modifier = Modifier.padding(
                        start = 8.dp,
                        end = 8.dp,
                        bottom = 8.dp
                    )
                )
            }

            is Pair<*, *> -> {
                PairItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is Triple<*, *, *> -> {
                TripleItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is Quad<*, *, *, *> -> {
                QuadItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is RadialTiltShiftParams -> {
                RadialTiltShiftParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is LinearTiltShiftParams -> {
                LinearTiltShiftParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is GlitchParams -> {
                GlitchParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is SideFadeParams.Relative -> {
                SideFadeRelativeItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is WaterParams -> {
                WaterParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is EnhancedZoomBlurParams -> {
                EnhancedZoomBlurParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is ClaheParams -> {
                ClaheParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is LinearGaussianParams -> {
                LinearGaussianParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is ToneCurvesParams -> {
                ToneCurvesParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is BilaterialBlurParams -> {
                BilaterialBlurParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is KaleidoscopeParams -> {
                KaleidoscopeParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is ChannelMixParams -> {
                ChannelMixParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is VoronoiCrystallizeParams -> {
                VoronoiCrystallizeParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is PinchParams -> {
                PinchParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is RubberStampParams -> {
                RubberStampParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is SmearParams -> {
                SmearParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is ArcParams -> {
                ArcParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is SparkleParams -> {
                SparkleParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is AsciiParams -> {
                AsciiParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is CropOrPerspectiveParams -> {
                CropOrPerspectiveParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is DistortPerspectiveParams -> {
                DistortPerspectiveParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is FlareParams -> {
                FlareParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is ShearParams -> {
                ShearParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is WaterDropParams -> {
                WaterDropParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is IntegerSize -> {
                IntegerSizeParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is SeamCarvingParams -> {
                SeamCarvingParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is BloomParams -> {
                BloomParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is NtscParams -> {
                NtscParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is DropShadowParams -> {
                DropShadowParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is TornEdgeParams -> {
                TornEdgeParamsItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }
        }
    }
}
