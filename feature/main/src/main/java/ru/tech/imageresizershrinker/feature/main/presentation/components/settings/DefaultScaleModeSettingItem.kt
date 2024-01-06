package ru.tech.imageresizershrinker.feature.main.presentation.components.settings

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Numbers
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.core.domain.ImageScaleMode
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.widget.controls.ScaleModeSelector
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalSettingsState

@Composable
fun DefaultScaleModeSettingItem(
    onValueChange: (ImageScaleMode) -> Unit,
    shape: Shape = ContainerShapeDefaults.defaultShape,
    modifier: Modifier = Modifier.padding(horizontal = 8.dp)
) {
    val settingsState = LocalSettingsState.current
    ScaleModeSelector(
        modifier = modifier,
        shape = shape,
        value = settingsState.defaultImageScaleMode,
        onValueChange = onValueChange,
        backgroundColor = MaterialTheme.colorScheme.secondaryContainer.copy(
            alpha = 0.2f
        ),
        title = {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.Numbers, null)
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(R.string.default_value),
                    modifier = Modifier.weight(1f),
                    style = LocalTextStyle.current.copy(lineHeight = 18.sp),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    )
}