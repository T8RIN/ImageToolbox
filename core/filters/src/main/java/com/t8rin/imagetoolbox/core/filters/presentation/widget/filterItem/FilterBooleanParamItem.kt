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

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.filters.domain.model.FilterParam
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch

@Composable
fun FilterBooleanParamItem(
    value: Boolean,
    info: FilterParam,
    previewOnly: Boolean,
    onValueChange: (Boolean) -> Unit
) {
    if (info.options.isNotEmpty()) {
        FilterParamItem(if (value) 1f else 0f, info, previewOnly) {
            onValueChange(it >= 0.5f)
        }
    } else {
        info.title?.let { title ->
            PreferenceRowSwitch(
                title = stringResource(title),
                checked = value,
                onClick = onValueChange,
                modifier = Modifier.padding(top = 8.dp, start = 4.dp, end = 4.dp),
                applyHorizontalPadding = false,
                startContent = {},
                resultModifier = Modifier.padding(16.dp),
                enabled = !previewOnly
            )
        }
    }
}
