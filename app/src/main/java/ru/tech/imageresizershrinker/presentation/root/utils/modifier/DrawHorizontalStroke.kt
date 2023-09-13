package ru.tech.imageresizershrinker.presentation.root.utils.modifier

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isUnspecified
import androidx.compose.ui.zIndex
import com.gigamole.composeshadowsplus.common.ShadowsPlusDefaults
import com.gigamole.composeshadowsplus.rsblur.rsBlurShadow
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

fun Modifier.drawHorizontalStroke(top: Boolean = false, height: Dp = Dp.Unspecified) = composed {
    val borderWidth = LocalSettingsState.current.borderWidth
    val h = if (height.isUnspecified) {
        borderWidth.takeIf { it > 0.dp }
    } else height

    val color = MaterialTheme.colorScheme.outlineVariant(0.3f)


    if (h == null) {
        Modifier
    } else {
        val heightPx = with(LocalDensity.current) { h.toPx() }
        zIndex(100f)
            .drawWithContent {
                drawContent()
                drawRect(
                    color,
                    topLeft = if (top) Offset(0f, 0f) else Offset(0f, this.size.height),
                    size = Size(this.size.width, heightPx)
                )
            }
    }.then(
        if(!top) Modifier.shadow(
            animateDpAsState(if (h == null) 3.dp else 0.dp).value
        ) else Modifier.rsBlurShadow(
            animateDpAsState(if (h == null) 3.dp else 0.dp).value,
            offset = DpOffset(x = 0.dp, y = (-0.5).dp),
            color = ShadowsPlusDefaults.ShadowColor.copy(0.15f)
        )
    ).zIndex(100f)
}