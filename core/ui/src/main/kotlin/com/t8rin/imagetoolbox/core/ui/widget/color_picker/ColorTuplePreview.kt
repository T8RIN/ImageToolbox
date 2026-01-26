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

@file:Suppress("COMPOSE_APPLIER_CALL_MISMATCH")

package com.t8rin.imagetoolbox.core.ui.widget.color_picker

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.dynamic.theme.ColorTuple
import com.t8rin.dynamic.theme.ColorTupleItem
import com.t8rin.dynamic.theme.PaletteStyle
import com.t8rin.dynamic.theme.rememberColorScheme
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.shapes.MaterialStarShape
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.inverse
import com.t8rin.imagetoolbox.core.ui.theme.outlineVariant
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container


@Composable
fun ColorTuplePreview(
    modifier: Modifier = Modifier,
    isDefaultItem: Boolean = false,
    colorTuple: ColorTuple,
    appColorTuple: ColorTuple,
    onClick: () -> Unit
) {
    val settingsState = LocalSettingsState.current
    ColorTupleItem(
        colorTuple = remember(settingsState.themeStyle, colorTuple) {
            derivedStateOf {
                if (settingsState.themeStyle == PaletteStyle.TonalSpot) {
                    colorTuple
                } else colorTuple.run {
                    copy(secondary = primary, tertiary = primary)
                }
            }
        }.value,
        modifier = modifier
            .aspectRatio(1f)
            .container(
                shape = MaterialStarShape,
                color = rememberColorScheme(
                    isDarkTheme = settingsState.isNightMode,
                    amoledMode = settingsState.isAmoledMode,
                    colorTuple = colorTuple,
                    contrastLevel = settingsState.themeContrastLevel,
                    style = settingsState.themeStyle,
                    dynamicColor = false,
                    isInvertColors = settingsState.isInvertThemeColors
                ).surfaceVariant.copy(alpha = 0.8f),
                borderColor = MaterialTheme.colorScheme.outlineVariant(0.2f),
                resultPadding = 0.dp
            )
            .hapticsClickable(onClick = onClick)
            .padding(3.dp)
            .clip(ShapeDefaults.circle),
        backgroundColor = Color.Transparent
    ) {
        AnimatedContent(
            targetState = (colorTuple == appColorTuple)
                .and(
                    if (colorTuple in ColorTupleDefaults.defaultColorTuples) {
                        isDefaultItem
                    } else true
                )
        ) { selected ->
            BoxWithConstraints(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                if (selected) {
                    Box(
                        modifier = Modifier
                            .size(this.maxWidth * (5 / 9f))
                            .background(
                                color = animateColorAsState(
                                    colorTuple.primary.inverse(
                                        fraction = { cond ->
                                            if (cond) 0.8f
                                            else 0.5f
                                        },
                                        darkMode = colorTuple.primary.luminance() < 0.3f
                                    )
                                ).value,
                                shape = ShapeDefaults.circle
                            )
                    )
                    Icon(
                        imageVector = Icons.Rounded.Done,
                        contentDescription = stringResource(R.string.ok),
                        tint = colorTuple.primary,
                        modifier = Modifier.size(maxWidth * (1 / 3f))
                    )
                }
            }
        }
    }
}