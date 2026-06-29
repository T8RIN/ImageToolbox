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

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.t8rin.imagetoolbox.core.domain.model.MimeType
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFilePicker
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberImagePicker
import com.t8rin.imagetoolbox.feature.vector_canvas.presentation.components.model.VectorCanvasPickerActions
import com.t8rin.imagetoolbox.feature.vector_canvas.presentation.components.model.VectorCanvasScreenContext
import com.t8rin.imagetoolbox.feature.vector_canvas.presentation.components.model.VectorCanvasUiState

@Composable
internal fun rememberVectorCanvasPickerActions(
    context: VectorCanvasScreenContext
): VectorCanvasPickerActions {
    val imagePicker = rememberImagePicker { uri: Uri ->
        context.component.loadImage(uri) { image ->
            val center = context.state.viewport.screenToWorld(context.uiState.canvasCenter())
            context.controller.insertImage(
                bytes = image.bytes,
                intrinsicSize = Size(image.width.toFloat(), image.height.toFloat()),
                position = center
            )
        }
    }
    val jsonPicker = rememberFilePicker(
        mimeType = MimeType.Json,
        onSuccess = { uri: Uri ->
            context.component.importJson(uri) { json ->
                context.uiState.clearRuntimePattern()
                context.controller.importPath(json)
            }
        }
    )

    return VectorCanvasPickerActions(
        insertImage = imagePicker::pickImage,
        importJson = jsonPicker::pickFile
    )
}

private fun VectorCanvasUiState.canvasCenter() =
    Offset(
        x = canvasSize.width / 2f,
        y = canvasSize.height / 2f
    )
