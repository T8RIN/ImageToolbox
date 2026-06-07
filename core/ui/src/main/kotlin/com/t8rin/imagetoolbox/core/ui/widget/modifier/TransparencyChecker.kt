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
import androidx.compose.ui.graphics.Color
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
        node.invalidateCache()
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

    private var cache: CheckerCache? = null

    override fun ContentDrawScope.draw() {
        val scheme = colorScheme ?: currentValueOf(LocalMaterialTheme).colorScheme
        val checkerWidthPx = checkerWidth.toPx()
        val checkerHeightPx = checkerHeight.toPx()

        if (checkerWidthPx <= 0f || checkerHeightPx <= 0f) {
            drawContent()
            return
        }

        val surface = scheme.surface
        val elevatedSurface = scheme.surfaceColorAtElevation(20.dp)
        val background = elevatedSurface.blend(surface, 0.5f)
        val currentCache = cache?.takeIf {
            it.size == size &&
                    it.checkerWidthPx == checkerWidthPx &&
                    it.checkerHeightPx == checkerHeightPx &&
                    it.surface == surface &&
                    it.elevatedSurface == elevatedSurface &&
                    it.background == background
        } ?: CheckerCache(
            size = size,
            checkerWidthPx = checkerWidthPx,
            checkerHeightPx = checkerHeightPx,
            horizontalSteps = (size.width / checkerWidthPx).toInt(),
            verticalSteps = (size.height / checkerHeightPx).toInt(),
            surface = surface,
            elevatedSurface = elevatedSurface,
            background = background
        ).also {
            cache = it
        }

        drawRect(currentCache.background)

        for (y in 0..currentCache.verticalSteps) {
            for (x in 0..currentCache.horizontalSteps) {
                drawRect(
                    color = if ((x + y) % 2 == 1) {
                        currentCache.elevatedSurface
                    } else {
                        currentCache.surface
                    },
                    topLeft = Offset(
                        x = x * currentCache.checkerWidthPx,
                        y = y * currentCache.checkerHeightPx
                    ),
                    size = Size(
                        width = currentCache.checkerWidthPx,
                        height = currentCache.checkerHeightPx
                    )
                )
            }
        }

        drawContent()
    }

    fun invalidateCache() {
        cache = null
        invalidateDraw()
    }
}

private data class CheckerCache(
    val size: Size,
    val checkerWidthPx: Float,
    val checkerHeightPx: Float,
    val horizontalSteps: Int,
    val verticalSteps: Int,
    val surface: Color,
    val elevatedSurface: Color,
    val background: Color
)