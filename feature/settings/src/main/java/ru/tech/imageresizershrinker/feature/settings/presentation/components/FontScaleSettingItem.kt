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

package ru.tech.imageresizershrinker.feature.settings.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.colordetector.util.ColorUtil.roundToTwoDigits
import ru.tech.imageresizershrinker.core.domain.utils.trimTrailingZero
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.shapes.IconShapeContainer
import ru.tech.imageresizershrinker.core.ui.widget.controls.EnhancedSlider
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container

@Composable
fun FontScaleSettingItem(
    onValueChange: (Float) -> Unit,
    shape: Shape = ContainerShapeDefaults.bottomShape,
    modifier: Modifier = Modifier.padding(horizontal = 8.dp)
) {
    val settingsState = LocalSettingsState.current

    Column(
        modifier
            .container(
                shape = shape
            )
            .animateContentSize()
    ) {
        var sliderValue by remember {
            mutableFloatStateOf(settingsState.fontScale ?: 0f)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconShapeContainer(
                enabled = true,
                content = {
                    Icon(
                        imageVector = Icons.Rounded.TextFields,
                        contentDescription = null
                    )
                },
                modifier = Modifier.padding(
                    top = 16.dp,
                    start = 12.dp
                )
            )
            Text(
                text = stringResource(R.string.font_scale),
                modifier = Modifier
                    .padding(
                        top = 16.dp,
                        end = 16.dp,
                        start = 16.dp
                    )
                    .weight(1f),
                fontWeight = FontWeight.Medium
            )
            AnimatedContent(
                targetState = sliderValue,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                }
            ) { value ->
                Text(
                    text = value.takeIf { it > 0 }?.toString()?.trimTrailingZero()
                        ?: stringResource(R.string.defaultt),
                    color = MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.5f
                    ),
                    modifier = Modifier.padding(top = 16.dp, end = 16.dp),
                    lineHeight = 18.sp
                )
            }
        }
        EnhancedSlider(
            modifier = Modifier
                .padding(
                    top = 14.dp,
                    start = 12.dp,
                    end = 12.dp,
                    bottom = 10.dp
                ),
            value = sliderValue,
            onValueChange = {
                sliderValue = if (it == 0.45f) 0f
                else it.roundToTwoDigits()
            },
            onValueChangeFinished = {
                onValueChange(sliderValue)
            },
            valueRange = 0.45f..1.5f,
            steps = 20
        )
    }
}