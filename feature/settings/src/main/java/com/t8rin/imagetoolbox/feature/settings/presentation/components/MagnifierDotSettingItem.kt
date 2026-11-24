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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.RadioButtonChecked
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.utils.helper.toModel
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ColorRowSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import kotlin.math.roundToInt

@Composable
internal fun MagnifierDotSettingItem(
    onClick: () -> Unit,
    onSizeChange: (Int) -> Unit,
    onColorChange: (ColorModel) -> Unit
) {
    val settingsState = LocalSettingsState.current
    PreferenceRowSwitch(
        modifier = Modifier.padding(horizontal = 8.dp),
        shape = ShapeDefaults.center,
        title = stringResource(R.string.magnifier_dot),
        subtitle = stringResource(R.string.magnifier_dot_sub),
        checked = settingsState.magnifierDotEnabled,
        onClick = { onClick() },
        startIcon = Icons.Rounded.RadioButtonChecked,
        additionalContent = {
            AnimatedVisibility(
                visible = settingsState.magnifierDotEnabled,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    EnhancedSliderItem(
                        shape = ShapeDefaults.top,
                        title = stringResource(R.string.magnifier_dot_size),
                        value = settingsState.magnifierDotSize.value,
                        valueRange = 1f..16f,
                        internalStateTransformation = { it.roundToInt() },
                        onValueChange = {},
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        onValueChangeFinished = { onSizeChange(it.roundToInt()) }
                    )
                    ColorRowSelector(
                        modifier = Modifier.container(
                            shape = ShapeDefaults.bottom,
                            color = MaterialTheme.colorScheme.surfaceContainerLow
                        ),
                        value = settingsState.magnifierDotColor,
                        onValueChange = { onColorChange(it.toModel()) },
                        title = stringResource(R.string.color)
                    )
                }
            }
        }
    )
}