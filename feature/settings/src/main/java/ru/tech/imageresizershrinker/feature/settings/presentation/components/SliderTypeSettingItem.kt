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
import androidx.compose.material.icons.rounded.Android
import androidx.compose.material.icons.rounded.AutoAwesome
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
import ru.tech.imageresizershrinker.core.resources.icons.MaterialDesign
import ru.tech.imageresizershrinker.core.resources.icons.MiniEdit
import ru.tech.imageresizershrinker.core.resources.icons.Slider
import ru.tech.imageresizershrinker.core.settings.domain.model.SliderType
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.theme.takeColorFromScheme
import ru.tech.imageresizershrinker.core.ui.utils.provider.SafeLocalContainerColor
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedModalBottomSheet
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItem
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem

@Composable
fun SliderTypeSettingItem(
    onValueChange: (SliderType) -> Unit,
    shape: Shape = ContainerShapeDefaults.centerShape,
    modifier: Modifier = Modifier.padding(horizontal = 8.dp)
) {
    val settingsState = LocalSettingsState.current

    var showSheet by rememberSaveable {
        mutableStateOf(false)
    }

    PreferenceItem(
        modifier = modifier,
        title = stringResource(id = R.string.slider_type),
        startIcon = Icons.Rounded.Slider,
        subtitle = settingsState.sliderType.title,
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
                text = stringResource(id = R.string.slider_type),
                icon = Icons.Rounded.Slider
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
                SliderType.entries
            }

            entries.forEachIndexed { index, type ->
                val selected = type == settingsState.sliderType
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

private val SliderType.title: String
    @Composable
    get() = when (this) {
        SliderType.Fancy -> stringResource(R.string.fancy)
        SliderType.Material -> stringResource(R.string.material_2)
        SliderType.MaterialYou -> stringResource(R.string.material_you)
    }

private val SliderType.subtitle: String
    @Composable
    get() = when (this) {
        SliderType.Fancy -> stringResource(R.string.fancy_sub)
        SliderType.Material -> stringResource(R.string.material_2_sub)
        SliderType.MaterialYou -> stringResource(R.string.material_you_slider_sub)
    }


private val SliderType.icon: ImageVector
    get() = when (this) {
        SliderType.Fancy -> Icons.Rounded.AutoAwesome
        SliderType.Material -> Icons.Rounded.Android
        SliderType.MaterialYou -> Icons.Outlined.MaterialDesign
    }