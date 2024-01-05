package ru.tech.imageresizershrinker.coreui.widget.controls

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.domain.model.ImageFormat
import ru.tech.imageresizershrinker.coreui.widget.preferences.PreferenceRowSwitch

@Composable
fun SaveExifWidget(
    checked: Boolean,
    imageFormat: ImageFormat,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
) {
    PreferenceRowSwitch(
        modifier = modifier,
        title = stringResource(R.string.keep_exif),
        subtitle = if (imageFormat.canWriteExif) {
            stringResource(R.string.keep_exif_sub)
        } else {
            stringResource(
                R.string.image_exif_warning,
                imageFormat.title
            )
        },
        checked = checked,
        enabled = imageFormat.canWriteExif,
        changeAlphaWhenDisabled = false,
        shape = RoundedCornerShape(24.dp),
        color = backgroundColor,
        onClick = onCheckedChange,
        startIcon = Icons.Rounded.Fingerprint
    )
}