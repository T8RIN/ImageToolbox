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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.BugReport
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.ImageToolboxThemeForPreview
import com.t8rin.imagetoolbox.core.ui.theme.blend
import com.t8rin.imagetoolbox.core.ui.utils.helper.EnPreview
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.feature.settings.presentation.components.additional.AnimatedGradientBox
import com.t8rin.imagetoolbox.feature.settings.presentation.components.additional.FullscreenDebugMenu

@Composable
fun DebugMenuSettingItem(
    shape: Shape = ShapeDefaults.default,
    modifier: Modifier = Modifier.padding(horizontal = 8.dp)
) {
    val showMenu = rememberSaveable {
        mutableStateOf(false)
    }
    val isNightMode = LocalSettingsState.current.isNightMode

    AnimatedGradientBox(
        colors = {
            if (isNightMode) {
                listOf(
                    errorContainer.blend(
                        color = error,
                        fraction = 0.6f
                    ),
                    primary.blend(
                        color = error,
                        fraction = 0.4f
                    ),
                    error,
                )
            } else {
                listOf(
                    error.blend(
                        color = errorContainer,
                        fraction = 0.6f
                    ),
                    primaryContainer.blend(
                        color = errorContainer,
                        fraction = 0.4f
                    ),
                    errorContainer,
                )
            }
        }
    ) {
        PreferenceItem(
            onClick = {
                showMenu.value = true
            },
            shape = shape,
            modifier = modifier,
            overrideIconShapeContentColor = true,
            containerColor = Color.Transparent,
            contentColor = if (isNightMode) {
                MaterialTheme.colorScheme.onError
            } else {
                MaterialTheme.colorScheme.onErrorContainer
            },
            title = stringResource(R.string.debug_menu),
            subtitle = stringResource(R.string.debug_menu_sub),
            startIcon = Icons.TwoTone.BugReport
        )
    }

    FullscreenDebugMenu(
        showMenuState = showMenu
    ) {
        EnhancedButton(
            onClick = {
                throw OutOfMemoryError("TEST")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Trigger OutOfMemory crash")
        }
        EnhancedButton(
            onClick = {
                throw Throwable("ForegroundServiceDidNotStartInTimeException")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Trigger ForegroundServiceDidNotStartInTimeException")
        }
        EnhancedButton(
            onClick = {
                throw Throwable("TEST")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Trigger regular crash")
        }
    }
}

@Composable
private fun PreviewContent(isDarkTheme: Boolean) = ImageToolboxThemeForPreview(isDarkTheme) {
    Box(Modifier.padding(16.dp)) {
        EnhancedButton(
            onClick = {},
            modifier = Modifier.alpha(0f)
        ) { }

        DebugMenuSettingItem()
    }
}

@Composable
@EnPreview
private fun PreviewNight() = PreviewContent(true)

@Composable
@EnPreview
private fun PreviewDay() = PreviewContent(false)