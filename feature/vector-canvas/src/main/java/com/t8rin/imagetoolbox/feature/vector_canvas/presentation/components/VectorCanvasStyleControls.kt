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

@file:Suppress("FunctionNaming", "PackageName", "PackageNaming")

package com.t8rin.imagetoolbox.feature.vector_canvas.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Deselect
import com.t8rin.imagetoolbox.core.resources.icons.Eraser
import com.t8rin.imagetoolbox.core.resources.icons.KeyboardArrowDown
import com.t8rin.imagetoolbox.core.resources.icons.RoundedCorner
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.AlphaSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.feature.draw.domain.DrawLineStyle
import com.t8rin.imagetoolbox.feature.draw.presentation.components.DrawColorSelector
import com.t8rin.imagetoolbox.feature.draw.presentation.components.DrawLineStyleSelector
import com.t8rin.imagetoolbox.feature.draw.presentation.components.LineWidthSelector
import com.t8rin.imagetoolbox.feature.draw.presentation.components.element.OutlinedFillColorSelector
import com.t8rin.imagetoolbox.feature.vector_canvas.presentation.components.model.VectorCanvasSelection
import com.t8rin.imagetoolbox.feature.vector_canvas.presentation.components.model.canRound
import com.t8rin.imagetoolbox.feature.vector_canvas.presentation.components.model.supportsOpacity
import com.t8rin.imagetoolbox.feature.vector_canvas.presentation.components.model.toDrawLineStyle
import com.t8rin.imagetoolbox.feature.vector_canvas.presentation.components.model.toStrokeStyle
import io.ak1.drawbox.domain.model.Mode
import io.ak1.drawbox.domain.model.State
import io.ak1.drawbox.domain.model.StrokeStyle
import io.ak1.drawbox.presentation.viewmodel.DrawBoxController

@Composable
internal fun VectorCanvasStyleControls(
    state: State,
    selection: VectorCanvasSelection,
    controller: DrawBoxController
) {
    val isTextOnly = selection.text != null ||
            (!selection.hasSelection && state.mode == Mode.TEXT)
    val canShowStrokeControls = selection.hasStrokeSelection ||
            (!selection.hasSelection && state.mode.supportsOpacity())

    if (!isTextOnly && canShowStrokeControls) {
        StrokeControls(selection, controller)
    }
    DrawingOpacityControl(state, selection, controller, isTextOnly)
    EraserSizeControl(state, controller)
    ShapeControls(selection, controller)
    CornerRadiusControl(state, selection, controller)
    if (selection.hasSelection) {
        SelectionActions(controller)
    }
}

@Composable
private fun StrokeControls(
    selection: VectorCanvasSelection,
    controller: DrawBoxController
) {
    DrawColorSelector(
        value = selection.strokeColor,
        onValueChange = {
            if (selection.hasSelection) {
                controller.setSelectionColor(it)
            } else {
                controller.setColor(it)
            }
        },
        titleText = stringResource(R.string.stroke_color),
        modifier = Modifier.fillMaxWidth()
    )
    LineWidthSelector(
        value = selection.strokeWidth,
        onValueChange = {
            if (selection.hasStrokeSelection) {
                controller.setSelectionStrokeWidth(it)
            } else {
                controller.setStrokeWidth(it)
            }
        },
        modifier = Modifier.fillMaxWidth(),
        valueRange = 1f..64f
    )
    VectorCanvasLineStyleSelector(
        value = selection.strokeStyle,
        onValueChange = {
            if (selection.hasStrokeSelection) {
                controller.setSelectionStrokeStyle(it)
            } else {
                controller.setStrokeStyle(it)
            }
        }
    )
}

@Composable
private fun DrawingOpacityControl(
    state: State,
    selection: VectorCanvasSelection,
    controller: DrawBoxController,
    isTextOnly: Boolean
) {
    if (!selection.hasSelection && !isTextOnly && state.mode.supportsOpacity()) {
        AlphaSelector(
            value = state.opacity,
            onValueChange = controller::setOpacity,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun EraserSizeControl(
    state: State,
    controller: DrawBoxController
) {
    if (state.mode == Mode.ERASER) {
        EnhancedSliderItem(
            value = state.eraserSize,
            title = stringResource(R.string.vector_canvas_eraser_size),
            icon = Icons.Rounded.Eraser,
            valueRange = 4f..100f,
            onValueChange = controller::setEraserSize,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ShapeControls(
    selection: VectorCanvasSelection,
    controller: DrawBoxController
) {
    val shape = selection.shape ?: return
    OutlinedFillColorSelector(
        value = shape.fillColor,
        onValueChange = controller::setSelectionFillColor,
        modifier = Modifier.fillMaxWidth(),
        shape = ShapeDefaults.extraLarge
    )
    PreferenceRowSwitch(
        title = stringResource(R.string.vector_canvas_shape_stroke),
        checked = shape.strokeEnabled,
        onClick = controller::setSelectionStrokeEnabled,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun CornerRadiusControl(
    state: State,
    selection: VectorCanvasSelection,
    controller: DrawBoxController
) {
    if (!state.canRound(selection)) return

    EnhancedSliderItem(
        value = selection.cornerRadius,
        title = stringResource(R.string.vector_canvas_corner_radius),
        icon = Icons.Rounded.RoundedCorner,
        valueRange = 0f..64f,
        onValueChange = {
            if (selection.shape != null) {
                controller.setSelectionCornerRadius(it)
            } else {
                controller.setCornerRadius(it)
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun VectorCanvasLineStyleSelector(
    value: StrokeStyle,
    onValueChange: (StrokeStyle) -> Unit
) {
    val values = remember {
        listOf(
            DrawLineStyle.None,
            DrawLineStyle.Dashed(),
            DrawLineStyle.DotDashed
        )
    }
    DrawLineStyleSelector(
        value = value.toDrawLineStyle(),
        onValueChange = { onValueChange(it.toStrokeStyle()) },
        values = values,
        showStyleParams = false,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun SelectionActions(controller: DrawBoxController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .container(ShapeDefaults.circle)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        EnhancedIconButton(onClick = controller::sendSelectionToBack) {
            Icon(
                imageVector = Icons.Rounded.KeyboardArrowDown,
                contentDescription = stringResource(R.string.vector_canvas_send_to_back)
            )
        }
        EnhancedIconButton(onClick = controller::bringSelectionToFront) {
            Icon(
                imageVector = Icons.Rounded.KeyboardArrowDown,
                contentDescription = stringResource(R.string.vector_canvas_bring_to_front),
                modifier = Modifier.rotate(HALF_TURN_DEGREES)
            )
        }
        EnhancedIconButton(onClick = controller::clearSelection) {
            Icon(
                imageVector = Icons.Outlined.Deselect,
                contentDescription = stringResource(R.string.clear_selection)
            )
        }
    }
}

private const val HALF_TURN_DEGREES = 180f
