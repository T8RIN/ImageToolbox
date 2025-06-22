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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
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
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
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

    var primary by rememberSaveable(colorTuple) { mutableIntStateOf(colorTuple.primary.toArgb()) }
    var secondary by rememberSaveable(colorTuple) {
        mutableIntStateOf(
            colorTuple.secondary?.toArgb() ?: colorTuple.primary.calculateSecondaryColor()
        )
    }
    var tertiary by rememberSaveable(colorTuple) {
        mutableIntStateOf(
            colorTuple.tertiary?.toArgb() ?: colorTuple.primary.calculateTertiaryColor()
        )
    }

    var surface by rememberSaveable(colorTuple) {
        mutableIntStateOf(
            colorTuple.surface?.toArgb() ?: colorTuple.primary.calculateSurfaceColor()
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
        dynamicColor = settingsState.isDynamicColors,
        isInvertColors = settingsState.isInvertThemeColors
    )

    LaunchedEffect(visible) {
        if (!visible) {
            delay(1000)
            primary = colorTuple.primary.toArgb()
            secondary = colorTuple.secondary?.toArgb()
                ?: colorTuple.primary.calculateSecondaryColor()
            tertiary =
                colorTuple.tertiary?.toArgb()
                    ?: colorTuple.primary.calculateTertiaryColor()
            surface =
                colorTuple.surface?.toArgb()
                    ?: colorTuple.primary.calculateSurfaceColor()
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
                    contentPadding = PaddingValues(16.dp)
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
                                        primary = this.primary.toArgb()
                                        if (settingsState.themeStyle == PaletteStyle.TonalSpot) {
                                            secondary = this.secondary.toArgb()
                                            tertiary = this.tertiary.toArgb()
                                            surface = this.surface.toArgb()
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
                                color = primary,
                                onColorChange = {
                                    if (primary != it && settingsState.themeStyle == PaletteStyle.TonalSpot) {
                                        secondary = Color(it).calculateSecondaryColor()
                                        tertiary = Color(it).calculateTertiaryColor()
                                        surface = Color(it).calculateSurfaceColor()
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
                                    color = secondary,
                                    onColorChange = {
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
                                    color = tertiary,
                                    onColorChange = {
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
                                    color = surface,
                                    onColorChange = {
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
                            Color(primary),
                            Color(secondary),
                            Color(tertiary),
                            Color(surface)
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