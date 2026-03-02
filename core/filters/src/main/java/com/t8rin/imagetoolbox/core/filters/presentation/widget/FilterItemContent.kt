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

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.utils.Quad
import com.t8rin.imagetoolbox.core.domain.utils.cast
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterValueWrapper
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.PolarCoordinatesType
import com.t8rin.imagetoolbox.core.filters.domain.model.params.ArcParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.AsciiParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.BilaterialBlurParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.BloomParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.ChannelMixParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.ClaheParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.CropOrPerspectiveParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.EnhancedZoomBlurParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.GlitchParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.KaleidoscopeParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.LinearGaussianParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.LinearTiltShiftParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.PinchParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.RadialTiltShiftParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.RubberStampParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.SideFadeParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.SmearParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.SparkleParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.ToneCurvesParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.VoronoiCrystallizeParams
import com.t8rin.imagetoolbox.core.filters.domain.model.params.WaterParams
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.filters.presentation.utils.translatedName
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.ArcParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.AsciiParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.BilaterialBlurParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.BloomParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.BooleanItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.ChannelMixParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.ClaheParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.CropOrPerspectiveParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.EnhancedZoomBlurParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.FilterValueWrapperItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.FloatArrayItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.FloatItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.GlitchParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.IntegerSizeParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.KaleidoscopeParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.LinearGaussianParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.LinearTiltShiftParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.PairItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.PinchParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.QuadItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.RadialTiltShiftParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.RubberStampParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.SideFadeRelativeItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.SmearParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.SparkleParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.ToneCurvesParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.TripleItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.VoronoiCrystallizeParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.WaterParamsItem
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup

@Composable
internal fun <T : Any> FilterItemContent(
    filter: UiFilter<T>,
    onFilterChange: (value: Any) -> Unit,
    modifier: Modifier = Modifier,
    previewOnly: Boolean = false,
) {
    Column(
        modifier = modifier
    ) {
        when (val value = filter.value) {
            is FilterValueWrapper<*> -> {
                FilterValueWrapperItem(
                    value = value,
                    filter = filter.cast(),
                    onFilterChange = onFilterChange,
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

            is IntegerSize -> {
                IntegerSizeParamsItem(
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
        }
    }
}