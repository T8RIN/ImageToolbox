package com.smarttoolfactory.colorpicker.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

/**
 * Color display that displays both colors horizontally aligned with half [RoundedCornerShape]
 * with [Modifier.drawChecker] to display checker pattern when any color's alpha is less than 1.0f
 * @param initialColor color that should be static
 * @param currentColor color that is changed based on user actions
 */
@Composable
fun ColorDisplayRoundedRect(
    modifier: Modifier = Modifier,
    initialColor: Color,
    currentColor: Color
) {
    Row(modifier = modifier.height(40.dp)) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .drawChecker(RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp))
                .background(
                    initialColor,
                    shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                )
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .drawChecker(RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp))
                .background(
                    currentColor,
                    shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                )
        )
    }
}

/**
 * Color display that displays both colors horizontally aligned with half [RoundedCornerShape]
 * with [Modifier.drawChecker] to display checker pattern when any color's alpha is less than 1.0f
 * @param boxSize size of left [Box] with [initialBrush] and size of right [Box] with [currentBrush]
 * @param initialBrush [Brush] that should be static
 * @param currentBrush [Brush] that is changed based on user actions
 */
@Composable
fun ColorDisplayRoundedRect(
    modifier: Modifier,
    boxSize: DpSize,
    initialBrush: Brush,
    currentBrush: Brush
) {
    Row {
        Box(
            modifier = Modifier
                .size(boxSize)
                .drawChecker(RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp))
                .background(
                    initialBrush,
                    shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp)
                )
        )
        Box(
            modifier = Modifier
                .size(boxSize)
                .drawChecker(RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp))
                .background(
                    currentBrush,
                    shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp)
                )
        )
    }
}