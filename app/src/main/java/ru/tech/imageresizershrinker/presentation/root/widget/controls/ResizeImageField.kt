package ru.tech.imageresizershrinker.presentation.root.widget.controls

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.R
import ru.tech.imageresizershrinker.core.android.ImageUtils.restrict
import ru.tech.imageresizershrinker.domain.model.ImageInfo
import ru.tech.imageresizershrinker.presentation.root.utils.modifier.block
import ru.tech.imageresizershrinker.presentation.root.widget.text.RoundedTextField
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalSettingsState

@Composable
fun ResizeImageField(
    imageInfo: ImageInfo,
    bitmap: Bitmap?,
    onWidthChange: (Int) -> Unit,
    onHeightChange: (Int) -> Unit,
    showWarning: Boolean = false
) {
    Column(
        modifier = Modifier
            .block(shape = RoundedCornerShape(24.dp))
            .animateContentSize()
    ) {
        Row {
            RoundedTextField(
                enabled = bitmap != null,
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
        AnimatedVisibility(visible = showWarning) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(
                        alpha = 0.7f
                    ),
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                modifier = Modifier
                    .padding(8.dp)
                    .border(
                        width = LocalSettingsState.current.borderWidth,
                        color = MaterialTheme.colorScheme.onErrorContainer.copy(0.4f),
                        shape = RoundedCornerShape(16.dp)
                    ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.image_size_warning),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(8.dp),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 14.sp,
                    color = LocalContentColor.current.copy(alpha = 0.5f)
                )
            }
        }
    }
}