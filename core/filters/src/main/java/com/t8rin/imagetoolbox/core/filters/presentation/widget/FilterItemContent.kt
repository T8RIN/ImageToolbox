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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderPreset
import com.t8rin.imagetoolbox.core.filters.presentation.model.FilterParameterEditor
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.AsciiParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.CropOrPerspectiveParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.DistortPerspectiveParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.LensProfilesLinkItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.NtscParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.RawGmicCommandsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.SeamCarvingParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.ShaderParamsItem
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.ToneCurvesParamsItem

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
        when (filter) {
            is FilterParameterEditor -> {
                filter.ParamsItem(onFilterChange, previewOnly)
                if (filter is Filter.RawGmic) RawGmicCommandsItem()
                if (filter is Filter.LensCorrection) LensProfilesLinkItem(previewOnly)
            }

            is Filter.Shader -> {
                ShaderParamsItem(
                    value = filter.value,
                    presets = shaderPresets,
                    onValueChange = onFilterChange,
                    onImportPreset = onImportShaderPreset,
                    previewOnly = previewOnly
                )
            }

            is Filter.ToneCurves -> {
                ToneCurvesParamsItem(
                    value = filter.value,
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is Filter.Ascii -> {
                AsciiParamsItem(
                    value = filter.value,
                    filter = filter,
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is Filter.CropOrPerspective -> {
                CropOrPerspectiveParamsItem(
                    value = filter.value,
                    filter = filter,
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is Filter.DistortPerspective -> {
                DistortPerspectiveParamsItem(
                    value = filter.value,
                    filter = filter,
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is Filter.SeamCarving -> {
                SeamCarvingParamsItem(
                    value = filter.value,
                    filter = filter,
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }

            is Filter.VHSNtsc -> {
                NtscParamsItem(
                    value = filter.value,
                    filter = filter,
                    onFilterChange = onFilterChange,
                    previewOnly = previewOnly
                )
            }
        }
    }
}