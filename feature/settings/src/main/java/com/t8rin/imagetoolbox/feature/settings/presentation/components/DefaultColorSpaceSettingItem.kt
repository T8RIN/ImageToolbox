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

package com.t8rin.imagetoolbox.feature.settings.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ColorLens
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.image.model.ImageScaleMode
import com.t8rin.imagetoolbox.core.domain.image.model.ScaleColorSpace
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.DataSelector
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.title
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults

@Composable
fun DefaultColorSpaceSettingItem(
    onValueChange: (ImageScaleMode) -> Unit,
    shape: Shape = ShapeDefaults.center,
    modifier: Modifier = Modifier.padding(horizontal = 8.dp)
) {
    val settingsState = LocalSettingsState.current

    AnimatedVisibility(
        visible = settingsState.defaultImageScaleMode != ImageScaleMode.Base,
        modifier = Modifier.fillMaxWidth()
    ) {
        val items = remember {
            ScaleColorSpace.entries
        }
        DataSelector(
            value = settingsState.defaultImageScaleMode.scaleColorSpace,
            onValueChange = {
                onValueChange(
                    settingsState.defaultImageScaleMode.copy(it)
                )
            },
            initialExpanded = true,
            spanCount = 2,
            entries = items,
            title = stringResource(R.string.tag_color_space),
            titleIcon = Icons.Outlined.ColorLens,
            itemContentText = { it.title },
            containerColor = Color.Unspecified,
            shape = shape,
            modifier = modifier,
            selectedItemColor = MaterialTheme.colorScheme.secondary
        )
    }
}