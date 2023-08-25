package ru.tech.imageresizershrinker.presentation.root.widget.controls

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.block

@Composable
fun SaveExifWidget(
    selected: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(shape = RoundedCornerShape(24.dp))
            .clickable { onCheckedChange(!selected) }
            .block(shape = RoundedCornerShape(24.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Rounded.Fingerprint, null, modifier = Modifier.defaultMinSize(24.dp, 24.dp))
        Column(
            Modifier
                .weight(1f)
                .padding(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 8.dp)
        ) {
            Text(
                text = stringResource(R.string.keep_exif),
                fontWeight = FontWeight.Medium
            )
            Text(
                text = stringResource(R.string.keep_exif_sub),
                fontWeight = FontWeight.Normal,
                color = LocalContentColor.current.copy(0.5f),
                lineHeight = 12.sp,
                fontSize = 12.sp
            )
        }
        Switch(
            checked = selected,
            onCheckedChange = onCheckedChange
        )
    }
}