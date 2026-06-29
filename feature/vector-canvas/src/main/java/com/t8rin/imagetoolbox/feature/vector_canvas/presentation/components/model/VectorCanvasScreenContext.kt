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

@file:Suppress("PackageName", "PackageNaming")

package com.t8rin.imagetoolbox.feature.vector_canvas.presentation.components.model

import androidx.compose.ui.graphics.Color
import com.t8rin.imagetoolbox.feature.vector_canvas.presentation.screenLogic.VectorCanvasComponent
import io.ak1.drawbox.domain.model.State
import io.ak1.drawbox.presentation.viewmodel.DrawBoxController

internal data class VectorCanvasScreenContext(
    val component: VectorCanvasComponent,
    val state: State,
    val controller: DrawBoxController,
    val uiState: VectorCanvasUiState
) {
    fun handleBack() {
        if (component.haveChanges) {
            uiState.showExitDialog = true
        } else {
            component.onGoBack()
        }
    }

}

internal data class VectorCanvasInitialColors(
    val background: Color,
    val stroke: Color
)

internal data class VectorCanvasPickerActions(
    val insertImage: () -> Unit,
    val importJson: () -> Unit
)
