package ru.tech.imageresizershrinker.presentation.draw_screen.components

import android.graphics.BlurMaskFilter
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePaint
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.zedalpha.shadowgadgets.compose.clippedShadow

@Composable
fun Modifier.materialShadow(
    shape: Shape,
    elevation: Dp
) = composed {
    val density = LocalDensity.current

    val isConcavePath by remember(shape) {
        derivedStateOf {
            shape.createOutline(
                size = Size(1f, 1f),
                layoutDirection = LayoutDirection.Ltr,
                density = Density(1f)
            ).let {
                it is Outline.Generic && !it.path.isConvex
            }
        }
    }

    val api29Shadow = Modifier.clippedShadow(
        shape = shape,
        elevation = elevation
    )

    val api21shadow = Modifier.drawWithCache {
        val outline = shape.createOutline(
            size.copy(
                width = size.width + 1.dp.toPx(),
                height = size.width + 1.dp.toPx()
            ),
            layoutDirection,
            density
        )

        val paint = Paint().asFrameworkPaint().apply {
            this.color = Color.Black.copy(alpha = .2f).toArgb()
            setShadowLayer(
                elevation.toPx(),
                0f,
                0f,
                Color.Black
                    .copy(alpha = .2f)
                    .toArgb()
            )
            maskFilter = BlurMaskFilter(elevation.toPx(), BlurMaskFilter.Blur.NORMAL)
        }.asComposePaint()

        onDrawBehind {
            drawIntoCanvas { canvas ->
                inset(left = 0f, top = 0f, right = 0f, bottom = 0f) {
                    canvas.drawOutline(
                        outline = outline,
                        paint = paint
                    )
                }
            }
        }
    }

    when {
        isConcavePath && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q -> api21shadow
        else -> api29Shadow
    }
}