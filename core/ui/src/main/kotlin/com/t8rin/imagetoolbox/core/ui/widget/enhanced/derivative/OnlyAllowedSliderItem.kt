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

package com.t8rin.imagetoolbox.core.ui.widget.enhanced.derivative

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import kotlinx.collections.immutable.toPersistentMap
import kotlin.math.roundToInt

@Composable
fun OnlyAllowedSliderItem(
    label: String,
    icon: ImageVector,
    value: Int,
    allowed: Collection<Int>,
    maxAllowed: Int = Int.MAX_VALUE,
    onValueChange: (Int) -> Unit,
    valueSuffix: String = " px",
    shape: Shape = ShapeDefaults.large,
) {
    val availableAllowed = allowed.filter { it < maxAllowed }
    val effectiveAllowed = availableAllowed.ifEmpty { listOf(allowed.first()) }
    val clampedValue = value.coerceAtMost(effectiveAllowed.last())
    var index by remember(clampedValue, effectiveAllowed) {
        mutableIntStateOf(effectiveAllowed.indexOf(clampedValue).coerceAtLeast(0))
    }
    LaunchedEffect(maxAllowed) {
        if (value >= maxAllowed && effectiveAllowed.isNotEmpty()) {
            onValueChange(effectiveAllowed.last())
        }
    }

    EnhancedSliderItem(
        value = index,
        internalStateTransformation = { it.roundToInt() },
        onValueChange = {
            val newIdx = it.roundToInt().coerceIn(effectiveAllowed.indices)
            if (newIdx != index) {
                index = newIdx
                onValueChange(effectiveAllowed[newIdx])
            }
        },
        valueRange = 0f..(effectiveAllowed.lastIndex.toFloat().coerceAtLeast(0f)),
        steps = (effectiveAllowed.size - 2).coerceAtLeast(0),
        enabled = effectiveAllowed.size > 1,
        title = label,
        valuesPreviewMapping = remember(effectiveAllowed) {
            buildMap {
                effectiveAllowed.forEachIndexed { index, value ->
                    put(index.toFloat(), "${value}${valueSuffix}")
                }
            }.toPersistentMap()
        },
        icon = icon,
        isAnimated = false,
        canInputValue = false,
        shape = shape,
    )
}