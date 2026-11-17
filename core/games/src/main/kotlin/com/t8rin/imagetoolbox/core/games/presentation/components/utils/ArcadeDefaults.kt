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

package com.t8rin.imagetoolbox.core.games.presentation.components.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.games.presentation.components.model.ArcadeStyle

object ArcadeDefaults {
    internal const val LevelTransitionDuration = 0.24f

    val style: ArcadeStyle
        @Composable
        get() {
            val colors = MaterialTheme.colorScheme
            val shape = MaterialTheme.shapes.extraLarge
            val typography = MaterialTheme.typography
            return remember(colors, shape, typography) {
                ArcadeStyle(
                    shape = shape,
                    background = Brush.verticalGradient(
                        listOf(colors.surfaceContainerLow, colors.surfaceContainer)
                    ),
                    borderColor = colors.outlineVariant,
                    playerColor = colors.primary,
                    playerDetailColor = colors.onPrimary,
                    obstacleColor = colors.secondary,
                    accentColor = colors.tertiary,
                    detailColor = colors.outline.copy(alpha = 0.42f),
                    textColor = colors.onSurface,
                    highScoreColor = colors.onSurfaceVariant,
                    scoreTextStyle = typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    overlayColor = colors.scrim.copy(alpha = 0.5f),
                    buttonColor = colors.tertiaryContainer,
                    buttonContentColor = colors.onTertiaryContainer,
                    gameHeight = 280.dp,
                    hazardColor = colors.error
                )
            }
        }
}