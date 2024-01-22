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

package ru.tech.imageresizershrinker.core.ui.widget.buttons

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.widget.modifier.materialShadow
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalSettingsState

@Composable
fun ToggleGroupButton(
    @SuppressLint("ModifierParameter") modifier: Modifier = defaultModifier,
    enabled: Boolean,
    items: List<String>,
    selectedIndex: Int,
    title: String? = null,
    indexChanged: (Int) -> Unit,
    fadingEdgesColor: Color = MaterialTheme.colorScheme.surfaceContainer
) {
    ToggleGroupButton(
        enabled = enabled,
        items = items,
        selectedIndex = selectedIndex,
        indexChanged = indexChanged,
        modifier = modifier,
        title = {
            title?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(8.dp))
            }
        },
        fadingEdgesColor = fadingEdgesColor
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToggleGroupButton(
    @SuppressLint("ModifierParameter") modifier: Modifier = defaultModifier,
    enabled: Boolean,
    items: List<String>,
    selectedIndex: Int,
    title: @Composable () -> Unit = {},
    indexChanged: (Int) -> Unit,
    fadingEdgesColor: Color = MaterialTheme.colorScheme.surfaceContainer
) {
    val settingsState = LocalSettingsState.current
    val haptics = LocalHapticFeedback.current

    val disColor = MaterialTheme.colorScheme.onSurface
        .copy(alpha = 0.38f)
        .compositeOver(MaterialTheme.colorScheme.surface)

    ProvideTextStyle(
        value = TextStyle(
            color = if (!enabled) disColor
            else Color.Unspecified
        )
    ) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            title()
            Box {
                SingleChoiceSegmentedButtonRow(
                    space = max(settingsState.borderWidth, 1.dp),
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(start = 6.dp, end = 6.dp, bottom = 8.dp, top = 8.dp)
                ) {
                    CompositionLocalProvider(
                        LocalMinimumInteractiveComponentEnforcement provides false
                    ) {
                        items.forEachIndexed { index, item ->
                            val shape = SegmentedButtonDefaults.itemShape(index, items.size)
                            SegmentedButton(
                                enabled = enabled,
                                onClick = {
                                    haptics.performHapticFeedback(
                                        HapticFeedbackType.LongPress
                                    )
                                    indexChanged(index)
                                },
                                border = BorderStroke(
                                    width = settingsState.borderWidth,
                                    color = MaterialTheme.colorScheme.outlineVariant()
                                ),
                                selected = index == selectedIndex,
                                colors = SegmentedButtonDefaults.colors(
                                    activeBorderColor = MaterialTheme.colorScheme.outlineVariant(),
                                    inactiveContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                    activeContainerColor = if (enabled) {
                                        MaterialTheme.colorScheme.secondary
                                    } else {
                                        MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp)
                                    },
                                    activeContentColor = MaterialTheme.colorScheme.onSecondary
                                ),
                                modifier = Modifier.materialShadow(
                                    shape = shape,
                                    elevation = animateDpAsState(
                                        if (settingsState.borderWidth >= 0.dp || !settingsState.drawButtonShadows) 0.dp
                                        else if (selectedIndex == index) 2.dp
                                        else 1.dp
                                    ).value
                                ),
                                shape = shape
                            ) {
                                AutoSizeText(
                                    text = item,
                                    style = LocalTextStyle.current.copy(
                                        fontSize = 13.sp
                                    ),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .width(8.dp)
                        .height(50.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                0f to fadingEdgesColor,
                                1f to Color.Transparent
                            )
                        )
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .width(8.dp)
                        .height(50.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                0f to Color.Transparent,
                                1f to fadingEdgesColor
                            )
                        )
                )
            }
        }
    }
}

private var defaultModifier = Modifier
    .fillMaxWidth()
    .padding(8.dp)