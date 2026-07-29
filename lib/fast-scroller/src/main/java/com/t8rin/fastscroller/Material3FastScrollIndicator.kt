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

package com.t8rin.fastscroller

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sqrt

/** Bubble indicator styled after the fast-scroller indicator in Launcher3. */
@Composable
fun Material3FastScrollIndicator(
    label: String,
    position: Float,
    visible: Boolean,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
) {
    var height by remember { mutableIntStateOf(0) }
    val alpha by animateFloatAsState(if (visible) 1f else 0f, label = "fastScrollerIndicatorAlpha")

    Box(
        modifier = modifier
            .onSizeChanged { height = it.height }
            .offset { IntOffset(0, (position - height / 2f).roundToInt()) }
            .clip(Material3IndicatorShape)
            .background(color.copy(alpha = alpha))
            .wrapContentSize(unbounded = true)
            .defaultMinSize(minWidth = 78.dp, minHeight = 64.dp)
            .padding(end = 14.dp, top = 8.dp, bottom = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = contentColor.copy(alpha = alpha),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
    }
}

private object Material3IndicatorShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline = Outline.Generic(
        Path().apply {
            val height = size.height
            val radius = height / 2
            val diagonal = sqrt(2.0).toFloat()
            val width = max(radius + diagonal * radius, size.width)
            arcTo(radius, radius, radius, 90f, 180f)
            val outerX = width - diagonal * radius
            arcTo(outerX, radius, radius, -90f, 45f)
            val innerRadius = radius / 5
            val innerX = width - diagonal * innerRadius
            arcTo(innerX, radius, innerRadius, -45f, 90f)
            arcTo(outerX, radius, radius, 45f, 45f)
            close()
        }
    )
}

private fun Path.arcTo(
    centerX: Float,
    centerY: Float,
    radius: Float,
    startAngle: Float,
    sweepAngle: Float,
) {
    arcTo(
        rect = Rect(
            left = centerX - radius,
            top = centerY - radius,
            right = centerX + radius,
            bottom = centerY + radius,
        ),
        startAngleDegrees = startAngle,
        sweepAngleDegrees = sweepAngle,
        forceMoveTo = false,
    )
}