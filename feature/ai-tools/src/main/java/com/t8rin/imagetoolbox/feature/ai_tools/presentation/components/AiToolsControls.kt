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

package com.t8rin.imagetoolbox.feature.ai_tools.presentation.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.GridOn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Exercise
import com.t8rin.imagetoolbox.core.resources.icons.Stack
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.other.InfoContainer
import com.t8rin.imagetoolbox.feature.ai_tools.presentation.screenLogic.AiToolsComponent
import kotlin.math.roundToInt

@Composable
internal fun AiToolsControls(component: AiToolsComponent) {
    EnhancedSliderItem(
        value = component.params.strength,
        internalStateTransformation = { it.roundToInt() },
        steps = 100,
        valueRange = 0f..100f,
        onValueChange = {
            component.updateParams { copy(strength = it) }
        },
        title = stringResource(R.string.strength),
        icon = Icons.Outlined.Exercise
    )
    Spacer(Modifier.height(8.dp))
    val chunkPowers = generateSequence(16) { it * 2 }.takeWhile { it <= 2048 }.toList()
    val overlapPowers = generateSequence(8) { it * 2 }.takeWhile { it <= 256 }.toList()

    PowerSliderItem(
        label = stringResource(id = R.string.chunk_size),
        icon = Icons.Rounded.GridOn,
        value = component.params.chunkSize,
        powers = chunkPowers,
        onValueChange = { component.updateParams { copy(chunkSize = it) } }
    )
    Spacer(Modifier.height(8.dp))
    PowerSliderItem(
        label = stringResource(R.string.overlap_size),
        icon = Icons.Outlined.Stack,
        value = component.params.overlap,
        powers = overlapPowers,
        maxAllowed = component.params.chunkSize,
        onValueChange = { component.updateParams { copy(overlap = it) } }
    )
    Spacer(Modifier.height(8.dp))
    InfoContainer(
        text = stringResource(R.string.note_chunk_info, component.params.chunkSize),
        containerColor = MaterialTheme.colorScheme.secondaryContainer
    )
}