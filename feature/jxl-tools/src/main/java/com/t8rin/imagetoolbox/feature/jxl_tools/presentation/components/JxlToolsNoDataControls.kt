/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.widget.modifier.withModifier
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem


@Composable
internal fun JxlToolsNoDataControls(
    onPickImage: (Screen.JxlTools.Type) -> Unit
) {
    val isPortrait by isPortraitOrientationAsState()
    val types = remember {
        Screen.JxlTools.Type.entries
    }
    val preference1 = @Composable {
        PreferenceItem(
            title = stringResource(types[0].title),
            subtitle = stringResource(types[0].subtitle),
            startIcon = types[0].icon,
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onPickImage(types[0])
            }
        )
    }
    val preference2 = @Composable {
        PreferenceItem(
            title = stringResource(types[1].title),
            subtitle = stringResource(types[1].subtitle),
            startIcon = types[1].icon,
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onPickImage(types[1])
            }
        )
    }
    val preference3 = @Composable {
        PreferenceItem(
            title = stringResource(types[2].title),
            subtitle = stringResource(types[2].subtitle),
            startIcon = types[2].icon,
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onPickImage(types[2])
            }
        )
    }
    val preference4 = @Composable {
        PreferenceItem(
            title = stringResource(types[3].title),
            subtitle = stringResource(types[3].subtitle),
            startIcon = types[3].icon,
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onPickImage(types[3])
            }
        )
    }
    if (isPortrait) {
        Column {
            preference1()
            Spacer(modifier = Modifier.height(8.dp))
            preference2()
            Spacer(modifier = Modifier.height(8.dp))
            preference3()
            Spacer(modifier = Modifier.height(8.dp))
            preference4()
        }
    } else {
        val direction = LocalLayoutDirection.current
        val cutout = WindowInsets.displayCutout.asPaddingValues().let {
            PaddingValues(
                start = it.calculateStartPadding(direction),
                end = it.calculateEndPadding(direction)
            )
        }

        Column {
            Row(
                modifier = Modifier.padding(cutout)
            ) {
                preference1.withModifier(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                preference2.withModifier(modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.padding(cutout)
            ) {
                preference3.withModifier(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                preference4.withModifier(modifier = Modifier.weight(1f))
            }
        }
    }
}