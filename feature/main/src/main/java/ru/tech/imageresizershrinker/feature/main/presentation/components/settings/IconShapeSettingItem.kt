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

package ru.tech.imageresizershrinker.feature.main.presentation.components.settings

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FormatShapes
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.IconShape
import ru.tech.imageresizershrinker.core.settings.presentation.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.shapes.CloverShape
import ru.tech.imageresizershrinker.core.ui.shapes.IconShapesList
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceRow
import ru.tech.imageresizershrinker.core.ui.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem

@Composable
fun IconShapeSettingItem(
    value: Int?,
    onValueChange: (Int) -> Unit,
    shape: Shape = ContainerShapeDefaults.bottomShape,
    modifier: Modifier = Modifier.padding(horizontal = 8.dp)
) {
    var showPickerSheet by rememberSaveable { mutableStateOf(false) }

    PreferenceRow(
        modifier = modifier,
        shape = shape,
        title = stringResource(R.string.icon_shape),
        subtitle = stringResource(R.string.icon_shape_sub),
        onClick = {
            showPickerSheet = true
        },
        startIcon = Icons.Outlined.FormatShapes,
        endContent = {
            Box(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(64.dp)
                    .container(
                        shape = CloverShape,
                        color = MaterialTheme.colorScheme
                            .surfaceVariant
                            .copy(alpha = 0.5f),
                        borderColor = MaterialTheme.colorScheme.outlineVariant(
                            0.2f
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                IconShapePreview()
            }
        }
    )

    SimpleSheet(
        visible = showPickerSheet,
        onDismiss = { showPickerSheet = false },
        title = {
            TitleItem(
                icon = Icons.Outlined.FormatShapes,
                text = stringResource(R.string.icon_shape)
            )
        },
        confirmButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = {
                    showPickerSheet = false
                }
            ) {
                Text(stringResource(R.string.close))
            }
        }
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(55.dp),
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(
                space = 6.dp,
                alignment = Alignment.CenterVertically
            ),
            horizontalArrangement = Arrangement.spacedBy(
                space = 6.dp,
                alignment = Alignment.CenterHorizontally
            ),
            contentPadding = PaddingValues(16.dp)
        ) {
            itemsIndexed(IconShapesList) { index, iconShape ->
                val selected by remember(index, value) {
                    derivedStateOf {
                        index == value
                    }
                }
                val color by animateColorAsState(
                    if (selected) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.secondaryContainer.copy(
                        alpha = 0.2f
                    )
                )
                val borderColor by animateColorAsState(
                    if (selected) {
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(0.7f)
                    } else MaterialTheme.colorScheme.onSecondaryContainer.copy(
                        alpha = 0.1f
                    )
                )
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .container(
                            color = color,
                            shape = CloverShape,
                            borderColor = borderColor,
                            resultPadding = 0.dp
                        )
                        .clickable {
                            onValueChange(index)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    IconShapePreview(iconShape)
                }
            }
            item {
                val selected by remember(value) {
                    derivedStateOf {
                        value == null
                    }
                }
                val color by animateColorAsState(
                    if (selected) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.secondaryContainer.copy(
                        alpha = 0.2f
                    )
                )
                val borderColor by animateColorAsState(
                    if (selected) {
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(0.7f)
                    } else MaterialTheme.colorScheme.onSecondaryContainer.copy(
                        alpha = 0.1f
                    )
                )
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .container(
                            color = color,
                            shape = CloverShape,
                            borderColor = borderColor,
                            resultPadding = 0.dp
                        )
                        .clickable {
                            onValueChange(-1)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    IconShapePreview(iconShape = null)
                }
            }
        }
    }
}

@Composable
private fun IconShapePreview(
    iconShape: IconShape? = LocalSettingsState.current.iconShape
) {
    val color = MaterialTheme.colorScheme.onSurfaceVariant
    when (iconShape) {
        null -> {
            Icon(
                imageVector = Icons.Rounded.Block,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(30.dp)
            )
        }

        IconShape.Random -> {
            Icon(
                imageVector = Icons.Rounded.Shuffle,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(30.dp)
            )
        }

        else -> {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .container(
                        borderWidth = 2.dp,
                        borderColor = color,
                        color = Color.Transparent,
                        shape = iconShape.shape
                    )
            )
        }
    }
}
