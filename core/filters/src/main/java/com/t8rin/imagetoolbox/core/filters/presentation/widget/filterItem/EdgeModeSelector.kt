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

package com.t8rin.imagetoolbox.core.filters.presentation.widget.filterItem

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.filters.domain.model.enums.BlurEdgeMode
import com.t8rin.imagetoolbox.core.filters.presentation.utils.translatedName
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup

@Composable
internal fun EdgeModeSelector(
    title: Int?,
    value: BlurEdgeMode,
    onValueChange: (BlurEdgeMode) -> Unit,
) {
    Text(
        text = stringResource(title!!),
        modifier = Modifier.padding(
            top = 8.dp,
            start = 12.dp,
            end = 12.dp,
        )
    )
    val entries = BlurEdgeMode.entries

    EnhancedButtonGroup(
        inactiveButtonColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        items = entries.map { it.translatedName },
        selectedIndex = entries.indexOf(value),
        onIndexChange = {
            onValueChange(entries[it])
        }
    )
}