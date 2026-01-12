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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import kotlinx.collections.immutable.toPersistentMap
import kotlin.math.roundToInt

@Composable
internal fun PowerSliderItem(
    label: String,
    icon: ImageVector,
    value: Int,
    powers: List<Int>,
    maxAllowed: Int = Int.MAX_VALUE,
    onValueChange: (Int) -> Unit
) {
    val availablePowers = powers.filter { it < maxAllowed }
    val effectivePowers = availablePowers.ifEmpty { listOf(powers.first()) }
    val clampedValue = value.coerceAtMost(effectivePowers.last())
    var index by remember(clampedValue, effectivePowers) {
        mutableIntStateOf(effectivePowers.indexOf(clampedValue).coerceAtLeast(0))
    }
    LaunchedEffect(maxAllowed) {
        if (value >= maxAllowed && effectivePowers.isNotEmpty()) {
            onValueChange(effectivePowers.last())
        }
    }

    EnhancedSliderItem(
        value = index,
        internalStateTransformation = { it.roundToInt() },
        onValueChange = {
            val newIdx = it.roundToInt().coerceIn(effectivePowers.indices)
            if (newIdx != index) {
                index = newIdx
                onValueChange(effectivePowers[newIdx])
            }
        },
        valueRange = 0f..(effectivePowers.lastIndex.toFloat().coerceAtLeast(0f)),
        steps = (effectivePowers.size - 2).coerceAtLeast(0),
        enabled = effectivePowers.size > 1,
        title = label,
        valuesPreviewMapping = remember(effectivePowers) {
            buildMap {
                effectivePowers.forEachIndexed { index, value ->
                    put(index.toFloat(), "${value}px")
                }
            }.toPersistentMap()
        },
        icon = icon,
        isAnimated = false,
        canInputValue = false,
        shape = ShapeDefaults.large,
    )
}