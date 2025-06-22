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

package com.t8rin.imagetoolbox.feature.recognize.text.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.utils.then
import com.t8rin.imagetoolbox.core.filters.domain.model.Filter
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText

@Composable
fun FilterSelectionBar(
    addedFilters: List<Filter<*>>,
    onContrastClick: () -> Unit,
    onThresholdClick: () -> Unit,
    onSharpnessClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedIndices = remember(addedFilters) {
        derivedStateOf {
            setOfNotNull(
                addedFilters.filterIsInstance<Filter.Contrast>().isNotEmpty().then(0),
                addedFilters.filterIsInstance<Filter.Sharpen>().isNotEmpty().then(1),
                addedFilters.filterIsInstance<Filter.Threshold>().isNotEmpty().then(2),
            )
        }
    }.value

    EnhancedButtonGroup(
        itemCount = 3,
        modifier = modifier.container(
            ShapeDefaults.extraLarge
        ),
        activeButtonColor = MaterialTheme.colorScheme.secondaryContainer,
        selectedIndices = selectedIndices,
        onIndexChange = {
            when (it) {
                0 -> onContrastClick()
                1 -> onSharpnessClick()
                2 -> onThresholdClick()
            }
        },
        title = {
            Text(
                text = stringResource(id = R.string.transformations),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        },
        itemContent = {
            val text = when (it) {
                0 -> stringResource(id = R.string.contrast)
                1 -> stringResource(id = R.string.sharpen)
                else -> stringResource(id = R.string.threshold)
            }

            AutoSizeText(
                text = text,
                maxLines = 1
            )
        },
        isScrollable = false
    )
}