package ru.tech.imageresizershrinker.presentation.root.utils.modifier

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.border
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.isUnspecified
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.theme.suggestContainerColorBy
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

fun Modifier.fabBorder(
    height: Dp = Dp.Unspecified,
    shape: Shape? = null,
    elevation: Dp = 8.dp
) = composed {
    val h = if (height.isUnspecified) {
        LocalSettingsState.current.borderWidth.takeIf { it > 0.dp }
    } else null

    val szape = shape ?: FloatingActionButtonDefaults.shape

    if (h == null) {
        Modifier
    } else {
        border(
            h,
            MaterialTheme.colorScheme.outlineVariant(
                luminance = 0.3f,
                onTopOf = MaterialTheme.colorScheme.suggestContainerColorBy(LocalContentColor.current)
            ),
            szape
        )
    }.shadow(
        animateDpAsState(if (h == null) elevation else 0.dp).value,
        szape
    )
}