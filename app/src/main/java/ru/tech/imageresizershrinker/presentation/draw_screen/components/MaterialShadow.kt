package ru.tech.imageresizershrinker.presentation.draw_screen.components

import android.os.Build
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.gigamole.composeshadowsplus.rsblur.rsBlurShadow
import com.zedalpha.shadowgadgets.compose.clippedShadow
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

@Composable
fun Modifier.materialShadow(
    shape: Shape,
    elevation: Dp,
    enabled: Boolean = LocalSettingsState.current.allowShowingShadowsInsteadOfBorders,
    isClipped: Boolean = true
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
    val elev = animateDpAsState(if (enabled) elevation else 0.dp).value

    val api29Shadow = if (isClipped) {
        Modifier.clippedShadow(
            shape = shape,
            elevation = elev
        )
    } else {
        Modifier.shadow(
            shape = shape,
            elevation = elev
        )
    }

    val api21shadow = Modifier.rsBlurShadow(
        shape = shape,
        radius = elev,
        isAlphaContentClip = isClipped
    )
    when {
        isConcavePath && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q -> api21shadow
        else -> api29Shadow
    }
}