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

package com.t8rin.imagetoolbox.feature.draw.presentation.components.element

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.icons.RotateRight
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import kotlin.math.atan2
import kotlin.math.roundToInt

@Composable
internal fun LineAngleIndicator(
    drawDownPosition: Offset,
    currentDrawPosition: Offset,
    imageWidth: Int,
    imageHeight: Int,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val offset = with(density) {
        val indicatorWidth = 96.dp.roundToPx()
        val indicatorHeight = 40.dp.roundToPx()

        val verticalGap = 108.dp.roundToPx()
        val horizontalGap = -indicatorWidth / 2

        IntOffset(
            x = (currentDrawPosition.x.roundToInt() + horizontalGap)
                .coerceIn(0, (imageWidth - indicatorWidth).coerceAtLeast(0)),
            y = (currentDrawPosition.y.roundToInt() - verticalGap)
                .coerceIn(0, (imageHeight - indicatorHeight).coerceAtLeast(0))
        )
    }

    Surface(
        modifier = modifier
            .offset { offset }
            .defaultMinSize(minHeight = 40.dp),
        shape = ShapeDefaults.extraLarge,
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        tonalElevation = 6.dp,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.RotateRight,
                contentDescription = null,
                modifier = Modifier.padding(end = 6.dp)
            )
            Text(
                text = "${drawDownPosition.angleDegreesTo(currentDrawPosition)}°",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

private fun Offset.angleDegreesTo(other: Offset): Int {
    val degrees = Math.toDegrees(
        atan2(
            y = (other.y - y).toDouble(),
            x = (other.x - x).toDouble()
        )
    ).roundToInt()

    return (degrees % FULL_CIRCLE_DEGREES + FULL_CIRCLE_DEGREES) % FULL_CIRCLE_DEGREES
}

private const val FULL_CIRCLE_DEGREES = 360