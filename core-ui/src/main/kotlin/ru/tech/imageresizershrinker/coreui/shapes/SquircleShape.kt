package ru.tech.imageresizershrinker.coreui.shapes

import android.graphics.Matrix
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

val SquircleShape: Shape = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val baseWidth = 1000f
        val baseHeight = 1000f

        val path = Path().apply {
            moveTo(0f, 500f)
            cubicTo(0f, 88.25f, 88.25f, 0f, 500f, 0f)
            cubicTo(911.75f, 0f, 1000f, 88.25f, 1000f, 500f)
            cubicTo(1000f, 911.75f, 911.75f, 1000f, 500f, 1000f)
            cubicTo(88.25f, 1000f, 0f, 911.75f, 0f, 500f)
            close()
        }

        return Outline.Generic(
            path
                .asAndroidPath()
                .apply {
                    transform(Matrix().apply {
                        setScale(size.width / baseWidth, size.height / baseHeight)
                    })
                }
                .asComposePath()
        )
    }
}