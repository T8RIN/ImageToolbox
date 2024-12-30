/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.feature.markup_layers.presentation.components.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FormatBold
import androidx.compose.material.icons.rounded.FormatItalic
import androidx.compose.material.icons.rounded.FormatStrikethrough
import androidx.compose.material.icons.rounded.FormatUnderlined
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import ru.tech.imageresizershrinker.feature.markup_layers.domain.DomainTextDecoration
import ru.tech.imageresizershrinker.feature.markup_layers.domain.LayerPosition
import ru.tech.imageresizershrinker.feature.markup_layers.domain.LayerType
import ru.tech.imageresizershrinker.feature.markup_layers.domain.MarkupLayer
import ru.tech.imageresizershrinker.feature.markup_layers.presentation.components.EditBoxState

data class UiMarkupLayer(
    val type: LayerType,
    val state: EditBoxState = EditBoxState(isActive = true)
) {
    fun copy(isActive: Boolean) = UiMarkupLayer(
        type = type,
        state = state.copy(isActive = isActive)
    )
}

fun UiMarkupLayer.asDomain(): MarkupLayer = MarkupLayer(
    type = type,
    position = LayerPosition(
        scale = state.scale,
        rotation = state.rotation,
        offsetX = state.offset.x,
        offsetY = state.offset.y,
        alpha = state.alpha,
        currentCanvasSize = state.canvasSize
    )
)

fun MarkupLayer.asUi(): UiMarkupLayer = UiMarkupLayer(
    type = type,
    state = EditBoxState(
        scale = position.scale,
        rotation = position.rotation,
        offset = Offset(
            x = position.offsetX,
            y = position.offsetY
        ),
        isActive = false,
        canvasSize = position.currentCanvasSize
    )
)

val DomainTextDecoration.icon: ImageVector
    get() = when (this) {
        LayerType.Text.Decoration.Bold -> Icons.Rounded.FormatBold
        LayerType.Text.Decoration.Italic -> Icons.Rounded.FormatItalic
        LayerType.Text.Decoration.Underline -> Icons.Rounded.FormatUnderlined
        LayerType.Text.Decoration.LineThrough -> Icons.Rounded.FormatStrikethrough
    }