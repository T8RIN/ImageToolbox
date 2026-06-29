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

package com.t8rin.imagetoolbox.feature.vector_canvas.presentation.components

import androidx.compose.runtime.Composable
import com.t8rin.imagetoolbox.feature.vector_canvas.presentation.components.model.VectorCanvasInitialColors
import com.t8rin.imagetoolbox.feature.vector_canvas.presentation.components.model.VectorCanvasScreenContext
import com.t8rin.imagetoolbox.feature.vector_canvas.presentation.components.model.withZIndex
import io.ak1.drawbox.domain.model.Element
import io.ak1.drawbox.domain.model.Intent
import io.ak1.drawbox.presentation.viewmodel.DrawBoxController

@Composable
internal fun VectorCanvasOverlays(
    context: VectorCanvasScreenContext,
    initialColors: VectorCanvasInitialColors
) {
    val component = context.component
    val state = context.state
    val controller = context.controller
    val uiState = context.uiState
    VectorCanvasLayersSheet(
        visible = uiState.showLayers,
        elements = state.elements,
        selectedIds = state.selectedIds,
        actions = VectorCanvasLayerActions(
            onDismiss = { uiState.showLayers = false },
            onDelete = { controller.onIntent(Intent.DeleteElement(it.id)) },
            onReorder = { controller.reorderElements(it) }
        )
    )
    VectorCanvasDialogs(
        component = component,
        uiState = uiState,
        onClear = {
            controller.reset()
            controller.setBgColor(initialColors.background)
            controller.setColor(initialColors.stroke)
            uiState.clearRuntimePattern()
        },
        onSaveTo = { location ->
            uiState.pendingPngLocation = location
            controller.saveBitmap()
        }
    )
}

private fun DrawBoxController.reorderElements(elements: List<Element>) {
    onIntent(Intent.BeginTransform)
    elements.forEachIndexed { index, element ->
        onIntent(Intent.UpdateElement(element.withZIndex(index)))
    }
}