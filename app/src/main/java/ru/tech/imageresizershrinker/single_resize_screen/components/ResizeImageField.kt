package ru.tech.imageresizershrinker.single_resize_screen.components

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.utils.helper.BitmapUtils.restrict
import ru.tech.imageresizershrinker.utils.modifier.block
import ru.tech.imageresizershrinker.widget.RoundedTextField

@Composable
fun ResizeImageField(
    bitmapInfo: BitmapInfo,
    bitmap: Bitmap?,
    onWidthChange: (Int) -> Unit,
    onHeightChange: (Int) -> Unit
) {
    Row(Modifier.block(shape = RoundedCornerShape(24.dp))) {
        RoundedTextField(
            enabled = bitmap != null,
            value = bitmapInfo.width.takeIf { it != 0 }.let { it ?: "" }.toString(),
            onValueChange = {
                onWidthChange(it.restrict().toIntOrNull() ?: 0)
            },
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            label = {
                Text(
                    stringResource(
                        R.string.width,
                        bitmap?.width?.toString() ?: ""
                    )
                )
            },
            modifier = Modifier
                .weight(1f)
                .padding(
                    start = 8.dp,
                    top = 8.dp,
                    bottom = 8.dp,
                    end = 4.dp
                )
        )
        RoundedTextField(
            enabled = bitmap != null,
            value = bitmapInfo.height.takeIf { it != 0 }.let { it ?: "" }.toString(),
            onValueChange = {
                onHeightChange(it.restrict().toIntOrNull() ?: 0)
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            shape = RoundedCornerShape(12.dp),
            label = {
                Text(
                    stringResource(
                        R.string.height,
                        bitmap?.height?.toString()
                            ?: ""
                    )
                )
            },
            modifier = Modifier
                .weight(1f)
                .padding(
                    start = 4.dp,
                    top = 8.dp,
                    bottom = 8.dp,
                    end = 8.dp
                ),
        )
    }
}