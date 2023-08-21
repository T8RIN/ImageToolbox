package ru.tech.imageresizershrinker.presentation.erase_background_screen.components

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.transparencyChecker(
    colorScheme: ColorScheme = MaterialTheme.colorScheme
) = drawBehind {
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
                color = if (isGrayTile) {
                    colorScheme.surfaceColorAtElevation(20.dp)
                } else colorScheme.surface,
                topLeft = Offset(x * checkerWidth, y * checkerHeight),
                size = Size(checkerWidth, checkerHeight)
            )
        }
    }
}