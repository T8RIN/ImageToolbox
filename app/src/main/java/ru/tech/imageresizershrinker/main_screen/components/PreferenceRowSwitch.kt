package ru.tech.imageresizershrinker.main_screen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.resize_screen.components.blend
import ru.tech.imageresizershrinker.widget.AutoSizeText

@Composable
fun PreferenceRowSwitch(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onClick: (Boolean) -> Unit
) {
    PreferenceRow(
        modifier = modifier,
        title = title,
        subtitle = subtitle,
        onClick = { onClick(!checked) },
        endContent = {
            val thumbIcon: (@Composable () -> Unit)? = if (checked) {
                {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = null,
                        modifier = Modifier.size(SwitchDefaults.IconSize)
                    )
                }
            } else null
            Switch(
                thumbContent = thumbIcon,
                colors = SwitchDefaults.colors(
                    uncheckedBorderColor = MaterialTheme.colorScheme.outline.blend(
                        MaterialTheme.colorScheme.secondaryContainer, 0.5f
                    ),
                    uncheckedThumbColor = MaterialTheme.colorScheme.outline.blend(
                        MaterialTheme.colorScheme.secondaryContainer, 0.4f
                    ),
                    uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                checked = checked,
                onCheckedChange = {
                    onClick(it)
                }
            )
        }
    )
}

@Composable
fun PreferenceRow(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String? = null,
    maxLines: Int = Int.MAX_VALUE,
    startContent: (@Composable () -> Unit)? = null,
    endContent: (@Composable () -> Unit)? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .block(
                color = MaterialTheme.colorScheme.secondaryContainer.copy(
                    alpha = 0.5f
                )
            )
            .padding(horizontal = if (startContent != null) 0.dp else 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        startContent?.invoke()
        Column(modifier = Modifier.weight(1f)) {
            AutoSizeText(text = title, maxLines = maxLines)
            Spacer(modifier = Modifier.height(2.dp))
            subtitle?.let {
                Text(
                    text = it,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 14.sp,
                    color = LocalContentColor.current.copy(alpha = 0.5f)
                )
            }
        }
        Spacer(Modifier.width(8.dp))
        endContent?.invoke()
    }
}