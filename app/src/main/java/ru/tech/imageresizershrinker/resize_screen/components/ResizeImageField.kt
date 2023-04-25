package ru.tech.imageresizershrinker.resize_screen.components

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
import ru.tech.imageresizershrinker.main_screen.components.block
import ru.tech.imageresizershrinker.resize_screen.viewModel.SingleResizeViewModel.Companion.restrict
import ru.tech.imageresizershrinker.widget.RoundedTextField

@Composable
fun ResizeImageField(
    bitmapInfo: BitmapInfo,
    bitmap: Bitmap?,
    onWidthChange: (String) -> Unit,
    onHeightChange: (String) -> Unit
) {
    Row(Modifier.block()) {
        RoundedTextField(
            enabled = bitmap != null,
            value = bitmapInfo.width,
            onValueChange = {
                onWidthChange(it.restrict())
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
            value = bitmapInfo.height,
            onValueChange = {
                onHeightChange(it.restrict())
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