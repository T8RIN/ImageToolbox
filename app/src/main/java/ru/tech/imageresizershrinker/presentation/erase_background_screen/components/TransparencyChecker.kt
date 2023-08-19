package ru.tech.imageresizershrinker.presentation.erase_background_screen.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.transparencyChecker() = drawBehind {
    val width = this.size.width
    val height = this.size.height

    val checkerWidth = 10.dp.toPx()
    val checkerHeight = 10.dp.toPx()

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
