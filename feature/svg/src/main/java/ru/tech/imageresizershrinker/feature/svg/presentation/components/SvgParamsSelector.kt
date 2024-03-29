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

package ru.tech.imageresizershrinker.feature.svg.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChangeHistory
import androidx.compose.material.icons.outlined.FormatColorFill
import androidx.compose.material.icons.outlined.RepeatOne
import androidx.compose.material.icons.rounded.ColorLens
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.widget.controls.EnhancedSliderItem
import ru.tech.imageresizershrinker.core.ui.widget.controls.resize_group.components.BlurRadiusSelector
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceRowSwitch
import ru.tech.imageresizershrinker.feature.svg.domain.SvgParams
import kotlin.math.roundToInt

@Composable
fun SvgParamsSelector(
    value: SvgParams,
    onValueChange: (SvgParams) -> Unit
) {
    Column {
        PreferenceRowSwitch(
            title = stringResource(id = R.string.use_sampled_palette),
            subtitle = stringResource(id = R.string.use_sampled_palette_sub),
            checked = value.isPaletteSampled,
            onClick = {
                onValueChange(
                    value.copy(isPaletteSampled = it)
                )
            },
            startIcon = Icons.Outlined.FormatColorFill,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        EnhancedSliderItem(
            value = value.colorsCount,
            title = stringResource(R.string.max_colors_count),
            icon = Icons.Rounded.ColorLens,
            valueRange = 2f..64f,
            internalStateTransformation = {
                it.roundToInt()
            },
            onValueChange = {
                onValueChange(
                    value.copy(
                        colorsCount = it.roundToInt()
                    )
                )
            },
            shape = RoundedCornerShape(24.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        EnhancedSliderItem(
            value = value.quantizationCyclesCount,
            icon = Icons.Outlined.RepeatOne,
            title = stringResource(id = R.string.repeat_count),
            valueRange = 1f..10f,
            steps = 9,
            internalStateTransformation = { it.roundToInt() },
            onValueChange = {
                onValueChange(
                    value.copy(
                        quantizationCyclesCount = it.roundToInt()
                    )
                )
            },
            shape = RoundedCornerShape(24.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        BlurRadiusSelector(
            modifier = Modifier.fillMaxWidth(),
            value = value.blurRadius,
            onValueChange = {
                onValueChange(
                    value.copy(
                        blurRadius = it
                    )
                )
            },
            color = Color.Unspecified,
            valueRange = 0f..100f,
            shape = RoundedCornerShape(24.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        EnhancedSliderItem(
            value = value.blurDelta,
            icon = Icons.Outlined.ChangeHistory,
            title = stringResource(id = R.string.blur_size),
            valueRange = 0f..1024f,
            internalStateTransformation = { it.roundToInt() },
            onValueChange = {
                onValueChange(
                    value.copy(
                        blurDelta = it.roundToInt()
                    )
                )
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}