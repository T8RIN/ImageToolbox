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

package ru.tech.imageresizershrinker.image_splitting.presentation.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.TableRows
import androidx.compose.material.icons.rounded.ViewColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.ImageFormatSelector
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.QualitySelector
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedSliderItem
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.image_splitting.domain.SplitParams
import kotlin.math.roundToInt

@Composable
internal fun SplitParamsSelector(
    value: SplitParams,
    onValueChange: (SplitParams) -> Unit
) {
    var rowsCount by remember {
        mutableIntStateOf(value.rowsCount)
    }

    EnhancedSliderItem(
        value = rowsCount,
        title = stringResource(R.string.rows_count),
        sliderModifier = Modifier
            .padding(
                top = 14.dp,
                start = 12.dp,
                end = 12.dp,
                bottom = 10.dp
            ),
        icon = Icons.Rounded.TableRows,
        valueRange = 1f..20f,
        steps = 18,
        internalStateTransformation = {
            it.roundToInt()
        },
        onValueChangeFinished = {
            onValueChange(
                value.copy(rowsCount = it.roundToInt())
            )
            rowsCount = it.roundToInt()
        },
        onValueChange = {},
        shape = ContainerShapeDefaults.topShape
    )
    Spacer(Modifier.height(4.dp))

    var columnsCount by remember {
        mutableIntStateOf(value.columnsCount)
    }

    EnhancedSliderItem(
        value = columnsCount,
        title = stringResource(R.string.columns_count),
        sliderModifier = Modifier
            .padding(
                top = 14.dp,
                start = 12.dp,
                end = 12.dp,
                bottom = 10.dp
            ),
        steps = 18,
        icon = Icons.Rounded.ViewColumn,
        valueRange = 1f..20f,
        internalStateTransformation = {
            it.roundToInt()
        },
        onValueChangeFinished = {
            onValueChange(
                value.copy(columnsCount = it.roundToInt())
            )
            columnsCount = it.roundToInt()
        },
        onValueChange = {},
        shape = ContainerShapeDefaults.bottomShape
    )
    if (value.imageFormat.canChangeCompressionValue) {
        Spacer(Modifier.height(8.dp))
    }
    QualitySelector(
        imageFormat = value.imageFormat,
        quality = value.quality,
        onQualityChange = {
            onValueChange(
                value.copy(quality = it)
            )
        }
    )
    Spacer(Modifier.height(8.dp))
    ImageFormatSelector(
        value = value.imageFormat,
        onValueChange = {
            onValueChange(
                value.copy(imageFormat = it)
            )
        }
    )
}