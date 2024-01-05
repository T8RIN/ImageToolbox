package ru.tech.imageresizershrinker.core.ui.widget.preferences

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.utils.ProvideContainerShape

@Composable
fun PreferenceRow(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String? = null,
    color: Color = MaterialTheme.colorScheme.secondaryContainer.copy(
        alpha = 0.2f
    ),
    enabled: Boolean = true,
    shape: Shape = RoundedCornerShape(16.dp),
    contentColor: Color? = null,
    applyHorPadding: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    startContent: (@Composable () -> Unit)? = null,
    endContent: (@Composable () -> Unit)? = null,
    titleFontStyle: TextStyle = LocalTextStyle.current.copy(lineHeight = 18.sp),
    resultModifier: Modifier = Modifier.padding(
        horizontal = if (startContent != null) 0.dp else 16.dp,
        vertical = 8.dp
    ),
    changeAlphaWhenDisabled: Boolean = true,
    autoShadowElevation: Dp = 1.dp,
    onClick: (() -> Unit)?
) {
    val internalColor = contentColor
        ?: if (color == MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)) contentColorFor(
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant
        ) else contentColorFor(backgroundColor = color)
    CompositionLocalProvider(LocalContentColor provides internalColor) {
        Row(
            modifier = modifier
                .then(
                    if (applyHorPadding) {
                        Modifier.padding(horizontal = 16.dp)
                    } else Modifier
                )
                .container(
                    color = color,
                    shape = shape,
                    resultPadding = 0.dp,
                    autoShadowElevation = if (enabled) autoShadowElevation else 0.dp
                )
                .then(
                    onClick
                        ?.takeIf { enabled }
                        ?.let {
                            Modifier.clickable { onClick() }
                        } ?: Modifier
                )
                .then(resultModifier)
                .then(
                    if (changeAlphaWhenDisabled) Modifier.alpha(animateFloatAsState(targetValue = if (enabled) 1f else 0.5f).value)
                    else Modifier
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProvideContainerShape(null) {
                startContent?.invoke()
            }
            Column(modifier = Modifier.weight(1f)) {
                AnimatedContent(
                    targetState = title,
                    transitionSpec = {
                        fadeIn().togetherWith(fadeOut())
                    }
                ) {
                    Text(
                        text = it,
                        maxLines = maxLines,
                        style = titleFontStyle,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                AnimatedContent(
                    targetState = subtitle,
                    transitionSpec = {
                        fadeIn().togetherWith(fadeOut())
                    }
                ) {
                    it?.let {
                        Text(
                            text = it,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            lineHeight = 14.sp,
                            color = LocalContentColor.current.copy(alpha = 0.5f)
                        )
                    }
                }
            }
            Spacer(Modifier.width(8.dp))
            ProvideContainerShape(null) {
                endContent?.invoke()
            }
        }
    }
}