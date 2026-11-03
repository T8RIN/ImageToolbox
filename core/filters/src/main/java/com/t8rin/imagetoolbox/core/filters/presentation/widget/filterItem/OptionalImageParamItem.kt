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

package com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.ImageModel
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.ImageSelector
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch

@Composable
fun OptionalImageParamItem(
    value: ImageModel?,
    @StringRes title: Int,
    @StringRes toggleTitle: Int,
    previewOnly: Boolean,
    onValueChange: (ImageModel?) -> Unit
) {
    var enabled by remember(value) { mutableStateOf(value != null) }
    PreferenceRowSwitch(
        title = stringResource(toggleTitle),
        checked = enabled,
        onClick = {
            if (!previewOnly) {
                enabled = it
                if (!it) onValueChange(null)
            }
        },
        modifier = Modifier.padding(8.dp),
        applyHorizontalPadding = false,
        startContent = {},
        resultModifier = Modifier.padding(16.dp),
        enabled = !previewOnly
    )
    if (enabled) {
        ImageSelector(
            enabled = !previewOnly,
            value = value?.data,
            title = stringResource(title),
            subtitle = null,
            onValueChange = { if (!previewOnly) onValueChange(ImageModel(it.toString())) },
            modifier = Modifier.padding(8.dp)
        )
    }
}