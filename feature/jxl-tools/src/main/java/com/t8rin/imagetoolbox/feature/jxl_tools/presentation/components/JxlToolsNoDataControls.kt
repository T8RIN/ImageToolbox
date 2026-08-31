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

package com.t8rin.imagetoolbox.feature.jxl_tools.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem


@Composable
internal fun JxlToolsNoDataControls(
    onPickImage: (Screen.JxlTools.Type) -> Unit
) {
    val isPortrait by isPortraitOrientationAsState()
    val settingsState = LocalSettingsState.current
    val types = remember {
        Screen.JxlTools.Type.entries
    }
    val preference: @Composable (Screen.JxlTools.Type, Modifier) -> Unit = { type, modifier ->
        PreferenceItem(
            title = stringResource(type.title),
            subtitle = stringResource(type.subtitle),
            startIcon = type.icon,
            modifier = modifier.fillMaxWidth(),
            enabled = type.isAvailableWith(settingsState.filenameBehavior),
            onDisabledClick = ::showOverwriteFormatWarning,
            onClick = {
                onPickImage(type)
            }
        )
    }
    if (isPortrait) {
        Column {
            types.forEachIndexed { index, type ->
                preference(type, Modifier)
                if (index != types.lastIndex) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    } else {
        Column(
            modifier = Modifier.windowInsetsPadding(
                WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal)
            )
        ) {
            types.chunked(2).forEachIndexed { rowIndex, rowTypes ->
                Row {
                    rowTypes.forEachIndexed { index, type ->
                        preference(type, Modifier.weight(1f))
                        if (index == 0) Spacer(modifier = Modifier.width(8.dp))
                    }
                    if (rowTypes.size == 1) Spacer(modifier = Modifier.weight(1f))
                }
                if (rowIndex != types.chunked(2).lastIndex) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
