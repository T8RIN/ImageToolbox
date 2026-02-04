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

package com.t8rin.imagetoolbox.feature.settings.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material.icons.rounded.ColorLens
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.domain.model.ColorHarmonizer
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.blend
import com.t8rin.imagetoolbox.core.ui.theme.inverse
import com.t8rin.imagetoolbox.core.ui.theme.toColor
import com.t8rin.imagetoolbox.core.ui.utils.confetti.LocalConfettiHostState
import com.t8rin.imagetoolbox.core.ui.widget.color_picker.ColorSelection
import com.t8rin.imagetoolbox.core.ui.widget.color_picker.RecentAndFavoriteColorsCard
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedChip
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedVerticalScroll
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ConfettiHarmonizationColorSettingItem(
    onValueChange: (ColorHarmonizer) -> Unit,
    shape: Shape = ShapeDefaults.center,
    modifier: Modifier = Modifier.padding(horizontal = 8.dp)
) {
    val settingsState = LocalSettingsState.current
    val items = remember {
        ColorHarmonizer.entries
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
                    shape = shape
                )
                .alpha(
                    animateFloatAsState(
                        if (enabled) 1f
                        else 0.5f
                    ).value
                )
        ) {
            TitleItem(
                modifier = Modifier.padding(
                    top = 12.dp,
                    end = 12.dp,
                    bottom = 16.dp,
                    start = 12.dp
                ),
                iconEndPadding = 14.dp,
                text = stringResource(R.string.harmonization_color),
                icon = Icons.Outlined.ColorLens
            )

            FlowRow(
                verticalArrangement = Arrangement.spacedBy(
                    space = 8.dp,
                    alignment = Alignment.CenterVertically
                ),
                horizontalArrangement = Arrangement.spacedBy(
                    space = 8.dp,
                    alignment = Alignment.CenterHorizontally
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, bottom = 8.dp, end = 8.dp)
            ) {
                val value = settingsState.confettiColorHarmonizer
                items.forEach { harmonizer ->
                    val colorScheme = MaterialTheme.colorScheme
                    val selectedColor = when (harmonizer) {
                        is ColorHarmonizer.Custom -> Color(value.ordinal)
                            .blend(
                                color = colorScheme.surface,
                                fraction = 0.1f
                            )

                        ColorHarmonizer.Primary -> colorScheme.primary
                        ColorHarmonizer.Secondary -> colorScheme.secondary
                        ColorHarmonizer.Tertiary -> colorScheme.tertiary
                    }
                    EnhancedChip(
                        onClick = {
                            if (harmonizer !is ColorHarmonizer.Custom) {
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
                        selectedColor = selectedColor,
                        selectedContentColor = when (harmonizer) {
                            is ColorHarmonizer.Custom -> selectedColor.inverse(
                                fraction = {
                                    if (it) 0.9f
                                    else 0.6f
                                },
                                darkMode = selectedColor.luminance() < 0.3f
                            )

                            else -> contentColorFor(backgroundColor = selectedColor)
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

    var tempColor by remember(settingsState.confettiColorHarmonizer) {
        mutableStateOf(
            (settingsState.confettiColorHarmonizer as? ColorHarmonizer.Custom)?.color?.toColor()
                ?: Color.Black
        )
    }
    EnhancedModalBottomSheet(
        sheetContent = {
            Column(
                modifier = Modifier
                    .enhancedVerticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                RecentAndFavoriteColorsCard(
                    onRecentColorClick = { tempColor = it },
                    onFavoriteColorClick = { tempColor = it }
                )

                ColorSelection(
                    value = tempColor,
                    onValueChange = {
                        tempColor = it
                    }
                )
            }
        },
        visible = showColorPicker,
        onDismiss = {
            showColorPicker = it
        },
        title = {
            TitleItem(
                text = stringResource(R.string.color),
                icon = Icons.Rounded.ColorLens
            )
        },
        confirmButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = {
                    confettiHostState.currentToastData?.dismiss()
                    onValueChange(ColorHarmonizer.Custom(tempColor.toArgb()))
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

private val ColorHarmonizer.title: String
    @Composable
    get() = when (this) {
        is ColorHarmonizer.Custom -> stringResource(R.string.custom)
        ColorHarmonizer.Primary -> stringResource(R.string.primary)
        ColorHarmonizer.Secondary -> stringResource(R.string.secondary)
        ColorHarmonizer.Tertiary -> stringResource(R.string.tertiary)
    }