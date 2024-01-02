package ru.tech.imageresizershrinker.coreui.widget.controls

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
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
import ru.tech.imageresizershrinker.coreui.R
import ru.tech.imageresizershrinker.coredomain.model.ImageInfo
import ru.tech.imageresizershrinker.coredomain.model.IntegerSize
import ru.tech.imageresizershrinker.coreui.utils.helper.ImageUtils.restrict
import ru.tech.imageresizershrinker.coreui.widget.modifier.container
import ru.tech.imageresizershrinker.coreui.widget.text.RoundedTextField

@Composable
fun ResizeImageField(
    imageInfo: ImageInfo,
    originalSize: IntegerSize?,
    onWidthChange: (Int) -> Unit,
    onHeightChange: (Int) -> Unit,
    showWarning: Boolean = false
) {
    Column(
        modifier = Modifier
            .container(shape = RoundedCornerShape(24.dp))
            .animateContentSize()
    ) {
        Row {
            RoundedTextField(
                enabled = originalSize != null,
                value = imageInfo.width.takeIf { it != 0 }.let { it ?: "" }.toString(),
                onValueChange = { value ->
                    onWidthChange(
                        value.restrict().toIntOrNull() ?: 0
                    )
                },
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                label = {
                    Text(
                        stringResource(
                            R.string.width,
                            originalSize?.width?.toString() ?: ""
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
                enabled = originalSize != null,
                value = imageInfo.height.takeIf { it != 0 }.let { it ?: "" }.toString(),
                onValueChange = { value ->
                    onHeightChange(
                        value.restrict().toIntOrNull() ?: 0
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                shape = RoundedCornerShape(12.dp),
                label = {
                    Text(
                        stringResource(
                            R.string.height,
                            originalSize?.height?.toString()
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
        OOMWarning(visible = showWarning)
    }
}