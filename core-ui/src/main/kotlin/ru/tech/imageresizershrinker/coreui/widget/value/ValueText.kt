package ru.tech.imageresizershrinker.coreui.widget.value

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.coredomain.utils.trimTrailingZero

@Composable
fun ValueText(
    modifier: Modifier = Modifier.padding(
        top = 8.dp,
        end = 8.dp
    ),
    value: Number,
    enabled: Boolean = true,
    valueSuffix: String = "",
    onClick: () -> Unit
) {
    AnimatedContent(
        targetState = value,
        transitionSpec = { fadeIn(tween(100)) togetherWith fadeOut(tween(100)) }
    ) {
        Text(
            text = "${it.toString().trimTrailingZero()}$valueSuffix",
            color = LocalContentColor.current.copy(0.5f),
            modifier = modifier
                .clip(CircleShape)
                .clickable(enabled = enabled, onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 6.dp),
            lineHeight = 18.sp
        )
    }
}