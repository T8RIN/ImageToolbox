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

package ru.tech.imageresizershrinker.feature.main.presentation.components.settings

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ViewSidebar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.shapes.IconShapeContainer
import ru.tech.imageresizershrinker.core.ui.widget.controls.EnhancedSlider
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.feature.main.presentation.components.FabPreview
import kotlin.math.roundToInt

@Composable
fun FabAlignmentSettingItem(
    updateAlignment: (Float) -> Unit,
    modifier: Modifier = Modifier
        .padding(horizontal = 8.dp),
    shape: Shape = ContainerShapeDefaults.bottomShape
) {
    val settingsState = LocalSettingsState.current
    val color = MaterialTheme.colorScheme
        .secondaryContainer
        .copy(alpha = 0.2f)

    Box(
        modifier
            .container(
                shape = shape,
                color = color
            )
            .animateContentSize()
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(
                    start = 4.dp,
                    top = 4.dp,
                    bottom = 4.dp,
                    end = 8.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val derivedValue by remember(settingsState) {
                derivedStateOf {
                    when (settingsState.fabAlignment) {
                        Alignment.BottomStart -> 0
                        Alignment.BottomCenter -> 1
                        else -> 2
                    }
                }
            }
            var sliderValue by remember(derivedValue) {
                mutableFloatStateOf(derivedValue.toFloat())
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.padding(end = 12.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconShapeContainer(
                        enabled = true,
                        underlyingColor = color,
                        content = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ViewSidebar,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.padding(end = 16.dp, start = 8.dp)
                    )
                    Column {
                        Text(
                            text = stringResource(R.string.fab_alignment),
                            lineHeight = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                        AnimatedContent(
                            targetState = sliderValue,
                            transitionSpec = {
                                fadeIn() togetherWith fadeOut()
                            }
                        ) { value ->
                            Text(
                                text = stringResource(
                                    when (value.roundToInt()) {
                                        0 -> R.string.start_position
                                        1 -> R.string.center_position
                                        else -> R.string.end_position
                                    }
                                ),
                                color = MaterialTheme.colorScheme.onSurface.copy(
                                    alpha = 0.5f
                                ),
                                modifier = Modifier.padding(
                                    top = 2.dp,
                                    bottom = 8.dp
                                ),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                lineHeight = 14.sp,
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                EnhancedSlider(
                    modifier = Modifier
                        .padding(
                            start = 8.dp,
                            end = 16.dp,
                            top = 8.dp
                        ),
                    value = sliderValue,
                    onValueChange = {
                        sliderValue = it
                        updateAlignment(sliderValue)
                    },
                    colors = SliderDefaults.colors(
                        activeTickColor = MaterialTheme.colorScheme.inverseSurface,
                        inactiveTickColor = MaterialTheme.colorScheme.inverseSurface,
                        activeTrackColor = Color.Transparent,
                        inactiveTrackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                            0.15f
                        ),
                        thumbColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    valueRange = 0f..2f,
                    steps = 1
                )
            }
            FabPreview(
                alignment = settingsState.fabAlignment,
                modifier = Modifier
                    .width(75.dp)
                    .height(110.dp)
            )
        }
    }
}