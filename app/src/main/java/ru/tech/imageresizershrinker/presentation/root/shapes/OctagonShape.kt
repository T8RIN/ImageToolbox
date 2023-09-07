package ru.tech.imageresizershrinker.presentation.root.shapes

import android.graphics.Matrix
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

val OctagonShape: Shape = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val baseWidth = 1000f
        val baseHeight = 1000f

        val path = Path().apply {
            moveTo(500f, 0f)
            lineTo(853.5534f, 146.4466f)
            lineTo(1000f, 500f)
            lineTo(853.5534f, 853.5534f)
            lineTo(500f, 1000f)
            lineTo(146.4466f, 853.5534f)
            lineTo(0f, 500f)
            lineTo(146.4466f, 146.4466f)
            lineTo(500f, 0f)
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