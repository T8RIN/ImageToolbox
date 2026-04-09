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

import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.t8rin.imagetoolbox.feature.markup_layers.domain.LayerType
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.model.UiMarkupLayer
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.screenLogic.MarkupLayersComponent

@Composable
internal fun BoxWithConstraintsScope.Layer(
    component: MarkupLayersComponent,
    layer: UiMarkupLayer,
    onActivate: () -> Unit,
    onShowContextOptions: () -> Unit,
    onUpdateLayer: (UiMarkupLayer, Boolean) -> Unit
) {
    val type = layer.type

    EditBox(
        state = layer.state,
        cornerRadiusPercent = layer.cornerRadiusPercent,
        blendingMode = layer.blendingMode,
        onTap = {
            if (layer.state.isActive) {
                layer.state.isInEditMode = true
            } else {
                onActivate()
            }
        },
//        onLongTap = {
//            TODO: Works Bad
//            if (!layer.state.isActive) {
//                onActivate()
//            }
//            onShowContextOptions()
//        },
        content = {
            LayerContent(
                modifier = Modifier.sizeIn(
                    maxWidth = this@Layer.maxWidth / 2,
                    maxHeight = this@Layer.maxHeight / 2
                ),
                type = type,
                textFullSize = this@Layer.constraints.run { minOf(maxWidth, maxHeight) },
                onTextLayout = if (layer.type is LayerType.Text) {
                    { result ->
                        val visibleLineCount = if (result.didOverflowHeight) {
                            (0 until result.lineCount).count { lineIndex ->
                                result.getLineBottom(lineIndex) <= result.size.height
                            }
                        } else {
                            result.lineCount
                        }

                        if (visibleLineCount > 0 && layer.visibleLineCount != visibleLineCount) {
                            onUpdateLayer(
                                layer.copy(visibleLineCount = visibleLineCount),
                                false
                            )
                        }
                    }
                } else null
            )
        }
    )

    EditLayerSheet(
        component = component,
        visible = layer.state.isInEditMode,
        onDismiss = { layer.state.isInEditMode = it },
        onUpdateLayer = onUpdateLayer,
        layer = layer
    )
}