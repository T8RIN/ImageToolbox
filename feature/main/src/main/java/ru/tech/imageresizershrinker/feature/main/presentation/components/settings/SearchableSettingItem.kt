package ru.tech.imageresizershrinker.feature.main.presentation.components.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.utils.ProvideContainerShape
import ru.tech.imageresizershrinker.feature.main.presentation.viewModel.MainViewModel

@Composable
fun SearchableSettingItem(
    modifier: Modifier = Modifier,
    group: SettingsGroup,
    setting: Setting,
    shape: Shape,
    viewModel: MainViewModel
) {
    Column(
        modifier = modifier.container(resultPadding = 0.dp, shape = shape)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = group.icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(text = stringResource(id = group.titleId), fontSize = 12.sp)
        }
        val itemShape = when (setting) {
            is Setting.ImagePickerMode -> null
            is Setting.NightMode -> null
            else -> RoundedCornerShape(12.dp)
        }
        ProvideContainerShape(itemShape) {
            SettingItem(
                setting = setting,
                viewModel = viewModel
            )
        }
        Spacer(Modifier.height(8.dp))
    }
}