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

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.rounded.Draw
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.domain.model.Harmonizer
import ru.tech.imageresizershrinker.core.settings.presentation.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.shapes.IconShapeContainer
import ru.tech.imageresizershrinker.core.ui.theme.blend
import ru.tech.imageresizershrinker.core.ui.theme.inverse
import ru.tech.imageresizershrinker.core.ui.utils.confetti.LocalConfettiHostState
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedChip
import ru.tech.imageresizershrinker.core.ui.widget.color_picker.ColorSelection
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem

@Composable
fun ConfettiHarmonizerSettingItem(
    onValueChange: (Harmonizer) -> Unit,
    shape: Shape = ContainerShapeDefaults.centerShape,
    modifier: Modifier = Modifier.padding(horizontal = 8.dp)
) {
    val backgroundColor = Color.Unspecified
    val settingsState = LocalSettingsState.current
    val items = remember {
        Harmonizer.entries
    }

    val enabled = settingsState.isConfettiEnabled

    val confettiHostState = LocalConfettiHostState.current
    val scope = rememberCoroutineScope()

    var showColorPicker by remember {
        mutableStateOf(false)
    }

    Box {
        Column(
            modifier = modifier
                .container(
                    shape = shape,
                    color = backgroundColor
                )
                .alpha(
                    animateFloatAsState(
                        if (enabled) 1f
                        else 0.5f
                    ).value
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                IconShapeContainer(
                    enabled = true,
                    underlyingColor = MaterialTheme.colorScheme.secondaryContainer.copy(0.2f),
                    content = {
                        Icon(
                            imageVector = Icons.Outlined.ColorLens,
                            contentDescription = null
                        )
                    }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(R.string.harmonization_color),
                    style = LocalTextStyle.current.copy(lineHeight = 18.sp),
                    fontWeight = FontWeight.Medium
                )
            }

            FlowRow(
                verticalArrangement = Arrangement.spacedBy(
                    8.dp,
                    Alignment.CenterVertically
                ),
                horizontalArrangement = Arrangement.spacedBy(
                    8.dp,
                    Alignment.CenterHorizontally
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, bottom = 8.dp, end = 8.dp)
            ) {
                val value = settingsState.confettiHarmonizer
                val level = settingsState.confettiHarmonizationLevel
                items.forEach { harmonizer ->
                    val colorScheme = MaterialTheme.colorScheme
                    val selectedColorDerived by remember(harmonizer, value, level) {
                        derivedStateOf {
                            when (harmonizer) {
                                is Harmonizer.Custom -> Color(value.ordinal)
                                    .blend(
                                        color = colorScheme.surface,
                                        fraction = 0.1f
                                    )

                                Harmonizer.Primary -> colorScheme.primary
                                Harmonizer.Secondary -> colorScheme.secondary
                                Harmonizer.Tertiary -> colorScheme.tertiary
                            }
                        }
                    }
                    EnhancedChip(
                        onClick = {
                            if (harmonizer !is Harmonizer.Custom) {
                                confettiHostState.currentToastData?.dismiss()
                                onValueChange(harmonizer)
                                scope.launch {
                                    delay(200L)
                                    confettiHostState.showConfetti()
                                }
                            } else {
                                showColorPicker = true
                            }
                        },
                        selected = harmonizer::class.isInstance(value),
                        label = {
                            Text(text = harmonizer.title)
                        },
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                        selectedColor = selectedColorDerived,
                        selectedContentColor = when (harmonizer) {
                            is Harmonizer.Custom -> selectedColorDerived.inverse(
                                fraction = {
                                    if (it) 0.9f
                                    else 0.6f
                                },
                                darkMode = selectedColorDerived.luminance() < 0.3f
                            )

                            else -> contentColorFor(backgroundColor = selectedColorDerived)
                        },
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (!enabled) {
            Surface(
                color = Color.Transparent,
                modifier = Modifier.matchParentSize()
            ) {}
        }
    }

    var tempColor by remember(settingsState.confettiHarmonizer) {
        mutableIntStateOf((settingsState.confettiHarmonizer as? Harmonizer.Custom)?.color ?: 0)
    }
    SimpleSheet(
        sheetContent = {
            Box {
                Column(
                    Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(start = 36.dp, top = 36.dp, end = 36.dp, bottom = 24.dp)
                ) {
                    ColorSelection(
                        color = tempColor,
                        onColorChange = {
                            tempColor = it
                        }
                    )
                }
            }
        },
        visible = showColorPicker,
        onDismiss = {
            showColorPicker = it
        },
        title = {
            TitleItem(
                text = stringResource(R.string.color),
                icon = Icons.Rounded.Draw
            )
        },
        confirmButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = {
                    confettiHostState.currentToastData?.dismiss()
                    onValueChange(Harmonizer.Custom(tempColor))
                    scope.launch {
                        delay(200L)
                        confettiHostState.showConfetti()
                    }
                    showColorPicker = false
                }
            ) {
                AutoSizeText(stringResource(R.string.ok))
            }
        }
    )
}

private val Harmonizer.title: String
    @Composable
    get() = when (this) {
        is Harmonizer.Custom -> stringResource(R.string.custom)
        Harmonizer.Primary -> stringResource(R.string.primary)
        Harmonizer.Secondary -> stringResource(R.string.secondary)
        Harmonizer.Tertiary -> stringResource(R.string.tertiary)
    }