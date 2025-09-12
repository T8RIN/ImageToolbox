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

package com.t8rin.imagetoolbox.feature.ascii_art.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.InvertColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem.AsciiParamsSelector
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.feature.ascii_art.presentation.screenLogic.AsciiArtComponent

@Composable
internal fun AsciiArtControls(
    component: AsciiArtComponent
) {
    Column(
        modifier = Modifier
            .container(
                shape = ShapeDefaults.large,
                resultPadding = 0.dp
            )
    ) {
        TitleItem(
            text = stringResource(R.string.params),
            icon = Icons.Rounded.Build,
        )
        Box(
            modifier = Modifier
                .padding(
                    horizontal = 12.dp
                )
        ) {
            AsciiParamsSelector(
                value = component.asciiParams,
                onValueChange = { params ->
                    component.setGradient(params.gradient)
                    component.setFontSize(params.fontSize)
                },
                itemShapes = {
                    ShapeDefaults.byIndex(
                        index = it,
                        size = 3
                    )
                }
            )
        }
        Spacer(Modifier.height(4.dp))
        PreferenceRowSwitch(
            title = stringResource(R.string.invert_colors),
            subtitle = stringResource(R.string.invert_colors_ascii_sub),
            startIcon = Icons.Rounded.InvertColors,
            checked = component.isInvertImage,
            onClick = {
                component.toggleIsInvertImage()
            },
            shape = ShapeDefaults.bottom,
            containerColor = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        )
        Spacer(Modifier.height(12.dp))
    }
}