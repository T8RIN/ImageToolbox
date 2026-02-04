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

package com.t8rin.imagetoolbox.core.ui.widget.color_picker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.rounded.ContentPaste
import androidx.compose.material.icons.rounded.FormatColorFill
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.dynamic.theme.ColorTuple
import com.t8rin.dynamic.theme.PaletteStyle
import com.t8rin.dynamic.theme.calculateSecondaryColor
import com.t8rin.dynamic.theme.calculateSurfaceColor
import com.t8rin.dynamic.theme.calculateTertiaryColor
import com.t8rin.dynamic.theme.rememberAppColorTuple
import com.t8rin.dynamic.theme.rememberColorScheme
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.toColor
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.saver.ColorSaver
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import kotlinx.coroutines.delay

@Composable
fun ColorTuplePicker(
    visible: Boolean,
    onDismiss: () -> Unit,
    colorTuple: ColorTuple,
    title: String = stringResource(R.string.color_scheme),
    onColorChange: (ColorTuple) -> Unit,
) {
    val settingsState = LocalSettingsState.current

    var primary by rememberSaveable(colorTuple, stateSaver = ColorSaver) {
        mutableStateOf(colorTuple.primary)
    }
    var secondary by rememberSaveable(colorTuple, stateSaver = ColorSaver) {
        mutableStateOf(
            colorTuple.secondary ?: colorTuple.primary.calculateSecondaryColor().toColor()
        )
    }
    var tertiary by rememberSaveable(colorTuple, stateSaver = ColorSaver) {
        mutableStateOf(
            colorTuple.tertiary ?: colorTuple.primary.calculateTertiaryColor().toColor()
        )
    }

    var surface by rememberSaveable(colorTuple, stateSaver = ColorSaver) {
        mutableStateOf(
            colorTuple.surface ?: colorTuple.primary.calculateSurfaceColor().toColor()
        )
    }

    val appColorTuple = rememberAppColorTuple(
        defaultColorTuple = settingsState.appColorTuple,
        dynamicColor = true,
        darkTheme = true
    )

    val scheme = rememberColorScheme(
        amoledMode = false,
        isDarkTheme = true,
        colorTuple = appColorTuple,
        contrastLevel = settingsState.themeContrastLevel,
        style = settingsState.themeStyle,
        dynamicColor = false,
        isInvertColors = settingsState.isInvertThemeColors
    )

    LaunchedEffect(visible) {
        if (!visible) {
            delay(1000)
            primary = colorTuple.primary
            secondary = colorTuple.secondary
                ?: colorTuple.primary.calculateSecondaryColor().toColor()
            tertiary =
                colorTuple.tertiary
                    ?: colorTuple.primary.calculateTertiaryColor().toColor()
            surface =
                colorTuple.surface
                    ?: colorTuple.primary.calculateSurfaceColor().toColor()
        }
    }

    EnhancedModalBottomSheet(
        visible = visible,
        onDismiss = {
            if (!it) onDismiss()
        },
        title = {
            TitleItem(
                text = title,
                icon = Icons.Outlined.Palette
            )
        },
        sheetContent = {
            Box {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(260.dp),
                    horizontalArrangement = Arrangement.spacedBy(
                        16.dp,
                        Alignment.CenterHorizontally
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                    contentPadding = PaddingValues(16.dp),
                    flingBehavior = enhancedFlingBehavior()
                ) {
                    item(
                        span = {
                            GridItemSpan(maxCurrentLineSpan)
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .container(ShapeDefaults.extraLarge)
                                .padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.FormatColorFill,
                                contentDescription = null
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.monet_colors))
                            Spacer(Modifier.width(8.dp))
                            Spacer(Modifier.weight(1f))
                            EnhancedButton(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                onClick = {
                                    scheme.apply {
                                        primary = this.primary
                                        if (settingsState.themeStyle == PaletteStyle.TonalSpot) {
                                            secondary = this.secondary
                                            tertiary = this.tertiary
                                            surface = this.surface
                                        }
                                    }
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.ContentPaste,
                                    contentDescription = stringResource(R.string.pastel)
                                )
                            }
                        }
                    }
                    item(
                        span = {
                            if (settingsState.themeStyle != PaletteStyle.TonalSpot) {
                                GridItemSpan(
                                    maxCurrentLineSpan
                                )
                            } else GridItemSpan(1)
                        }
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .container(ShapeDefaults.extraLarge)
                                .padding(horizontal = 20.dp)
                        ) {
                            TitleItem(text = stringResource(R.string.primary))
                            ColorSelection(
                                value = primary,
                                onValueChange = {
                                    if (primary != it && settingsState.themeStyle == PaletteStyle.TonalSpot) {
                                        secondary = it.calculateSecondaryColor().toColor()
                                        tertiary = it.calculateTertiaryColor().toColor()
                                        surface = it.calculateSurfaceColor().toColor()
                                    }
                                    primary = it
                                },
                                infoContainerColor = MaterialTheme.colorScheme.surface
                            )
                        }
                    }
                    if (settingsState.themeStyle == PaletteStyle.TonalSpot) {
                        item {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .container(ShapeDefaults.extraLarge)
                                    .padding(horizontal = 20.dp)
                            ) {
                                TitleItem(text = stringResource(R.string.secondary))
                                ColorSelection(
                                    value = secondary,
                                    onValueChange = {
                                        secondary = it
                                    },
                                    infoContainerColor = MaterialTheme.colorScheme.surface
                                )
                            }
                        }
                        item {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .container(ShapeDefaults.extraLarge)
                                    .padding(horizontal = 20.dp)
                            ) {
                                TitleItem(text = stringResource(R.string.tertiary))
                                ColorSelection(
                                    value = tertiary,
                                    onValueChange = {
                                        tertiary = it
                                    },
                                    infoContainerColor = MaterialTheme.colorScheme.surface
                                )
                            }
                        }
                        item {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .container(ShapeDefaults.extraLarge)
                                    .padding(horizontal = 20.dp)
                            ) {
                                TitleItem(text = stringResource(R.string.surface))
                                ColorSelection(
                                    value = surface,
                                    onValueChange = {
                                        surface = it
                                    },
                                    infoContainerColor = MaterialTheme.colorScheme.surface
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            EnhancedButton(
                onClick = {
                    onColorChange(
                        ColorTuple(
                            primary = primary,
                            secondary = secondary,
                            tertiary = tertiary,
                            surface = surface
                        )
                    )
                    onDismiss()
                }
            ) {
                AutoSizeText(stringResource(R.string.save))
            }
        },
    )
}