package com.smarttoolfactory.colorpicker.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

/**
 * [Box] that draws checker pattern behind its [content] using [Modifier.drawChecker]
 * @param size size of the checker. If size is [DpSize.Unspecified] **10.dp** is used by
 * default.
 * @param shape shape of the checker.
 * @param content  The content of the Box.
 */
@Composable
private fun CheckeredBox(
    modifier: Modifier,
    shape: Shape,
    size: DpSize = DpSize.Unspecified,
    content: @Composable () -> Unit
) {
    Box(modifier.drawChecker(shape, size)) {
        content()
    }
}

/**
 * Modify element to add checker with appearance specified with a [shape], and [size]
 * and clip it.
 * @param size size of the checker. If size is [DpSize.Unspecified] **10.dp** is used by
 * default.
 * @param shape shape of the checker.
 */
fun Modifier.drawChecker(shape: Shape, size: DpSize = DpSize.Unspecified) = this
    .clip(shape)
    .then(
        drawWithCache {
            this.onDrawBehind {
                val width = this.size.width
                val height = this.size.height

                val checkerWidth =
                    (if (size != DpSize.Unspecified) size.width.toPx() else 10.dp.toPx())
                        .coerceAtMost(width / 2)

                val checkerHeight =
                    (if (size != DpSize.Unspecified) size.height.toPx() else 10.dp.toPx())
                        .coerceAtMost(height / 2)

                val horizontalSteps = (width / checkerWidth).toInt()
                val verticalSteps = (height / checkerHeight).toInt()

                for (y in 0..verticalSteps) {
                    for (x in 0..horizontalSteps) {
                        val isGrayTile = ((x + y) % 2 == 1)
                        drawRect(
                            color = if (isGrayTile) Color.LightGray else Color.White,
                            topLeft = Offset(x * checkerWidth, y * checkerHeight),
                            size = Size(checkerWidth, checkerHeight)
                        )
                    }
                }
            }
        }
    )