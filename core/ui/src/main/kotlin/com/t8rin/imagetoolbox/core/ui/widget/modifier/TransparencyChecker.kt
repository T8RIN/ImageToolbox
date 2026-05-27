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

package com.t8rin.imagetoolbox.core.ui.widget.modifier

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme.LocalMaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.ui.theme.blend

fun Modifier.transparencyChecker(
    colorScheme: ColorScheme? = null,
    checkerWidth: Dp = 10.dp,
    checkerHeight: Dp = 10.dp,
): Modifier = this.then(
    TransparencyCheckerElement(
        colorScheme = colorScheme,
        checkerWidth = checkerWidth,
        checkerHeight = checkerHeight
    )
)

private data class TransparencyCheckerElement(
    val colorScheme: ColorScheme?,
    val checkerWidth: Dp,
    val checkerHeight: Dp,
) : ModifierNodeElement<TransparencyCheckerNode>() {

    override fun create(): TransparencyCheckerNode {
        return TransparencyCheckerNode(
            colorScheme = colorScheme,
            checkerWidth = checkerWidth,
            checkerHeight = checkerHeight
        )
    }

    override fun update(node: TransparencyCheckerNode) {
        node.colorScheme = colorScheme
        node.checkerWidth = checkerWidth
        node.checkerHeight = checkerHeight
        node.invalidateDraw()
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "transparencyChecker"
        properties["colorScheme"] = colorScheme
        properties["checkerWidth"] = checkerWidth
        properties["checkerHeight"] = checkerHeight
    }
}

private class TransparencyCheckerNode(
    var colorScheme: ColorScheme?,
    var checkerWidth: Dp,
    var checkerHeight: Dp,
) : Modifier.Node(),
    DrawModifierNode,
    CompositionLocalConsumerModifierNode {

    override fun ContentDrawScope.draw() {
        val scheme = colorScheme ?: currentValueOf(LocalMaterialTheme).colorScheme

        val checkerWidthPx = checkerWidth.toPx()
        val checkerHeightPx = checkerHeight.toPx()

        if (checkerWidthPx <= 0f || checkerHeightPx <= 0f) {
            drawContent()
            return
        }

        val horizontalSteps = (size.width / checkerWidthPx).toInt()
        val verticalSteps = (size.height / checkerHeightPx).toInt()

        val surface = scheme.surface
        val elevatedSurface = scheme.surfaceColorAtElevation(20.dp)
        val background = elevatedSurface.blend(surface, 0.5f)

        drawRect(background)

        for (y in 0..verticalSteps) {
            for (x in 0..horizontalSteps) {
                drawRect(
                    color = if ((x + y) % 2 == 1) {
                        elevatedSurface
                    } else {
                        surface
                    },
                    topLeft = Offset(
                        x = x * checkerWidthPx,
                        y = y * checkerHeightPx
                    ),
                    size = Size(
                        width = checkerWidthPx,
                        height = checkerHeightPx
                    )
                )
            }
        }

        drawContent()
    }
}