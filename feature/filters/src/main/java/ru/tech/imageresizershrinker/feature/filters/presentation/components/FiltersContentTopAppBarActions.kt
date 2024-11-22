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

package ru.tech.imageresizershrinker.feature.filters.presentation.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Colorize
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.Texture
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.theme.mixedContainer
import ru.tech.imageresizershrinker.core.ui.utils.helper.isPortraitOrientationAsState
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ZoomButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.other.TopAppBarEmoji
import ru.tech.imageresizershrinker.core.ui.widget.saver.ColorSaver
import ru.tech.imageresizershrinker.core.ui.widget.sheets.ZoomModalSheet
import ru.tech.imageresizershrinker.feature.filters.presentation.screenLogic.FiltersComponent
import ru.tech.imageresizershrinker.feature.pick_color.presentation.components.PickColorFromImageSheet

@Composable
internal fun RowScope.FiltersContentTopAppBarActions(
    component: FiltersComponent,
    actions: @Composable RowScope.() -> Unit
) {
    val isPortrait by isPortraitOrientationAsState()

    if (component.previewBitmap != null) {
        var showColorPicker by rememberSaveable { mutableStateOf(false) }
        var tempColor by rememberSaveable(
            showColorPicker,
            stateSaver = ColorSaver
        ) { mutableStateOf(Color.Black) }

        EnhancedIconButton(
            onClick = {
                showColorPicker = true
            },
            enabled = component.previewBitmap != null
        ) {
            Icon(
                imageVector = Icons.Outlined.Colorize,
                contentDescription = stringResource(R.string.pipette)
            )
        }
        PickColorFromImageSheet(
            visible = showColorPicker,
            onDismiss = {
                showColorPicker = false
            },
            bitmap = component.previewBitmap,
            onColorChange = { tempColor = it },
            color = tempColor
        )

        var showZoomSheet by rememberSaveable { mutableStateOf(false) }
        ZoomButton(
            onClick = { showZoomSheet = true },
            visible = component.bitmap != null,
        )
        ZoomModalSheet(
            data = component.previewBitmap,
            visible = showZoomSheet,
            onDismiss = {
                showZoomSheet = false
            }
        )
    }
    if (component.bitmap == null) {
        TopAppBarEmoji()
    } else {
        if (isPortrait) {
            when (component.filterType) {
                is Screen.Filter.Type.Basic -> {
                    EnhancedIconButton(
                        containerColor = MaterialTheme.colorScheme.mixedContainer,
                        onClick = component::showAddFiltersSheet
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AutoFixHigh,
                            contentDescription = stringResource(R.string.add_filter)
                        )
                    }
                }

                is Screen.Filter.Type.Masking -> {
                    EnhancedIconButton(
                        containerColor = MaterialTheme.colorScheme.mixedContainer,
                        onClick = component::showAddFiltersSheet
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Texture,
                            contentDescription = stringResource(R.string.add_mask)
                        )
                    }
                }

                null -> Unit
            }
        } else {
            actions()
        }
    }
}