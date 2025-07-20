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

package com.t8rin.imagetoolbox.core.filters.presentation.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.t8rin.imagetoolbox.core.domain.utils.Quad
import com.t8rin.imagetoolbox.core.domain.utils.cast
import com.t8rin.imagetoolbox.core.filters.domain.model.BilaterialBlurParams
import com.t8rin.imagetoolbox.core.filters.domain.model.ChannelMixParams
import com.t8rin.imagetoolbox.core.filters.domain.model.ClaheParams
import com.t8rin.imagetoolbox.core.filters.domain.model.EnhancedZoomBlurParams
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterValueWrapper
import com.t8rin.imagetoolbox.core.filters.domain.model.GlitchParams
import com.t8rin.imagetoolbox.core.filters.domain.model.KaleidoscopeParams
import com.t8rin.imagetoolbox.core.filters.domain.model.LinearGaussianParams
import com.t8rin.imagetoolbox.core.filters.domain.model.LinearTiltShiftParams
import com.t8rin.imagetoolbox.core.filters.domain.model.RadialTiltShiftParams
import com.t8rin.imagetoolbox.core.filters.domain.model.SideFadeParams
import com.t8rin.imagetoolbox.core.filters.domain.model.ToneCurvesParams
import com.t8rin.imagetoolbox.core.filters.domain.model.WaterParams
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.BilaterialBlurParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.BooleanItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.ChannelMixParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.ClaheParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.EnhancedZoomBlurParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.FilterValueWrapperItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.FloatArrayItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.FloatItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.GlitchParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.KaleidoscopeParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.LinearGaussianParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.LinearTiltShiftParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.PairItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.QuadItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.RadialTiltShiftParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.SideFadeRelativeItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.ToneCurvesParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.TripleItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.WaterParamsItem

@Composable
internal fun <T> FilterItemContent(
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
        }
    }
}