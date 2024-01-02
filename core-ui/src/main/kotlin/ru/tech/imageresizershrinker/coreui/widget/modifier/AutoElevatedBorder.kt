package ru.tech.imageresizershrinker.coreui.widget.modifier

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.border
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isUnspecified
import ru.tech.imageresizershrinker.coreui.widget.modifier.materialShadow
import ru.tech.imageresizershrinker.coreui.theme.outlineVariant
import ru.tech.imageresizershrinker.coreui.theme.suggestContainerColorBy
import ru.tech.imageresizershrinker.coreui.widget.utils.LocalSettingsState

fun Modifier.autoElevatedBorder(
    height: Dp = Dp.Unspecified,
    shape: Shape? = null,
    color: Color = Color.Unspecified,
    autoElevation: Dp = 6.dp
) = composed {
    val h = if (height.isUnspecified) {
        LocalSettingsState.current.borderWidth.takeIf { it > 0.dp }
    } else null

    val shape1 = shape ?: FloatingActionButtonDefaults.shape

    if (h == null) {
        Modifier
    } else {
        border(
            width = h,
            color = if (color.isSpecified) color
            else {
                MaterialTheme.colorScheme.outlineVariant(
                    luminance = 0.3f,
                    onTopOf = MaterialTheme.colorScheme.suggestContainerColorBy(LocalContentColor.current)
                )
            },
            shape = shape1
        )
    }.materialShadow(
        elevation = animateDpAsState(if (h == null) autoElevation else 0.dp).value,
        shape = shape1
    )
}

fun Modifier.containerFabBorder(
    autoElevation: Dp = 1.5.dp,
    shape: Shape? = null
) = autoElevatedBorder(
    autoElevation = autoElevation,
    shape = shape
)