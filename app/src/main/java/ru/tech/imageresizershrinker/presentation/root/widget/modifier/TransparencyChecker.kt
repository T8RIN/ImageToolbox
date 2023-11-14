package ru.tech.imageresizershrinker.presentation.root.widget.modifier

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.transparencyChecker(
    colorScheme: ColorScheme = MaterialTheme.colorScheme,
    checkerWidth: Dp = 10.dp,
    checkerHeight: Dp = 10.dp
) = drawBehind {
    val width = this.size.width
    val height = this.size.height

    val checkerWidthPx = checkerWidth.toPx()
    val checkerHeightPx = checkerHeight.toPx()

    val horizontalSteps = (width / checkerWidthPx).toInt()
    val verticalSteps = (height / checkerHeightPx).toInt()

    for (y in 0..verticalSteps) {
        for (x in 0..horizontalSteps) {
            val isGrayTile = ((x + y) % 2 == 1)
            drawRect(
                color = if (isGrayTile) {
                    colorScheme.surfaceColorAtElevation(20.dp)
                } else colorScheme.surface,
                topLeft = Offset(x * checkerWidthPx, y * checkerHeightPx),
                size = Size(checkerWidthPx, checkerHeightPx)
            )
        }
    }
}