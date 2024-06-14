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

package ru.tech.imageresizershrinker.feature.settings.presentation.components.additional

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FontDownload
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.model.UiFontFamily
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.other.FontSelectionItem
import ru.tech.imageresizershrinker.core.ui.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem

@Composable
fun PickFontFamilySheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    onFontSelected: (UiFontFamily) -> Unit
) {
    SimpleSheet(
        visible = visible,
        onDismiss = {
            if (!it) onDismiss()
        },
        sheetContent = {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Adaptive(250.dp),
                contentPadding = PaddingValues(16.dp),
                verticalItemSpacing = 8.dp,
                horizontalArrangement = Arrangement.spacedBy(
                    alignment = Alignment.CenterHorizontally,
                    space = 8.dp
                )
            ) {
                items(UiFontFamily.entries) { font ->
                    FontSelectionItem(
                        font = font,
                        onClick = {
                            onFontSelected(font)
                        }
                    )
                }
            }
        },
        confirmButton = {
            EnhancedButton(
                onClick = onDismiss
            ) {
                AutoSizeText(stringResource(R.string.close))
            }
        },
        title = {
            TitleItem(
                icon = Icons.Rounded.FontDownload,
                text = stringResource(R.string.font),
            )
        }
    )
}