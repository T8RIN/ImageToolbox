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
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.Position
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Place
import com.t8rin.imagetoolbox.core.ui.theme.ImageToolboxThemeForPreview
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.widget.buttons.SupportingButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedVerticalScroll
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.fadingEdges
import kotlin.math.abs

@Composable
internal fun LayerAlignmentSupportingButton(
    normalizedPositionX: Float?,
    normalizedPositionY: Float?,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val selected = normalizedPositionX.isAlignmentValue() &&
            normalizedPositionY.isAlignmentValue()

    SupportingButton(
        icon = Icons.Outlined.Place,
        onClick = {
            if (enabled) onClick()
        },
        containerColor = takeColorFromScheme {
            if (selected) secondary else secondaryContainer
        },
        contentColor = takeColorFromScheme {
            if (selected) onSecondary else onSecondaryContainer
        },
        shape = ShapeDefaults.circle,
        modifier = Modifier
            .padding(4.dp)
            .alpha(if (enabled) 1f else 0.5f)
    )
}

@Composable
internal fun LayerAlignmentDialog(
    visible: Boolean,
    normalizedPositionX: Float?,
    normalizedPositionY: Float?,
    onAlignLayer: (Float, Float) -> Unit,
    onDismiss: () -> Unit
) {
    EnhancedAlertDialog(
        visible = visible,
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Outlined.Place,
                contentDescription = null
            )
        },
        title = {
            Text(stringResource(R.string.position))
        },
        text = {
            val state = rememberScrollState()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fadingEdges(
                        scrollableState = state,
                        isVertical = true
                    )
                    .enhancedVerticalScroll(state),
                horizontalArrangement = Arrangement.Center
            ) {
                CompositionLocalProvider(
                    LocalLayoutDirection provides LayoutDirection.Ltr
                ) {
                    LayerAlignmentSelector(
                        normalizedPositionX = normalizedPositionX,
                        normalizedPositionY = normalizedPositionY,
                        onAlignLayer = onAlignLayer
                    )
                }
            }
        },
        confirmButton = {
            EnhancedButton(
                onClick = onDismiss,
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(stringResource(R.string.close))
            }
        }
    )
}

@Composable
internal fun LayerAlignmentSelector(
    normalizedPositionX: Float?,
    normalizedPositionY: Float?,
    onAlignLayer: (Float, Float) -> Unit
) {
    val positions = Position.entriesSorted
    val entries = remember {
        positions.chunked(ALIGNMENT_GRID_COLUMN_COUNT)
    }

    Column(
        modifier = Modifier
            .widthIn(max = 400.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        entries.forEachIndexed { rowIndex, rowPositions ->
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
                        onClick = { onAlignLayer(x, y) },
                        shape = ShapeDefaults.byGridIndex(
                            index = rowIndex * ALIGNMENT_GRID_COLUMN_COUNT + columnIndex,
                            size = positions.size,
                            crossAxisCount = ALIGNMENT_GRID_COLUMN_COUNT
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun RowScope.AlignmentTile(
    position: Position,
    selected: Boolean,
    onClick: () -> Unit,
    shape: Shape
) {
    val contentColor = takeColorFromScheme {
        if (selected) {
            onSecondary
        } else {
            onSurface
        }
    }

    ClickableTile(
        onClick = onClick,
        containerColor = takeColorFromScheme {
            if (selected) {
                secondary
            } else {
                surfaceContainerLow
            }
        },
        shape = shape,
        modifier = Modifier.weight(1f)
    ) {
        Box(
            contentAlignment = position.contentAlignment,
            modifier = Modifier
                .padding(12.dp)
                .size(32.dp)
                .border(
                    width = 2.dp,
                    color = contentColor.copy(alpha = 0.6f),
                    shape = ShapeDefaults.pressed
                )
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        color = contentColor,
                        shape = ShapeDefaults.extremeSmall
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

private fun Float?.isAlignmentValue(): Boolean = this != null &&
        normalizedAlignmentValues.any(::isNear)

private const val ALIGNMENT_GRID_COLUMN_COUNT = 3
private const val NORMALIZED_CENTER = 0.5f
private const val ALIGNMENT_TOLERANCE = 0.0001

@Preview
@Composable
private fun Preview() = ImageToolboxThemeForPreview(true) {
    Button({}) { }
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(24.dp)
    ) {
        LayerAlignmentSelector(
            normalizedPositionX = 0.5f,
            normalizedPositionY = 0.5f,
            onAlignLayer = { _, _ -> }
        )
    }
}