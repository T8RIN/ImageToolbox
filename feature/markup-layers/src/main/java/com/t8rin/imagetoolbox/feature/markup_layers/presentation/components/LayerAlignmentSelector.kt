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

package com.t8rin.imagetoolbox.feature.markup_layers.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.Position
import com.t8rin.imagetoolbox.core.ui.theme.ImageToolboxThemeForPreview
import com.t8rin.imagetoolbox.core.ui.widget.modifier.AutoCornersShape
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import kotlin.math.abs

@Composable
internal fun LayerAlignmentSelector(
    normalizedPositionX: Float?,
    normalizedPositionY: Float?,
    enabled: Boolean,
    onAlignLayer: (Float, Float) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Position.entriesSorted.chunked(ALIGNMENT_GRID_COLUMN_COUNT)
            .forEachIndexed { rowIndex, rowPositions ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    rowPositions.forEachIndexed { columnIndex, position ->
                        val x = normalizedAlignmentValues[columnIndex]
                        val y = normalizedAlignmentValues[rowIndex]
                        AlignmentTile(
                            position = position,
                            selected = normalizedPositionX?.isNear(x) == true &&
                                    normalizedPositionY?.isNear(y) == true,
                            enabled = enabled,
                            onClick = { onAlignLayer(x, y) }
                        )
                    }
                }
            }
    }
}

@Composable
private fun AlignmentTile(
    position: Position,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    ClickableTile(
        onClick = onClick,
        enabled = enabled,
        containerColor = if (selected) {
            MaterialTheme.colorScheme.tertiaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainerLow
        },
        modifier = Modifier
            .size(
                width = 66.dp,
                height = 48.dp
            )
    ) {
        val contentColor = LocalContentColor.current

        Box(
            contentAlignment = position.contentAlignment,
            modifier = Modifier
                .size(24.dp)
                .border(
                    width = 1.5.dp,
                    color = contentColor.copy(alpha = 0.6f),
                    shape = ShapeDefaults.extraSmall
                )
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(
                        color = contentColor,
                        shape = AutoCornersShape(1.dp)
                    )
            )
        }
    }
}

private val normalizedAlignmentValues = listOf(0f, NORMALIZED_CENTER, 1f)

private val Position.contentAlignment: Alignment
    get() = when (this) {
        Position.TopLeft -> Alignment.TopStart
        Position.TopCenter -> Alignment.TopCenter
        Position.TopRight -> Alignment.TopEnd
        Position.CenterLeft -> Alignment.CenterStart
        Position.Center -> Alignment.Center
        Position.CenterRight -> Alignment.CenterEnd
        Position.BottomLeft -> Alignment.BottomStart
        Position.BottomCenter -> Alignment.BottomCenter
        Position.BottomRight -> Alignment.BottomEnd
    }

private fun Float.isNear(value: Float): Boolean = abs(this - value) < ALIGNMENT_TOLERANCE

private const val ALIGNMENT_GRID_COLUMN_COUNT = 3
private const val NORMALIZED_CENTER = 0.5f
private const val ALIGNMENT_TOLERANCE = 0.001

@Preview
@Composable
private fun Preview() = ImageToolboxThemeForPreview(true) {
    LayerAlignmentSelector(
        normalizedPositionX = null,
        normalizedPositionY = null,
        enabled = true,
        onAlignLayer = { _, _ -> }
    )
}