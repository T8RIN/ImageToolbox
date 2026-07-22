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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.colors.util.roundToTwoDigits
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.BrightnessAuto
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch

@Composable
fun RawAutoBrightnessSettingItem(
    onClick: () -> Unit,
    onValueChange: (Float) -> Unit,
    shape: Shape = ShapeDefaults.center,
    modifier: Modifier = Modifier.padding(horizontal = 8.dp),
) {
    val settings = LocalSettingsState.current.rawDevelopSettings
    val enabled = !settings.useEmbeddedPreview
    var value by remember(settings.brightness) { mutableFloatStateOf(settings.brightness) }

    Column(
        modifier = modifier.container(
            shape = shape,
            resultPadding = 0.dp
        )
    ) {
        PreferenceRowSwitch(
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            title = stringResource(R.string.raw_auto_brightness),
            subtitle = stringResource(R.string.raw_auto_brightness_sub),
            checked = settings.autoBrightness,
            onClick = { onClick() },
            startIcon = Icons.Outlined.BrightnessAuto,
            drawContainer = false
        )

        AnimatedVisibility(visible = !settings.autoBrightness && enabled) {
            EnhancedSliderItem(
                modifier = Modifier.padding(
                    start = 8.dp,
                    end = 8.dp,
                    bottom = 8.dp
                ),
                shape = ShapeDefaults.default,
                value = value,
                title = stringResource(R.string.raw_brightness),
                onValueChange = { value = it.roundToTwoDigits() },
                onValueChangeFinished = { onValueChange(value) },
                internalStateTransformation = Float::roundToTwoDigits,
                valueRange = 0.1f..4f,
                steps = 38,
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            )
        }
    }
}
