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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Lightbulb
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.ImageToolboxThemeForPreview
import com.t8rin.imagetoolbox.core.ui.theme.blend
import com.t8rin.imagetoolbox.core.ui.utils.helper.EnPreview
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemOverload
import com.t8rin.imagetoolbox.feature.settings.presentation.components.additional.AnimatedGradientBox

@Composable
fun HelpTipsSettingItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier.padding(horizontal = 8.dp),
    shape: Shape = ShapeDefaults.default,
) {
    val isNightMode = LocalSettingsState.current.isNightMode

    AnimatedGradientBox(
        colors = {
            if (isNightMode) {
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
    ) {
        PreferenceItemOverload(
            shape = shape,
            onClick = onClick,
            startIcon = {
                Icon(
                    imageVector = Icons.TwoTone.Lightbulb,
                    contentDescription = null
                )
            },
            overrideIconShapeContentColor = true,
            containerColor = Color.Transparent,
            contentColor = if (isNightMode) {
                MaterialTheme.colorScheme.onTertiary
            } else {
                MaterialTheme.colorScheme.onTertiaryContainer
            },
            title = stringResource(R.string.help_tips),
            subtitle = stringResource(R.string.help_tips_settings_sub),
            modifier = modifier
        )
    }
}

@Composable
private fun PreviewContent(isDarkTheme: Boolean) = ImageToolboxThemeForPreview(
    isDarkTheme = isDarkTheme,
    keyColor = Color.Green
) {
    Box(Modifier.padding(16.dp)) {
        EnhancedButton(
            onClick = {},
            modifier = Modifier.alpha(0f)
        ) { }

        HelpTipsSettingItem({})
    }
}

@Composable
@EnPreview
private fun Preview() = Column {
    PreviewContent(true)
    PreviewContent(false)
}