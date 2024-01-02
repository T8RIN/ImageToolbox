package ru.tech.imageresizershrinker.coreui.widget.image

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChangeCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.coreresources.R
import ru.tech.imageresizershrinker.coreui.theme.outlineVariant
import ru.tech.imageresizershrinker.coreui.widget.controls.EnhancedButton
import ru.tech.imageresizershrinker.coreui.widget.controls.EnhancedIconButton
import ru.tech.imageresizershrinker.coreui.widget.modifier.container

@Composable
fun ImageCounter(
    imageCount: Int?,
    onRepick: () -> Unit
) {
    AnimatedVisibility(
        visible = imageCount != null,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .container(shape = CircleShape)
                    .padding(start = 3.dp)
            ) {
                EnhancedButton(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(0.3f),
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer.copy(0.9f),
                    onClick = { if ((imageCount ?: 0) > 1) onRepick() },
                    borderColor = MaterialTheme.colorScheme.outlineVariant(
                        0.1f,
                        MaterialTheme.colorScheme.tertiaryContainer.copy(0.1f),
                    ),
                    isShadowClip = true
                ) {
                    Text(stringResource(R.string.images, imageCount ?: 0L))
                }
                EnhancedIconButton(
                    onClick = { if ((imageCount ?: 0) > 1) onRepick() },
                    borderColor = MaterialTheme.colorScheme.outlineVariant(
                        0.1f,
                        MaterialTheme.colorScheme.tertiaryContainer.copy(0.1f),
                    ),
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(0.3f),
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer.copy(0.9f),
                    isShadowClip = true
                ) {
                    Icon(Icons.Rounded.ChangeCircle, null)
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}