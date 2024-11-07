/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package ru.tech.imageresizershrinker.feature.settings.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ToggleOff
import androidx.compose.material.icons.outlined.ToggleOn
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material.icons.rounded.RadioButtonChecked
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.Cube
import ru.tech.imageresizershrinker.core.resources.icons.IOS
import ru.tech.imageresizershrinker.core.resources.icons.MaterialDesign
import ru.tech.imageresizershrinker.core.resources.icons.MiniEdit
import ru.tech.imageresizershrinker.core.resources.icons.Windows
import ru.tech.imageresizershrinker.core.settings.domain.model.SwitchType
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.theme.takeColorFromScheme
import ru.tech.imageresizershrinker.core.ui.utils.provider.SafeLocalContainerColor
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedModalBottomSheet
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItem
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem

@Composable
fun SwitchTypeSettingItem(
    onValueChange: (SwitchType) -> Unit,
    shape: Shape = ContainerShapeDefaults.topShape,
    modifier: Modifier = Modifier.padding(horizontal = 8.dp)
) {
    val settingsState = LocalSettingsState.current

    var showSheet by rememberSaveable {
        mutableStateOf(false)
    }

    PreferenceItem(
        modifier = modifier,
        title = stringResource(id = R.string.switch_type),
        startIcon = Icons.Outlined.ToggleOn,
        subtitle = settingsState.switchType.title,
        onClick = {
            showSheet = true
        },
        shape = shape,
        endIcon = Icons.Rounded.MiniEdit
    )

    EnhancedModalBottomSheet(
        visible = showSheet,
        onDismiss = { showSheet = it },
        title = {
            TitleItem(
                text = stringResource(id = R.string.switch_type),
                icon = Icons.Outlined.ToggleOff
            )
        },
        confirmButton = {
            EnhancedButton(
                onClick = {
                    showSheet = false
                },
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Text(stringResource(R.string.close))
            }
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(8.dp)
        ) {
            val entries = remember {
                SwitchType.entries
            }

            entries.forEachIndexed { index, type ->
                val selected = type == settingsState.switchType
                PreferenceItem(
                    onClick = { onValueChange(type) },
                    title = type.title,
                    subtitle = type.subtitle,
                    color = takeColorFromScheme {
                        if (selected) secondaryContainer
                        else SafeLocalContainerColor
                    },
                    shape = ContainerShapeDefaults.shapeForIndex(index, entries.size),
                    startIcon = type.icon,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = settingsState.borderWidth,
                            color = animateColorAsState(
                                if (selected) MaterialTheme.colorScheme
                                    .onSecondaryContainer
                                    .copy(alpha = 0.5f)
                                else Color.Transparent
                            ).value,
                            shape = ContainerShapeDefaults.shapeForIndex(index, entries.size)
                        ),
                    endIcon = if (selected) {
                        Icons.Rounded.RadioButtonChecked
                    } else Icons.Rounded.RadioButtonUnchecked
                )
            }
        }
    }
}

private val SwitchType.title: String
    @Composable
    get() = when (this) {
        SwitchType.MaterialYou -> stringResource(R.string.material_you)
        SwitchType.Compose -> stringResource(R.string.compose)
        SwitchType.Pixel -> stringResource(R.string.pixel_switch)
        SwitchType.Fluent -> stringResource(R.string.fluent_switch)
        SwitchType.Cupertino -> stringResource(R.string.cupertino_switch)
    }

private val SwitchType.subtitle: String
    @Composable
    get() = when (this) {
        SwitchType.MaterialYou -> stringResource(R.string.material_you_switch_sub)
        SwitchType.Compose -> stringResource(R.string.compose_switch_sub)
        SwitchType.Pixel -> stringResource(R.string.use_pixel_switch_sub)
        SwitchType.Fluent -> stringResource(R.string.fluent_switch_sub)
        SwitchType.Cupertino -> stringResource(R.string.cupertino_switch_sub)
    }


private val SwitchType.icon: ImageVector
    get() = when (this) {
        SwitchType.MaterialYou -> Icons.Outlined.MaterialDesign
        SwitchType.Compose -> Icons.Rounded.Cube
        SwitchType.Pixel -> Icons.Rounded.Android
        SwitchType.Fluent -> Icons.Rounded.Windows
        SwitchType.Cupertino -> Icons.Rounded.IOS
    }