package ru.tech.imageresizershrinker.presentation.root.utils.helper

import android.graphics.Matrix
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import ru.tech.imageresizershrinker.domain.model.IntegerSize
import kotlin.math.cos
import kotlin.math.sin

fun Path.scaleToFitCanvas(
    currentSize: IntegerSize,
    oldSize: IntegerSize,
    onGetScale: (Float, Float) -> Unit = { _, _ -> }
): Path {
    val sx = currentSize.width.toFloat() / oldSize.width
    val sy = currentSize.height.toFloat() / oldSize.height
    onGetScale(sx, sy)
    return android.graphics.Path(this.asAndroidPath()).apply {
        transform(
            Matrix().apply {
                setScale(sx, sy)
            }
        )
    }.asComposePath()
}

fun Offset.rotateVector(
    angle: Double
): Offset = Offset(
    x = (x * cos(Math.toRadians(angle)) - y * sin(Math.toRadians(angle))).toFloat(),
    y = (x * sin(Math.toRadians(angle)) + y * cos(Math.toRadians(angle))).toFloat()
)