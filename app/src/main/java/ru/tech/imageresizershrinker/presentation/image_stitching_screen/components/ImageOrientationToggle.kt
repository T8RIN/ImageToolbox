package ru.tech.imageresizershrinker.presentation.image_stitching_screen.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ScreenLockLandscape
import androidx.compose.material.icons.rounded.ScreenLockPortrait
import androidx.compose.material.icons.rounded.ScreenRotation
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.container

@Composable
fun ImageOrientationToggle(
    modifier: Modifier = Modifier,
    selected: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = modifier
            .container(shape = RoundedCornerShape(24.dp), resultPadding = 0.dp)
            .clickable { onCheckedChange(!selected) }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Rounded.ScreenRotation, null, modifier = Modifier.defaultMinSize(24.dp, 24.dp))
        Column(
            Modifier
                .weight(1f)
                .padding(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 8.dp)
        ) {
            Text(
                text = stringResource(R.string.image_orientation),
                fontWeight = FontWeight.Medium
            )
            AnimatedContent(selected) { isHorizontal ->
                Text(
                    text = if (isHorizontal) {
                        stringResource(R.string.horizontal)
                    } else stringResource(R.string.vertical),
                    fontWeight = FontWeight.Normal,
                    color = LocalContentColor.current.copy(0.5f),
                    lineHeight = 12.sp,
                    fontSize = 12.sp
                )
            }
        }
        Switch(
            checked = selected,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                uncheckedBorderColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.onPrimary,
                uncheckedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
            thumbContent = {
                AnimatedContent(selected) { isHorizontal ->
                    Icon(
                        imageVector = if (isHorizontal) {
                            Icons.Rounded.ScreenLockLandscape
                        } else Icons.Rounded.ScreenLockPortrait,
                        contentDescription = null,
                        modifier = Modifier.size(SwitchDefaults.IconSize)
                    )
                }
            }
        )
    }
}