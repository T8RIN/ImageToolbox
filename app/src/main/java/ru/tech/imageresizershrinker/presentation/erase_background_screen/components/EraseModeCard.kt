package ru.tech.imageresizershrinker.presentation.erase_background_screen.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.root.icons.material.Eraser
import ru.tech.imageresizershrinker.presentation.root.theme.outlineVariant
import ru.tech.imageresizershrinker.presentation.root.widget.preferences.PreferenceRow
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

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

@Composable
fun EraseModeButton(
    isRecoveryOn: Boolean,
    onClick: () -> Unit,
) {
    val containerColor = animateColorAsState(
        if (isRecoveryOn) MaterialTheme.colorScheme.tertiary.copy(0.8f)
        else MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
    ).value
    OutlinedIconButton(
        colors = IconButtonDefaults.filledIconButtonColors(
            contentColor = animateColorAsState(
                if (isRecoveryOn) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurface
            ).value,
            containerColor = containerColor
        ),
        border = BorderStroke(
            width = LocalSettingsState.current.borderWidth,
            color = MaterialTheme.colorScheme.outlineVariant(0.1f, containerColor)
        ),
        onClick = onClick
    ) {
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
    }
}