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

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.zIndex
import com.t8rin.imagetoolbox.feature.vector_canvas.presentation.components.model.VectorCanvasUiState
import io.ak1.drawbox.DrawBox
import io.ak1.drawbox.domain.model.Element
import io.ak1.drawbox.domain.model.State
import io.ak1.drawbox.presentation.viewmodel.DrawBoxController
import io.ak1.drawbox.text.InlineTextEditor

@Composable
internal fun VectorCanvasEditor(
    state: State,
    controller: DrawBoxController,
    uiState: VectorCanvasUiState,
    modifier: Modifier = Modifier
) {
    DetectNewTextElement(
        state = state,
        uiState = uiState
    )

    LaunchedEffect(state.mode) {
        if (uiState.editingTextId != null) uiState.commitTextEdit(controller)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { uiState.canvasSize = it }
    ) {
        DrawBox(
            state = state,
            onIntent = controller::onIntent,
            showGrid = uiState.showGrid,
            hiddenElementIds = uiState.editingTextId?.let(::setOf) ?: emptySet(),
            modifier = Modifier.fillMaxSize()
        )
        VectorCanvasInlineTextEditor(
            state = state,
            controller = controller,
            uiState = uiState
        )
    }
}

@Composable
private fun DetectNewTextElement(
    state: State,
    uiState: VectorCanvasUiState
) {
    LaunchedEffect(state.elements) {
        val text = state.elements.lastOrNull() as? Element.Text
        if (text?.text?.isEmpty() == true) uiState.beginNewTextEdit(text)
    }
}

@Composable
private fun VectorCanvasInlineTextEditor(
    state: State,
    controller: DrawBoxController,
    uiState: VectorCanvasUiState
) {
    val editingId = uiState.editingTextId ?: return
    val target = state.elements.firstOrNull { it.id == editingId } as? Element.Text

    if (target == null) {
        LaunchedEffect(editingId) { uiState.cancelTextEdit() }
        return
    }

    Box(
        modifier = Modifier
            .zIndex(TEXT_EDITOR_OVERLAY_Z_INDEX)
            .fillMaxSize()
            .pointerInput(editingId) {
                detectTapGestures {
                    uiState.commitTextEdit(controller)
                }
            }
    )
    InlineTextEditor(
        element = target,
        viewport = state.viewport,
        draft = uiState.textDraft,
        onDraftChange = { uiState.textDraft = it }
    )
}

private const val TEXT_EDITOR_OVERLAY_Z_INDEX = 9f
