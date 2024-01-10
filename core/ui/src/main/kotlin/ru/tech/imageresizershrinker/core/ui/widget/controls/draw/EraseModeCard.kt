package ru.tech.imageresizershrinker.core.ui.widget.controls.draw

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.icons.material.Eraser
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceRow

@Composable
fun EraseModeCard(
    isRecoveryOn: Boolean,
    onClick: () -> Unit,
) {
    PreferenceRow(
        title = stringResource(id = R.string.erase_mode),
        subtitle = if (!isRecoveryOn) {
            stringResource(id = R.string.erase_background)
        } else stringResource(id = R.string.restore_background),
        color = animateColorAsState(
            if (isRecoveryOn) MaterialTheme.colorScheme.tertiary.copy(0.8f)
            else MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
        ).value,
        contentColor = animateColorAsState(
            if (isRecoveryOn) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurface
        ).value,
        endContent = {
            AnimatedContent(
                targetState = isRecoveryOn,
                transitionSpec = {
                    fadeIn().togetherWith(fadeOut()).using(SizeTransform(false))
                }
            ) {
                if (it) {
                    Icon(Icons.Rounded.Brush, null)
                } else {
                    Icon(Icons.Rounded.Eraser, null)
                }
            }
        },
        onClick = onClick
    )
}

//TODO
@Composable
fun RecoverModeButton(
    selected: Boolean,
    onClick: () -> Unit,
) {
    val containerColor = animateColorAsState(
        if (selected) MaterialTheme.colorScheme.tertiary.copy(0.8f)
        else MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
    ).value

    EnhancedIconButton(
        contentColor = animateColorAsState(
            if (selected) MaterialTheme.colorScheme.onPrimary
            else MaterialTheme.colorScheme.onSurface
        ).value,
        containerColor = containerColor,
        borderColor = MaterialTheme.colorScheme.outlineVariant(0.1f, containerColor),
        onClick = onClick
    ) {
        AnimatedContent(
            targetState = selected,
            transitionSpec = {
                fadeIn().togetherWith(fadeOut()).using(SizeTransform(false))
            }
        ) {
            if (it) {
                Icon(Icons.Rounded.Brush, null)
            } else {
                Icon(Icons.Rounded.Eraser, null)
            }
        }
    }
}