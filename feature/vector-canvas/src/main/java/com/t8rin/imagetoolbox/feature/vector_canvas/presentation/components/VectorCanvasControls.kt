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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.feature.vector_canvas.presentation.components.model.VectorCanvasSelection
import com.t8rin.imagetoolbox.feature.vector_canvas.presentation.components.model.VectorCanvasUiState
import com.t8rin.imagetoolbox.feature.vector_canvas.presentation.components.model.vectorCanvasSelection
import io.ak1.drawbox.domain.model.Element
import io.ak1.drawbox.domain.model.Mode
import io.ak1.drawbox.domain.model.State
import io.ak1.drawbox.presentation.viewmodel.DrawBoxController

@Composable
internal fun VectorCanvasControls(
    state: State,
    controller: DrawBoxController,
    uiState: VectorCanvasUiState,
    actions: VectorCanvasControlActions
) {
    val isPortrait = isPortraitOrientationAsState().value
    val selection = state.vectorCanvasSelection()
    val itemModifier = Modifier.fillMaxWidth()

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LandscapeQuickActions(isPortrait, state.mode, controller)
        VectorCanvasModeSelector(
            value = state.mode,
            onValueChange = controller::setMode,
            modifier = itemModifier
        )
        VectorCanvasDrawingControls(
            state = state,
            selection = selection,
            controller = controller,
            onEditText = actions.onEditText
        )
        VectorCanvasViewportControls(
            viewport = state.viewport,
            focalPoint = uiState.canvasSize.centerOffset(),
            controller = controller,
            modifier = itemModifier
        )
        VectorCanvasBackgroundControls(
            backgroundColor = state.bgColor,
            pattern = uiState.pattern,
            showGrid = uiState.showGrid,
            actions = VectorCanvasBackgroundActions(
                onBackgroundColorChange = controller::setBgColor,
                onPatternChange = { uiState.pattern = it },
                onShowGridChange = { uiState.showGrid = it },
                onInsertImage = actions.onInsertImage
            ),
            modifier = itemModifier
        )
        VectorCanvasProjectControls(
            actions = VectorCanvasProjectActions(
                onExportSvg = actions.onExportSvg,
                onExportJson = actions.onExportJson,
                onImportJson = actions.onImportJson
            ),
            canReplay = state.elements.isNotEmpty(),
            modifier = itemModifier
        )
    }
}

@Composable
private fun LandscapeQuickActions(
    isPortrait: Boolean,
    mode: Mode,
    controller: DrawBoxController
) {
    if (isPortrait) return

    Row(
        modifier = Modifier.container(ShapeDefaults.circle),
        verticalAlignment = Alignment.CenterVertically
    ) {
        VectorCanvasQuickActions(
            mode = mode,
            controller = controller
        )
    }
}

@Composable
private fun VectorCanvasDrawingControls(
    state: State,
    selection: VectorCanvasSelection,
    controller: DrawBoxController,
    onEditText: (Element.Text) -> Unit
) {
    if (state.mode == Mode.TEXT || selection.text != null) {
        VectorCanvasTextControls(
            selection = selection,
            controller = controller,
            onEditText = {
                selection.text?.let(onEditText)
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
    VectorCanvasStyleControls(
        state = state,
        selection = selection,
        controller = controller
    )
}

internal data class VectorCanvasControlActions(
    val onInsertImage: () -> Unit,
    val onExportSvg: () -> Unit,
    val onExportJson: () -> Unit,
    val onImportJson: () -> Unit,
    val onEditText: (Element.Text) -> Unit
)

private fun androidx.compose.ui.unit.IntSize.centerOffset(): Offset = Offset(
    x = width / 2f,
    y = height / 2f
)
