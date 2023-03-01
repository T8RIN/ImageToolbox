package ru.tech.imageresizershrinker.resize_screen.components

import android.graphics.Bitmap
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.main_screen.components.block
import ru.tech.imageresizershrinker.utils.BitmapUtils.with

@Composable
fun PresetWidget(
    selectedPreset: Int,
    bitmap: Bitmap?,
    bitmapInfo: BitmapInfo,
    onChangeBitmapInfo: (BitmapInfo) -> Unit
) {
    Column(Modifier.block()) {
        Spacer(Modifier.size(8.dp))
        Text(
            stringResource(R.string.presets),
            Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth()) {
            val data =
                remember { List(7) { 100 - it * 10 } }
            Spacer(Modifier.width(4.dp))
            data.forEach {
                val selected = selectedPreset == it
                OutlinedIconButton(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onChangeBitmapInfo(
                            it.with(
                                bitmap,
                                bitmapInfo
                            )
                        )
                    },
                    border = BorderStroke(
                        1.dp, animateColorAsState(
                            if (selected) MaterialTheme.colorScheme.outlineVariant
                            else Color.Transparent
                        ).value
                    ),
                    colors = IconButtonDefaults.outlinedIconButtonColors(
                        containerColor = animateColorAsState(
                            if (selected) MaterialTheme.colorScheme.secondaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ).value,
                        contentColor = animateColorAsState(
                            if (selected) MaterialTheme.colorScheme.onSecondaryContainer
                            else MaterialTheme.colorScheme.onSurface
                        ).value,
                    )
                ) {
                    Text(it.toString())
                }
                Spacer(Modifier.width(4.dp))
            }
        }
    }
}