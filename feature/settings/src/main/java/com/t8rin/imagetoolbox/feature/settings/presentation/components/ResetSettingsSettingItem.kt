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

package com.t8rin.imagetoolbox.feature.settings.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ResetDialog
import com.t8rin.imagetoolbox.core.ui.widget.icon_shape.LocalIconShapeContainerColor
import com.t8rin.imagetoolbox.core.ui.widget.icon_shape.LocalIconShapeContentColor
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem

@Composable
fun ResetSettingsSettingItem(
    onReset: () -> Unit,
    shape: Shape = ShapeDefaults.bottom,
    modifier: Modifier = Modifier.padding(horizontal = 8.dp)
) {
    var showResetDialog by remember { mutableStateOf(false) }

    CompositionLocalProvider(
        LocalIconShapeContentColor provides MaterialTheme.colorScheme.onErrorContainer,
        LocalIconShapeContainerColor provides MaterialTheme.colorScheme.errorContainer
    ) {
        PreferenceItem(
            onClick = {
                showResetDialog = true
            },
            shape = shape,
            modifier = modifier,
            containerColor = MaterialTheme.colorScheme
                .errorContainer
                .copy(alpha = 0.5f),
            title = stringResource(R.string.reset),
            subtitle = stringResource(R.string.reset_settings_sub),
            startIcon = Icons.Rounded.RestartAlt,
            overrideIconShapeContentColor = true
        )
    }

    ResetDialog(
        visible = showResetDialog,
        onDismiss = {
            showResetDialog = false
        },
        onReset = {
            showResetDialog = false
            onReset()
        },
        title = stringResource(R.string.reset),
        text = stringResource(R.string.reset_settings_sub),
        icon = Icons.Rounded.RestartAlt
    )
}