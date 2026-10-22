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

package com.t8rin.imagetoolbox.core.ui.widget.other

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.blend

@Composable
fun HelpTipsGradient(
    scaleToSize: Boolean = false,
    content: @Composable (Color) -> Unit
) {
    val isNightMode = LocalSettingsState.current.isNightMode

    AnimatedGradientBox(
        scaleToSize = scaleToSize,
        colors = {
            if (isNightMode) {
                if (scaleToSize) {
                    listOf(
                        secondaryContainer.blend(
                            color = secondary,
                            fraction = 0.8f
                        ),
                        primary.blend(
                            color = tertiary,
                            fraction = 0.33f
                        ),
                        primaryContainer.blend(
                            color = secondary,
                            fraction = 0.65f
                        ),

                        )
                } else {
                    listOf(
                        primaryContainer.blend(
                            color = primary,
                            fraction = 0.8f
                        ),
                        secondaryContainer.blend(
                            color = primary,
                            fraction = 0.65f
                        ),
                        tertiary,
                    )
                }
            } else {
                if (scaleToSize) {
                    listOf(
                        secondary.blend(
                            color = secondaryContainer,
                            fraction = 0.8f
                        ),
                        primaryContainer.blend(
                            color = tertiaryContainer,
                            fraction = 0.33f
                        ),
                        primary.blend(
                            color = secondaryContainer,
                            fraction = 0.65f
                        )
                    )
                } else {
                    listOf(
                        primary.blend(
                            color = primaryContainer,
                            fraction = 0.8f
                        ),
                        secondary.blend(
                            color = primaryContainer,
                            fraction = 0.65f
                        ),
                        tertiaryContainer,
                    )
                }
            }
        }
    ) {
        content(
            if (isNightMode) MaterialTheme.colorScheme.onTertiary
            else MaterialTheme.colorScheme.onTertiaryContainer
        )
    }
}