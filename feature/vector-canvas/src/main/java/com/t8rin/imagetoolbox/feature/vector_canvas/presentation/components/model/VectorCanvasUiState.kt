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

package com.t8rin.imagetoolbox.feature.vector_canvas.presentation.components.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.IntSize
import io.ak1.drawbox.domain.model.Element
import io.ak1.drawbox.domain.model.Intent
import io.ak1.drawbox.presentation.viewmodel.DrawBoxController

@Stable
class VectorCanvasUiState internal constructor() {
    var showGrid by mutableStateOf(true)
    var pattern by mutableStateOf(VectorCanvasPattern.None)
    var canvasSize by mutableStateOf(IntSize.Zero)
    var editingTextId by mutableStateOf<String?>(null)
        private set
    var textDraft by mutableStateOf("")
    var showLayers by mutableStateOf(false)
    var showClearDialog by mutableStateOf(false)
    var showExitDialog by mutableStateOf(false)
    var showSaveLocationDialog by mutableStateOf(false)
    var pendingPngLocation by mutableStateOf<String?>(null)

    fun beginTextEdit(text: Element.Text) {
        editingTextId = text.id
        textDraft = text.text
    }

    fun beginNewTextEdit(text: Element.Text) {
        if (editingTextId == null) {
            editingTextId = text.id
            textDraft = ""
        }
    }

    fun commitTextEdit(controller: DrawBoxController) {
        val id = editingTextId ?: return
        if (textDraft.isBlank()) {
            controller.onIntent(Intent.DeleteElement(id))
        } else {
            controller.updateText(id, textDraft)
        }
        editingTextId = null
        textDraft = ""
    }

    fun cancelTextEdit() {
        editingTextId = null
        textDraft = ""
    }

    fun clearRuntimePattern() {
        pattern = VectorCanvasPattern.None
    }
}

@Composable
fun rememberVectorCanvasUiState(): VectorCanvasUiState = remember {
    VectorCanvasUiState()
}
