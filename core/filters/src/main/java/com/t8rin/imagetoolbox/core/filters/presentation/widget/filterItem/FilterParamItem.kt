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

package com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.domain.model.GradientPalette
import com.t8rin.imagetoolbox.core.domain.utils.roundTo
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterParam
import com.t8rin.imagetoolbox.core.ui.utils.helper.toColor
import com.t8rin.imagetoolbox.core.ui.utils.helper.toModel
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ColorRowSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.DataSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.SeedSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.palette_selection.GradientPaletteSelector
import kotlin.math.roundToInt

@Composable
fun FilterParamItem(
    value: Float,
    info: FilterParam,
    previewOnly: Boolean,
    onValueChange: (Float) -> Unit
) {
    val title = info.title?.let { stringResource(it) }.orEmpty()
    val current = value.takeIf(Float::isFinite)?.coerceIn(info.valueRange) ?: info.valueRange.start
    when {
        info.options.size == 2 -> EnhancedButtonGroup(
            items = info.options.map { stringResource(it) },
            selectedIndex = current.roundToInt().coerceIn(0, 1),
            onIndexChange = { onValueChange(it.toFloat()) },
            title = title,
            enabled = !previewOnly,
            isScrollable = false
        )

        info.options.isNotEmpty() -> {
            val labels = info.options.map { stringResource(it) }
            DataSelector(
                value = current.roundToInt().coerceIn(labels.indices),
                onValueChange = { onValueChange(it.toFloat()) },
                entries = labels.indices.toList(),
                title = title,
                titleIcon = null,
                itemContentText = { labels[it] },
                spanCount = 1,
                containerColor = Color.Unspecified,
                behaveAsContainer = false,
                enabled = !previewOnly
            )
        }

        info.isSeed -> SeedSelector(
            value = current,
            title = title,
            valueRange = info.valueRange,
            roundTo = info.roundTo,
            onValueChange = onValueChange,
            enabled = !previewOnly,
            icon = null,
            behaveAsContainer = false
        )

        else -> EnhancedSliderItem(
            value = current,
            title = title,
            valueRange = info.valueRange,
            onValueChange = { onValueChange(it.roundTo(info.roundTo)) },
            internalStateTransformation = { it.roundTo(info.roundTo) },
            enabled = !previewOnly,
            behaveAsContainer = false
        )
    }
}

@Composable
fun FilterColorParamItem(
    value: ColorModel,
    info: FilterParam,
    previewOnly: Boolean,
    allowAlpha: Boolean = false,
    onValueChange: (ColorModel) -> Unit
) {
    ColorRowSelector(
        value = value.toColor(),
        onValueChange = { if (!previewOnly) onValueChange(it.toModel()) },
        title = stringResource(requireNotNull(info.title)),
        allowCustom = !previewOnly,
        allowScroll = !previewOnly,
        allowAlpha = allowAlpha
    )
}

@Composable
fun <T : Enum<T>> FilterEnumParamItem(
    value: T,
    info: FilterParam,
    entries: List<T>,
    label: @Composable (T) -> String,
    previewOnly: Boolean,
    onValueChange: (T) -> Unit
) {
    val title = info.title?.let { stringResource(it) }.orEmpty()
    val labels = entries.map { label(it) }
    EnhancedButtonGroup(
        items = labels,
        selectedIndex = entries.indexOf(value),
        onIndexChange = { onValueChange(entries[it]) },
        title = title,
        enabled = !previewOnly
    )
}

@Composable
fun FilterPaletteParamItem(
    value: GradientPalette,
    previewOnly: Boolean,
    onValueChange: (GradientPalette) -> Unit
) {
    GradientPaletteSelector(
        value = value,
        onValueChange = { if (!previewOnly) onValueChange(it) },
        modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
    )
}
