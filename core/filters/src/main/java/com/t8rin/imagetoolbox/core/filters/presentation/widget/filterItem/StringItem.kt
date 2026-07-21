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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.icons.Done
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField

@Composable
internal fun StringItem(
    value: String,
    filter: UiFilter<String>,
    onFilterChange: (String) -> Unit,
    previewOnly: Boolean
) {
    val error = filter.error

    var textValue by remember(value) {
        mutableStateOf(value)
    }

    RoundedTextField(
        value = textValue,
        onValueChange = { textValue = it },
        label = filter.paramsInfo.firstOrNull()?.title?.let { stringResource(it) }.orEmpty(),
        modifier = Modifier.padding(12.dp),
        readOnly = previewOnly,
        singleLine = false,
        endIcon = {
            EnhancedIconButton(
                onClick = {
                    onFilterChange(textValue)
                }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Done,
                    contentDescription = "Done"
                )
            }
        },
        onLoseFocusTransformation = {
            onFilterChange(textValue)
            this
        },
        isError = error.isNotEmpty(),
        supportingText = { Text(error) },
    )
}
