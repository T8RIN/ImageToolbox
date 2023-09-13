package ru.tech.imageresizershrinker.presentation.draw_screen.components

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import com.gigamole.composeshadowsplus.rsblur.rsBlurShadow
import com.zedalpha.shadowgadgets.compose.clippedShadow

@Composable
fun Modifier.materialShadow(
    shape: Shape,
    elevation: Dp
) = composed {
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

    val api21shadow = Modifier.rsBlurShadow(
        shape = shape,
        radius = elevation
    )
    when {
        isConcavePath && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q -> api21shadow
        else -> api29Shadow
    }
}